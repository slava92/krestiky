(ns noliky.Chase
  (:require [noliky.Blind :as S]
            [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.GameResult :as G]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            [clojure.set :refer [difference]]))

(defn weigh [strategy move-result]
  (M/foldMoveResult
   nil
   #(assoc
     %1 :weight 0)
   #(assoc
     %1 :weight (G/gameResult 1 -1 0 %1))
   move-result))

;; (defn win? [move-result]
;;   (M/foldMoveResult
;;    nil ;; occupied
;;    (constantly nil) ;; keep playing
;;    (fn [finished-board] ;; is it a win  for current player?
;;      (let [myself (BL/whoseNotTurn finished-board)]
;;        (G/gameResult #(= myself %) false (B/getResult finished-board))))
;;    move-result))
;;        ;; winner == myself -> win
;;        ;; draw -> draw
;;        ;; loss

(def chaser-moves
  (reify T/strategy

    (first-move [this]
      (T/first-move S/random-moves))

    (next-move [this board]
      (let [attempts (map #(B/--> % board) P/positions)
            weighted (filter some? (map #(weigh this %) attempts))
            best-board (first (sort-by :weight weighted))]
        (clojure.pprint/pprint best-board)
        (ffirst (:moves best-board))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def b1 (T/first-move chaser-moves))
