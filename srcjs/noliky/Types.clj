(ns noliky.Types
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(defrecord Player [name])

(def Player1 (->Player "Alice"))
(def Player2 (->Player "Bob"))
(def Nobody (->Player "Ghost"))

(defrecord Position [pos])

(defrecord GameResult [result player])

(defrecord EmptyBoard [])

(defrecord Board [moves positions])

(defrecord FinishedBoard [b gr])

(defrecord UnfinishedEmpty [b])

(defrecord UnfinishedBoard [b])

(defrecord UnemptyBoard [b])

(defrecord UnemptyFinished [b])

(defrecord PositionOccupied [])

(defrecord KeepPlaying [board])

(defrecord GameFinished [board])

(defrecord TakeBackIsEmpty[])

(defrecord TakeBackIsBoard [board])

(defn abstract [s] (throw (js/Error. (str "abstract " s))))
(defn undefined [] (abstract "TBI"))

(defmulti show (fn [x] (str x)))
(defmethod show :default [x] (abstract "show"))


(defn -main [& args]
  (println "Hello world!")
  (println (str Player1)))

(set! *main-cli-fn* -main)
