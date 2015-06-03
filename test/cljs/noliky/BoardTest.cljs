(ns noliky.BoardTest
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as P]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer-macros (for-all)]
            [clojure.test.check.clojure-test :refer-macros (defspec)]))

(defprotocol strategy
  (first-move [this]) 
  (next-move [this board]))

(def random-moves
  (reify strategy
    (first-move [this]
      (get P/positions (rand-int (count P/positions))))
    (next-move [this board] nil)))
