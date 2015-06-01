(ns example.core
  (:require [cemerick.cljs.test :refer-macros (is deftest)]))

(deftest somewhat-less-wat
  (is (= "{}[]" (+ {} []))))

(deftest javascript-allows-div0
  (is (= js/Infinity (/ 1 0) (/ (int 1) (int 0)))))
