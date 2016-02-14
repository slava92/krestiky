(ns noliky.Position
  (:require [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(s/def NW :- T/PositionType (T/->Position "NW" :Position))
(s/def N  :- T/PositionType (T/->Position "N " :Position))
(s/def NE :- T/PositionType (T/->Position "NE" :Position))
(s/def W  :- T/PositionType (T/->Position "W " :Position))
(s/def C  :- T/PositionType (T/->Position "C " :Position))
(s/def E  :- T/PositionType (T/->Position "E " :Position))
(s/def SW :- T/PositionType (T/->Position "SW" :Position))
(s/def S  :- T/PositionType (T/->Position "S " :Position))
(s/def SE :- T/PositionType (T/->Position "SE" :Position))

(s/def positions :- [T/PositionType]
  [NW N NE W C E SW S SE])

(s/def pos->idx :- {s/Str s/Int}
  (into {}
        (map-indexed
         (fn [idx pos] [(:pos pos) (inc idx)])
         positions)))

;; s/Char is missing. use s/Any instead
(s/def char->pos :- {s/Any T/PositionType}
  {\1 NW, \2 N, \3 NE,
   \4 W,  \5 C, \6 E,
   \7 SW, \8 S, \9 SE})

(s/defmethod T/show :Position :- s/Str
  [p :- T/PositionType] (:pos p))
