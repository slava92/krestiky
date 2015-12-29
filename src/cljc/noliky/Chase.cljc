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

(s/defn game-space :- [(s/pair T/PlayerType "winner" #{T/MoveType} "board")]
  [board :- T/BoardType]
  (let [moves (:moves board)]
    (map
     (fn [[plr mvs]] [plr (set/difference mvs moves)])
     (filter (s/fn [[_ mvs] :- (s/pair T/PlayerType "winner" #{T/MoveType} "board")]
               (set/subset? moves mvs))
             FS/boards))))

(defn select-one [player games]
  (->> games
       (rand-nth)
       (second)
       (filter #(= player (second %)))
       (rand-nth)
       (first)))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (T/first-move BS/deep-thought))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [gs (game-space board)
            player (BL/whoseTurn board)
            player2 (BL/whoseNotTurn board)
            wins (filter #(= player (first %)) gs)
            one-move (filter #(= 1 (count (second %))) wins)
            losses (filter #(= player2 (first %)) gs)
            two-moves (filter #(= 2 (count (second %))) losses)
            draws (filter #(= PL/Nobody (first %)) gs)
            ]
        ;; #?(:clj (p/pprint (sort-by (comp count second) two-moves)))
        (cond
          (seq one-move) (select-one player one-move)
          (seq two-moves) (let [avoid
                                (->> two-moves
                                     (mapcat second)
                                     (filter #(= player (second %)))
                                     (map first)
                                     (into #{}))
                                possible
                                (->> gs
                                     (mapcat second)
                                     (filter #(= player (second %)))
                                     (map first)
                                     (into #{}))
                                available (set/difference possible avoid)]
                            (rand-nth (seq available)))
          (seq wins) (select-one player wins)
          (seq draws) (select-one player draws)
          (seq losses) (select-one player losses)
          :else (T/abstract (str "corrupted game-space: "
                                 (pr-str gs))))))))

;;;;;;;;;;;; test ;;;;;;;;;;;;;;;;;
;; (defn tst []
;;   (let [some (T/next-move deep-thought (T/first-move deep-thought))]
;;     some))

(def b1 (T/first-move deep-thought))

(defn tryit []
  (->> (B/empty-board)
       (B/--> P/C)
       (B/--> P/N)
       (B/--> P/NW)
       ))
(def b2 (:board (tryit)))

(defn tst []
  #?(:clj (println (BL/showBoard b2)))
  (T/next-move deep-thought b2)
  )
