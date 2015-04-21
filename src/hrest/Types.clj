(ns hrest.Types
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/defn clazz [x :- t/Any] :- (t/U t/Kw (t/Option java.lang.Class))
  (if (keyword? x) x (class x)))

(t/ann show [t/Any -> String])
(defmulti show clazz)
(defmethod show :default [x] (throw (Exception. "abstract show")))
