(ns hrest.Strategy
  (:require [hrest.Board :as B]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as G]
            [hrest.Player :as Pl]
            [hrest.Position :as P]
            [hrest.MoveResult :as M]
            [hrest.Types :as T]
            [clojure.set :refer [difference]])
  (:import [hrest.Types EmptyBoard]))

(defn availablePositions [board]
  (seq
   (difference
    (set P/positions)
    (set (BL/occupiedPositions board)))))

(def random-moves
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (let [pos (rand-nth P/positions)]
        (M/keepPlaying (B/--> pos (T/->EmptyBoard)))))

    ;; this -> Board -> Position
    (next-move [this board]
      (rand-nth (availablePositions board)))))

(defn game []
  (doseq [pos P/positions]
    (prn pos)))

(declare move)
(defn play [board]
  (doseq [pos (availablePositions board)]
    (move pos board)))

(defn show-outcome [{{moves :moves, positions :positions} :b, result :gr}]
  (let [ps (sort-by first
                    (map
                     #(vector (P/pos-to-char (key %1)) (Pl/toSymbol (val %1)))
                     positions))]
    (print (apply str (apply concat ps)))
    (println (str " " (G/playerGameResult "X" "O" "="  result)))))

(defn move [pos board]
  (let [mr (B/--> pos board)]
    (M/foldMoveResult
     #(throw (Exception. (format "Error in game design: '%s' is occupied" (T/show pos))))
     #(play %) ;; Board -> MoveResult
     show-outcome
     mr)))

(defn -main [& args]
  (play (T/->EmptyBoard)))
