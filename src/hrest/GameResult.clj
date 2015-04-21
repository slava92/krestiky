(ns hrest.GameResult
  (:require [hrest.Types :refer :all]
            [hrest.Player :as Plr])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann Draw (t/HVec [(t/Val ::Draw)]))
(def Draw [::Draw])

(t/ann WinPlayer1 (t/HVec [(t/Val :hrest.GameResult/Win)
                           (t/Val :hrest.Player/Player1)]))
(def WinPlayer1 [::Win Plr/Player1])

(t/ann WinPlayer2 (t/HVec [(t/Val :hrest.GameResult/Win)
                           (t/Val :hrest.Player/Player2)]))
(def WinPlayer2 [::Win Plr/Player2])

(t/defalias GameResult (t/U Draw WinPlayer1 WinPlayer2))

(t/ann gameResult (t/All [x] [[Plr/Player -> x] x GameResult -> x]))
(defn gameResult [pwin draw gresult]
  (if (= gresult Draw) draw (pwin (second gresult))))
