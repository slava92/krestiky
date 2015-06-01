(ns noliky.PlayerTest
  (:require [noliky.Player :as P :refer (player1 player2)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros (for-all)]
            [clojure.test.check.clojure-test :refer-macros (defspec)]))


(def players (gen/elements [(player1) (player2)]))

(defspec prop-alternate
  100
  (for-all [p players]
           (= p (P/alternate (P/alternate p)))))

(defspec prop-exclusive
  100
  (for-all [p players]
           (not= (P/isPlayer1 p) (P/isPlayer2 p))))
