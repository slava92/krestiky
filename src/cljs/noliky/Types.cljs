(ns noliky.Types)

(defrecord Player [name type])
(def Player1 (->Player "Alice" :Player))
(def Player2 (->Player "Bob" :Player))
(def Nobody (->Player "Ghost" :Player))

(defrecord Position [pos type])

(defrecord GameResult [result player type])

(defrecord EmptyBoard [type])

(defrecord Board [moves positions type])

(defrecord FinishedBoard [b gr type])

;; (t/defalias Unfinished (t/U UnfinishedEmpty UnfinishedBoard))
(defrecord UnfinishedEmpty [b type])

(defrecord UnfinishedBoard [b type])

;; (t/defalias Unempty (t/U UnemptyFinished UnemptyBoard))
(defrecord UnemptyBoard [b type])

(defrecord UnemptyFinished [b type])

;; (t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))
(defrecord PositionOccupied [type])

(defrecord KeepPlaying [board type])

(defrecord GameFinished [board type])

;; (t/defalias TakenBack (t/U TakeBackIsEmpty TakeBackIsBoard))
(defrecord TakeBackIsEmpty[type])

(defrecord TakeBackIsBoard [board type])

(defn abstract [s] (throw (js/Error. (str "abstract " s))))
(defn undefined [] (abstract "TBI"))

(defmulti show (fn [x] (:type x)))
(defmethod show :default [x] (abstract "show"))

