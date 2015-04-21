(ns hrest.Types
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

;; (t/defprotocol Show
;;   "Convertable to string"
;;   ([x] show [this :- x] :- String
;;    "Returns string representation"))

(t/defn clazz [x :- t/Any] :- (t/Option java.lang.Class) (class x))

;; (t/ann show [t/Any -> String])
;; (defmulti show clazz)
;; (defmethod show :default [x] (throw (Exception. "abstract show")))
