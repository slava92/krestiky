(ns noliky.Space
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as G]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [noliky.GameSpace :refer [moves]]
            [noliky.Chase :as Ch]))

;; moves :: String -> [Postion]

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (M/keepPlaying
       (B/-->
        (rand-nth (get moves "00"))
        (B/empty-board))))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [bl (BL/showBlock board)
            mvs (get moves bl)]
        (rand-nth mvs)))))

;;;;;;;;;;;; test ;;;;;;;;;;;;;;;
(def b1 (T/first-move deep-thought))
(def b2 (T/next-move deep-thought b1))

(defn move [s1 s2 board]
  (let [pos (T/next-move s1 board)]
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
