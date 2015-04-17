(ns krestiky.MoveResult
  (:require [krestiky.Types :refer :all])
  (:import [krestiky.Types P1])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defalias move-result-fold
  (t/All [x] (t/IFn [(P1 x) ;; position already occupied
                     (t/IFn [Board -> x]) ;; keep playing
                     (t/IFn [FinishedBoard -> x]) ;; game over
                     -> x])))

(t/ann-record move-result [mrf :- move-result-fold])
(defrecord ^:private move-result [mrf]
    MoveResult
    (keep-playing [move]
      (let [eb nil
            kpf (t/fn [a :- Board] :- (t/Option Board) a)
            gof (t/fn [_ :- FinishedBoard] :- (t/Option Board) eb)]
        ((:mrf move) (P1. eb) kpf gof)))
    (keep-playing-or [move els fb]
      (let [fgo (t/fn [_ :- FinishedBoard] (:_1 els))]
        ((:mrf move) els fb fgo)))
    (try-move [self pos]
      (let [go (t/fn [a :- FinishedBoard] self)
            kp (t/fn [b :- Board] (move-to b pos))]
        ((:mrf self) (P1. self) kp go))))

(t/defn mk-game-over [b :- FinishedBoard] :- MoveResult
  (let [gof (t/ann-form (fn [_ _ gameover] (gameover b)) move-result-fold)]
    (->move-result gof)))

(t/defn mk-keep-playing [b :- Board] :- MoveResult
  (let [gof (t/ann-form (fn [_ keepplay _] (keepplay b)) move-result-fold)]
    (->move-result gof)))

(t/defn mk-already-occupied [] :- MoveResult
  (let [gof (t/ann-form (fn [po _ _] (:_1 po)) move-result-fold)]
    (->move-result gof)))
