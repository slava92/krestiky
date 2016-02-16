(ns noliky.Chase
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as G]
            [noliky.GameSpace :as GS]
            [noliky.FullSpace :as FS]
            [noliky.Blind :as BS]
            [noliky.MoveResult :as M]
            [noliky.Player :as PL]
            [noliky.Position :as P]
            [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s
                      :include-macros true])
            #?(:clj [clojure.pprint :as p])
            [clojure.string :as str]
            [clojure.set :as set]))

(def out->winner first)
(def out->bset second)
(def cell->pos first)
(def cell->player second)
(def out->1st-pos (comp cell->pos first out->bset))

(s/defn game-space :- (s/pair [FS/Outcome] "outcomes"
                              [FS/Outcome] "deltas")
  [board :- FS/BoardSet
   space :- [FS/Outcome]]
  (let [outcomes (filter (s/fn [[_ mvs] :- FS/Outcome]
                           (set/subset? board mvs))
                         space)
        deltas (map (s/fn [[plr mvs] :- FS/Outcome]
                      (vector plr (set/difference mvs board))) outcomes)]
    (vector outcomes deltas)))

(s/defn get-moves :- [T/PositionType]
  [player :- T/PlayerType
   [_ board] :- FS/Outcome]
  (map cell->pos
       (filter #(= player (cell->player %)) board)))

(s/defn select-best :- [(s/pair T/PositionType "position"
                                FS/Outcome "outcome")]
  [player :- T/PlayerType
   replies :- [(s/pair T/PositionType "position"
                       FS/Outcome "outcome")]]
  (let [outcomes (group-by #(out->winner (second %)) replies)
        wins (get outcomes player)
        draws (get outcomes PL/Nobody)
        lost (get outcomes (PL/alternate player))]
    (cond (not-empty wins) wins
          (not-empty draws) draws
          (not-empty lost) lost
          :else (T/error "dead end"))))

(s/defn win-spots :- [(s/pair T/PositionType "position"
                              FS/Outcome "outcome")]
  [player :- T/PlayerType
   deltas]
  (let [wins (filter
              (fn [[winner board]]
                (and (= 1 (count board))
                     (= player winner)))
              deltas)]
    (if (not-empty wins)
      (map vector (map #(out->1st-pos %) wins) wins)
      (vector))))

(declare other-spots)
(s/defn one-spot :- (s/pair T/PositionType "position"
                            FS/Outcome "outcome")
  [[player board] :- FS/Snapshot
   position :- T/PositionType
   space]
  (let [board' (conj board [position player])
        player' (PL/alternate player)
        snapshot' [player' board']
        [space' deltas] (game-space board' space)
        wins (win-spots player' deltas)]
    (if (not-empty wins)
      (vector position [(out->winner (second (first wins))) board'])
      (let [outcomes' (other-spots snapshot' space' deltas)]
        (vector position [(out->winner (second (first outcomes'))) board'])))))

(s/defn other-spots :- [(s/pair T/PositionType "position"
                                FS/Outcome "outcome")]
  [[player _ :as sshot] :- FS/Snapshot
   space
   deltas]
  (let [grouped (group-by #(= 1 (count (second %))) deltas)
        last-moves (get grouped true)
        keep-play (get grouped false)
        last-moves' (map #(vector (out->1st-pos %) %) last-moves)
        moves (set (mapcat #(get-moves player %) keep-play))
        replies (map #(one-spot sshot % space) moves)]
    (select-best player (concat last-moves' replies))))

(s/defn all-spots :- [(s/pair T/PositionType "position"
                              FS/Outcome "outcome")]
  [[player board :as sshot] :- FS/Snapshot]
  (let [[space deltas] (game-space board FS/boards)
        wins (win-spots player deltas)]
    (if (not-empty wins)
      wins
      (other-spots sshot space deltas))))

(defn snapshot
  [board]
  (vector (BL/whoseTurn board) (-> board :positions vec set)))

(s/defn next-spot :- T/PositionType
  [board :- T/BoardType]
  (let [sshot (snapshot board)
        spots (all-spots sshot)]
    (rand-nth (map first spots))))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (.first-move BS/deep-thought))

    ;; this -> Board -> Position
    (next-move [this board]
      (next-spot board))))

;;;;;;;;;;;; test ;;;;;;;;;;;;;;;;;

(def b1 (.first-move deep-thought))
(def b1s (snapshot b1))

(defn tryit []
  (->> (B/empty-board)
       (B/--> P/NE)
       (B/--> P/C)
       (B/--> P/NW)
       (B/--> P/N)
       ))
(def b2 (:board (tryit)))
(def b2s (snapshot b2))

(defn tst [b]
  #?(:clj (println (BL/showBoard b)))
  (time (.next-move deep-thought b)))

(defn t2
  ([] (t2 false))
  ([validate]
   (s/set-fn-validation! validate)
   (next-spot b2)))

(s/defn show-outcome :- s/Str
  [[player moves] :- FS/Outcome]
  (str
   (->> moves
        seq
        (map (s/fn [[pos plr] :- FS/Cell]
               (str (get P/pos->idx (:pos pos)) (PL/toSymbol plr))))
        sort
        (apply str))
   " " (PL/toSymbol player)))

(defn move [s1 s2 board]
  (let [pos (.next-move s1 board)]
    (M/foldMoveResult
     nil
     #(move s2 s1 %)
     identity
     (B/--> pos board))))

(defn play [s1 s2]
  (let [b0 (T/first-move s1)]
    (G/playerGameResult
     "Alice"
     "Bob"
     "Draw"
     (:gr (move s2 s1 b0)))))

(defn tourney [s1 s2]
  (map (fn [i] (play s1 s2))
       (range)))
