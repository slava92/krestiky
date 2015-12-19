(ns noliky.Chase
  (:require [noliky.Blind :as S]
            [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [noliky.GameResult :as G]
            [noliky.MoveResult :as M]
            [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [clojure.set :refer [difference]]))

(s/defn nstep :- T/FinishedBoardType
  [board :- T/BoardType]
  (let [player (BL/whoseTurn board)
        ;; _ (s/validate T/PlayerType player)
        attempts (map #(B/--> % board) P/positions)
        ;; _ (s/validate [T/MoveResultType] attempts)
        ;; results :- [FinishedBoardType]
        finished-boards (filter
                         some?
                         (map
                          #(M/foldMoveResult nil (constantly nil) identity %)
                          attempts))
        ;; _ (s/validate [T/FinishedBoardType] finished-boards)
        wins (when (seq finished-boards)
               (filter #(= player (get-in % [:gr :player])) finished-boards))
        ;; _ (s/validate [T/FinishedBoardType] wins)
        ]
    (if (seq wins)
      (first wins)
      (let [fbs (filter
                 some?
                 (map
                  #(M/foldMoveResult nil nstep identity %)
                  attempts))
            wins' (when (seq fbs)
                    (filter #(= player (get-in % [:gr :player])) fbs))
            draws (when (seq wins')
                    (filter #(= G/Draw (:gr %)) fbs))]
        (cond
          (seq wins') (first wins')
          (seq draws) (first draws)
          :else (first fbs))))))

(s/defn next-step :- s/Int
  [board :- T/BoardType]
  ;; (clojure.pprint/pprint board)
  (let [attempts (map #(B/--> % board) P/positions)
        game-overs (filter some? (map #(M/foldMoveResult
                                        nil
                                        (constantly nil)
                                        identity
                                        %) attempts))
        moves (filter some? (map #(M/foldMoveResult
                                   nil
                                   identity
                                   (constantly nil)
                                   %) attempts))]
    (clojure.pprint/pprint game-overs)
    (clojure.pprint/pprint moves)
    0))

(s/defn weigh :- s/Int
  [strategy :- (s/protocol T/strategy)
   move-result :- T/MoveResultType]
  (M/foldMoveResult
   nil
   #(assoc
     %1 :weight (next-step %1))
   #(assoc
     %1 :weight (G/gameResult 1 -1 0 %1))
   move-result))

;; (defn win? [move-result]
;;   (M/foldMoveResult
;;    nil ;; occupied
;;    #(constantly nil) ;; keep playing
;;    (fn [finished-board] ;; is it a win  for current player?
;;      (let [myself (BL/whoseNotTurn finished-board)]
;;        (G/gameResult #(= myself %) false (B/getResult finished-board))))
;;    move-result))
;;        ;; winner == myself -> win
;;        ;; draw -> draw
;;        ;; loss

(def chaser-moves
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (T/first-move S/random-moves))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [attempts (map #(B/--> % board) P/positions)
            weighted (filter some? (map #(weigh this %) attempts))
            best-board (first (sort-by :weight weighted))]
        (clojure.pprint/pprint best-board)
        (ffirst (:moves best-board))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TODO: uncomment it and make to compile
(def b1 (T/first-move chaser-moves))
