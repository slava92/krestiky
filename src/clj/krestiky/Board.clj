(ns krestiky.Board
  (:require [krestiky.Types :refer :all]
            [krestiky.BoardLike :as BL]
            [krestiky.FinishedBoard :as FB]
            [krestiky.MoveResult :as MR]
            [krestiky.Position :as Pos]
            [krestiky.TakenBack :as TB])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defn is-draw [board :- Board] :- boolean
  (= (count (BL/occupied board))
     (count Pos/values)))

(t/ann winners (t/Coll (t/Coll Position)))
(def winners
  [[Pos/NW Pos/N Pos/NE]
   [Pos/W  Pos/C Pos/E]
   [Pos/SW Pos/S Pos/SE]
   [Pos/NW Pos/W Pos/SW]
   [Pos/N  Pos/C Pos/S]
   [Pos/NE Pos/E Pos/SE]
   [Pos/NW Pos/C Pos/SE]
   [Pos/SW Pos/C Pos/NE]])

(t/defn maybe-fold
  [e :- (t/Option Player) p :- (t/Option Player)] :- (t/Option Player)
  (when (and (not= e nil) (= e p)) e))

(t/defn pos->plrs [board :- Board diag :- (t/Coll Position)] :- (t/Coll (t/Option Player))
  (map (t/fn [pos :- Position] :- (t/Option Player)
         (BL/player-at board pos)) diag))

(t/defn game-over? [board :- Board] :- boolean
  (or (is-draw board) (got-winner board)))

(t/ann-record board-type [next-move :- Player
                          pos-map :- (t/Map t/AnyInteger Player)
                          n-moves :- t/AnyInteger
                          before :- (t/Option board-type)])
(defrecord board-type [next-move pos-map n-moves before]
  Board
  (move-to [board pos]
    (t/ann-form board Board)
    (t/ann-form board board-type)
    (t/ann-form pos Position)
    (if (contains? pos-map (to-int pos))
      (MR/mk-already-occupied)
      (let [new-board (->board-type
                       (alternate (BL/whose-not-turn board))
                       (assoc pos-map (to-int pos) (BL/whose-turn board))
                       (inc (BL/nmoves board))
                       board)]
        (if (game-over? new-board)
          (MR/mk-game-over (FB/->finished-board-type new-board))
          (MR/mk-keep-playing new-board)))))
  (got-winner [board]
    (let [full-row?
          (t/fn [diag :- (t/Coll Position)] :- boolean
            (let [pls (pos->plrs board diag)
                  same? (distinct pls)]
              (and (= 1 (count same?)) (not= nil (first same?)))))
          outcomes (some true? (map full-row? winners))]
      (not= nil outcomes)))
  Started
  (take-back [board]
    (if (nil? board) (TB/mk-is-empty) (TB/mk-is-board board)))
  Show
  (to-string [board] (BL/as-string board BL/simple-chars)))

(defmethod BL/empty-board? board-type [board] false)

(defmethod BL/nmoves board-type [{:keys [n-moves]}] n-moves)

(defmethod BL/occupied board-type [{:keys [pos-map]}]
  (t/ann-form pos-map (t/Map t/AnyInteger Player))
  (map (t/fn [pos :- t/AnyInteger] :- Position (Pos/from-int pos))
       (keys pos-map)))

(defmethod BL/player-at board-type [{:keys [pos-map]} pos]
          (->> pos to-int (get pos-map)))

(defmethod BL/whose-turn board-type [{:keys [next-move]}] next-move)
