(ns noliky.GameResultTest
  (:require [noliky.GameResult :as G]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros (for-all)]
            [clojure.test.check.clojure-test :refer-macros (defspec)]))

(def game-results
  [[true false false G/WinPlayer1]
   [false true false G/WinPlayer2]
   [false false true G/Draw]])

(defspec game-result-fold
  100
  (for-all [[win1 win2 draw gr] (gen/elements game-results)]
           (G/playerGameResult win1 win2 draw gr)))
