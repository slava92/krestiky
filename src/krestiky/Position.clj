(ns krestiky.Position
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))

(t/defprotocol Position
  "Position on Board"
  (toChar [this :- Position] :- char
          "Convert Position to character")
  (toInt [this :- Position] :- int
         "Convert Position to integer"))

(t/ann-datatype position [c :- char i :- int])
(deftype ^:private position [c i]
         Position
         (toChar [this] c)
         (toInt [this] i))

(def NW (->position \1 1))
(def N  (->position \2 2))
(def NE (->position \3 3))
(def W  (->position \4 4))
(def C  (->position \5 5))
(def E  (->position \6 6))
(def SW (->position \7 7))
(def S  (->position \8 8))
(def SE (->position \9 9))
(def values [NE N NW W C E SW S SE])

(t/ann fromChar [char -> Position])
(defn fromChar [c]
  (match c
         \1 NW \2 N \3 NE
         \4 W  \5 C \6 E
         \7 SW \8 S \9 SE))

(t/ann fromInt [int -> Position])
(defn fromInt [i]
  (match i
         1 NW 2 N 3 NE
         4 W  5 C 6 E
         7 SW 8 S 9 SE))

(t/ann valueOf [String -> Position])
(defn valueOf [s]
  (match s
         "NW" NW "N" N "NE" NE
         "W"  W  "C" C "E"  E
         "SW" SW "S" S "SE" SE))
