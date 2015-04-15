(ns krestiky.MoveResult
  (:require [krestiky.BoardLike :as BL]
            [krestiky.Board :as B]
            [krestiky.FinishedBoard :as FB]
            [krestiky.Position :refer [Position to-int] :as Pos])
  (:import [krestiky.BoardLike P1])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defprotocol
    MoveResult
  "Move Result"
  (keep_playing [move :- MoveResult] :- MoveResult)
  ([a] keep-playing-or [move :- MoveResult
                        els :- (P1 a)
                        fb :- (t/IFn [B/Board -> a])] :- a)
  (try-move [move :- MoveResult pos :- Position] :- MoveResult))

;; abstract <X> X fold(fj.P1<X> positionAlreadyOccupied,
;;                fj.F<Board,X> keepPlaying,
;;                fj.F<Board.FinishedBoard,X> gameOver) 
;; ([x] fold [move :- MoveResult
;;            pao :- (P1 x)
;;            keepp :- (t/IFn [B/Board -> x])
;;            gmover :- (t/IFn [FB/FinishedBoard -> x])] :- x)
(t/defalias move-result-fold
  (t/All [x] (t/IFn [(P1 x)
                     (t/IFn [B/Board -> x])
                     (t/IFn [FB/FinishedBoard -> x])
                     -> x])))

(t/ann-record move-result [mrf :- move-result-fold])
(defrecord
    ^:private move-result [mrf]
    MoveResult
    (keep_playing [move board]
      (let [eb nil
            kpf (t/fn [a :- Board] :- Board a)
            gof (t/fn [a :- FinishedBoard] :- (t/Option Board) a)]
        (mrf eb kpf gof)))
        ;; (throw (Exception. "TBI"))))
    (keep-playing-or [move els board] (throw (Exception. "TBI")))
    (try-move [move pos] (throw (Exception. "TBI"))))


;; fj.data.Option<Board>	keepPlaying() 
;; <A> A	keepPlayingOr(fj.P1<A> els, fj.F<Board,A> board) 
;; MoveResult	tryMove(Position p)

;; static MoveResult	gameOver(Board.FinishedBoard b) 
;; static MoveResult	keepPlaying(Board b) 
;; static MoveResult	positionAlreadyOccupied() 
