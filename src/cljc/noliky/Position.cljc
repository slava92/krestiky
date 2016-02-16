(ns noliky.Position
  (:require [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(def NW (T/->Position "NW" :Position))
(def N  (T/->Position "N " :Position))
(def NE (T/->Position "NE" :Position))
(def W  (T/->Position "W " :Position))
(def C  (T/->Position "C " :Position))
(def E  (T/->Position "E " :Position))
(def SW (T/->Position "SW" :Position))
(def S  (T/->Position "S " :Position))
(def SE (T/->Position "SE" :Position))

(def positions
  [NW N NE W C E SW S SE])

(def pos->idx
  (into {}
        (map-indexed
         (fn [idx pos] [(:pos pos) (inc idx)])
         positions)))

;; s/Char is missing. use s/Any instead
(def char->pos
  {\1 NW, \2 N, \3 NE,
   \4 W,  \5 C, \6 E,
   \7 SW, \8 S, \9 SE})

(defmethod T/show :Position
  [p] (:pos p))
