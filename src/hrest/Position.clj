(ns hrest.Position
  (:require [hrest.Types :refer :all])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defmethod show NW [p] "NW")
(defmethod show N  [p] "N ")
(defmethod show NE [p] "NE")
(defmethod show E  [p] "E ")
(defmethod show SE [p] "SE")
(defmethod show S  [p] "S ")
(defmethod show SW [p] "SW")
(defmethod show W  [p] "W ")
(defmethod show C  [p] "C ")

(t/ann positions (t/Coll Position))
(def positions [NW N NE E SE S SW W C])
