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

(s/defmethod ^:always-validate T/show :Position :- s/Str
  [p :- T/PositionType] (:pos p))
