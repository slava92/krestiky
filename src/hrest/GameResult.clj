(ns hrest.GameResult
  (:require [hrest.Types :refer :all]
            [hrest.Player :as Plr])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann gameResult (t/All [x] [[Player -> x] x GameResult -> x]))
(defn gameResult [pwin draw [outcome player]]
  (if (= outcome :hrest.Types.Win) (pwin player) draw))

(t/ann playerGameResult (t/All [x] [x x x GameResult -> x]))
(defn playerGameResult [pwin1 pwin2 draw result]
  (case result
    [:hrest.Types.Draw] draw
    [:hrest.Types.Win Player1] pwin1
    [:hrest.Types.Win Player2] pwin2))

(t/defn win [p :- Player] :- GameResult
  (if (= p Player1) WinPlayer1 WinPlayer2))

(t/defn player1Wins [] :- GameResult WinPlayer1)

(t/defn player2Wins [] :- GameResult WinPlayer2)

(t/defn draw [] :- GameResult Draw)

(t/defn isPlayer1Wins [gr :- GameResult] :- boolean
  (playerGameResult true false false gr))

(t/defn isPlayer2Wins [gr :- GameResult] :- boolean
  (playerGameResult false true false gr))

(t/defn isDraw [gr :- GameResult] :- boolean
  (playerGameResult false false true gr))
