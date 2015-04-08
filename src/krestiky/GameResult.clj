(ns krestiky.GameResult
  (:require [krestiky.Player :refer [Player Player1 Player2]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)

(t/defprotocol
    IGameResult
  "Game Result"
  (isDraw [this :- IGameResult] :- boolean)
  (isWin [this :- IGameResult] :- boolean)
  ([x] strictFold [this  :- IGameResult
                   player1Wins :- x player2Wins :- x draw :- x] :- x)
  (toString [this :- IGameResult] :- String)
  (winner [this :- IGameResult] :- (t/Option Player)))

(t/ann Draw IGameResult)
(declare Draw)
(t/ann Player1Wins IGameResult)
(declare Player1Wins)
(t/ann Player2Wins IGameResult)
(declare Player2Wins)

(t/ann-datatype GameResult [r :- char])
(deftype GameResult [r]
  IGameResult
  (isDraw [this]
    (match this Draw true :else false))
  (isWin [this] (match this Draw false :else true))
  (strictFold [this p1 p2 dr] p1)
  (toString [this]
    (match this
           Draw "Draw"
           Player1Wins "Player 1 Wins"
           Player2Wins "Player 2 Wins"))
  (winner [this]
    (match this Player1Wins Player1 Player2Wins Player2 :else nil)))

(t/ann valueOf [String -> IGameResult])
(defn valueOf [name]
  (match name
         "Draw" Draw
         "Player 1 Wins" Player1Wins
         "Player 2 Wins" Player2Wins))

(def values [Draw Player1Wins Player2Wins])

(t/ann win [Player -> IGameResult])
(defn win [p]
  (match p
         Player1 Player1Wins
         Player2 Player2Wins))

(def Draw (->GameResult \D))
(def Player1Wins (->GameResult \1))
(def Player2Wins (->GameResult \2))
