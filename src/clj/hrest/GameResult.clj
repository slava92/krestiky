(ns hrest.GameResult
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types Player GameResult])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(def Draw (->GameResult :Draw Nobody))
(def WinPlayer1 (->GameResult :Win Player1))
(def WinPlayer2 (->GameResult :Win Player2))

(t/ann gameResult (t/All [x] [[Player -> x] x GameResult -> x]))
(defn gameResult [pwin draw gr]
  (if (= (:result gr) :Win) (pwin (:player gr)) draw))

(t/ann playerGameResult (t/All [x] [x x x GameResult -> x]))
(defn playerGameResult [pwin1 pwin2 draw result]
  (cond
    (= Draw result) draw
    (= WinPlayer1 result) pwin1
    (= WinPlayer2 result)  pwin2
    :else (throw (Exception. "Error in game design"))))

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
