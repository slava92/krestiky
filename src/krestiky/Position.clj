(ns krestiky.Position
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)

(t/ann NW Position)
(declare NW)
(t/ann N Position)
(declare N)
(t/ann NE Position)
(declare NE)
(t/ann W Position)
(declare W)
(t/ann C Position)
(declare C)
(t/ann E Position)
(declare E)
(t/ann SW Position)
(declare SW)
(t/ann S Position)
(declare S)
(t/ann SE Position)
(declare SE)

(t/defprotocol Position
  "Position on Board"
  (toChar [this :- Position] :- char
          "Convert Position to character")
  (toInt [this :- Position] :- t/AnyInteger
         "Convert Position to integer")
  (fromChar [this :- Position c :- char] :- Position)
  (fromInt [this :- Position i :- t/AnyInteger] :- Position)
  (valueOf [this :- Position s :- String] :- Position))

(t/ann-datatype position [c :- char i :- t/AnyInteger])
(deftype ^:private position [c i]
  Position
  (toChar [_] c)
  (toInt [_] i)
  (fromChar [_ c]
    (match c
           \1 NW \2 N \3 NE
           \4 W  \5 C \6 E
           \7 SW \8 S \9 SE))
  (fromInt [_ i]
    (match i
           1 NW 2 N 3 NE
           4 W  5 C 6 E
           7 SW 8 S 9 SE))
  (valueOf [_ s]
    (match s
           "NW" NW "N" N "NE" NE
           "W"  W  "C" C "E"  E
           "SW" SW "S" S "SE" SE)))

(def NW (->position \1 1))
(def N  (->position \2 2))
(def NE (->position \3 3))
(def W  (->position \4 4))
(def C  (->position \5 5))
(def E  (->position \6 6))
(def SW (->position \7 7))
(def S  (->position \8 8))
(def SE (->position \9 9))

(t/ann values (t/Seqable Position))
(def values [NE N NW W C E SW S SE])
