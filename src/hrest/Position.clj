(ns hrest.Position
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types Position])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(def NW (->Position "NW"))
(def N  (->Position "N "))
(def NE (->Position "NE"))
(def W  (->Position "W "))
(def C  (->Position "C "))
(def E  (->Position "E "))
(def SW (->Position "SW"))
(def S  (->Position "S "))
(def SE (->Position "SE"))

(t/ann positions (t/Coll Position))
(def positions [NW N NE E SE S SW W C])

(defmethod show Position [p] (:pos p))
