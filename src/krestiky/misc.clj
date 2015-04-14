(ns krestiky.misc
  (:require [clojure.core.typed :as t]))

(defprotocol what
  (huh [this]))

(deftype player [c]
  what
  (huh [this] (str "it is " c)))
