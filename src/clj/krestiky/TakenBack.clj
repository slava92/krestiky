(ns krestiky.TakenBack
  (:require [krestiky.Types :refer :all])
  (:import [krestiky.Types P1])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defalias taken-back-fold
  (t/All [x] (t/IFn [(P1 x) (t/IFn [Board -> x]) -> x])))

(t/ann-record taken-back-type [tbf :- taken-back-fold])
(defrecord taken-back-type [tbf]
  TakenBack
  (tb-fold [this is-empty is-board]
    (tbf is-empty is-board)))

(t/defn mk-is-empty []
  (let [tbf (t/ann-form (fn [is-empty _] (:_1 is-empty)) taken-back-fold)]
    (->taken-back-type tbf)))

(t/defn mk-is-board [b :- Board]
  (let [tbf (t/ann-form (fn [_ is-board] (is-board b)) taken-back-fold)]
    (->taken-back-type tbf)))
