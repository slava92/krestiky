(ns noliky.Position
  (:require [noliky.Types :as T]))

(def NW (T/->Position "NW" :Position))
(def N  (T/->Position "N " :Position))
(def NE (T/->Position "NE" :Position))
(def W  (T/->Position "W " :Position))
(def C  (T/->Position "C " :Position))
(def E  (T/->Position "E " :Position))
(def SW (T/->Position "SW" :Position))
(def S  (T/->Position "S " :Position))
(def SE (T/->Position "SE" :Position))

(def positions [NW N NE W C E SW S SE])

(defmethod T/show :Position [p] (:pos p))
