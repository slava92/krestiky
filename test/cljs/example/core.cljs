(ns example.core
  (:require [clojure.test.check :as sc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:require-macros [clojure.test.check.clojure-test :refer (defspec)]
                   [clojure.test.check.properties :refer (for-all)]
                   [cljs.test :refer (is testing deftest run-tests)]))

;; javascript '+' accepts non-numbers
(deftest somewhat-less-wat
  (is (= "{}[]" (+ {} []))))

(deftest javascript-allows-div0
  (is (= js/Infinity (/ 1 0) (/ (int 1) (int 0)))))

(defspec first-element-is-min-after-sorting ;; the name of the test
  100 ;; the number of iterations for test.check to test
  (for-all [v (gen/not-empty (gen/vector gen/int))]
                (= (apply min v)
                   (first (sort v)))))
