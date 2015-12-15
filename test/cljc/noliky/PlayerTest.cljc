(ns noliky.PlayerTest
  (:require [noliky.Player :as P :refer (player1 player2)]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.test :as st]
            [clojure.test :as t]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer (for-all)]
            [clojure.test.check.clojure-test :refer (defspec)]))


(t/use-fixtures :once st/validate-schemas)

(def players (gen/elements [(player1) (player2)]))

(defspec prop-alternate
  100
  (for-all [p players]
           (= p (P/alternate (P/alternate p)))))

(defspec prop-exclusive
  100
  (for-all [p players]
           (not= (P/isPlayer1 p) (P/isPlayer2 p))))
