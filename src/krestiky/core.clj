(ns krestiky.core
  (:require [krestiky.Types :refer :all]
            [krestiky.BoardLike :as BL]
            [krestiky.EmptyBoard :as EB]
            [krestiky.MoveResult :as MR]
            [krestiky.Position :as Pos])
  (:import [krestiky.Types P1])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defn -main
  "Simple test case of two moves"
  [& args]
  (let [empty EB/empty-board
        board1 (start-to empty Pos/C)
        move-result2 (move-to board1 Pos/NW)
        po-undef (P1. board1)
        kpf1 (t/fn [b :- Board] :- Board b)
        gof-undef (fn [fb] (throw (Exception. "undefined")))
        board2 (mr-fold move-result2 po-undef kpf1 gof-undef)]
    (printf "Empty = \n%s\n" (BL/as-string empty BL/simple-chars))
    (printf "Board1 = \n%s\n" (BL/as-string board1 BL/simple-chars))
    (printf "Board2 = \n%s\n" (BL/as-string board2 BL/simple-chars))))
