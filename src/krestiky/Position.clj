(ns krestiky.Position
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
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
  (to-char [this :- Position] :- char
           "Convert Position to character")
  (to-int [this :- Position] :- t/AnyInteger
         "Convert Position to integer"))

(t/ann-datatype position [c :- char i :- t/AnyInteger])
(deftype ^:private position [c i]
  Position
  (to-char [_] c)
  (to-int [_] i))

(def NW (->position \1 1))
(def N  (->position \2 2))
(def NE (->position \3 3))
(def W  (->position \4 4))
(def C  (->position \5 5))
(def E  (->position \6 6))
(def SW (->position \7 7))
(def S  (->position \8 8))
(def SE (->position \9 9))

(t/ann values (t/Coll Position))
(def values [NE N NW W C E SW S SE])

(t/defn from-char [c :- char] :- Position
  (match c
         \1 NW \2 N \3 NE
         \4 W  \5 C \6 E
         \7 SW \8 S \9 SE))
(t/defn from-int [i :- t/AnyInteger] :- Position
  (match i
         1 NW 2 N 3 NE
         4 W  5 C 6 E
         7 SW 8 S 9 SE))
(t/defn value-of [s :- String] :- Position
  (match s
         "NW" NW "N" N "NE" NE
         "W"  W  "C" C "E"  E
         "SW" SW "S" S "SE" SE))
