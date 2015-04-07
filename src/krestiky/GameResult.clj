(ns krestiky.GameResult
  (:require [krestiky.Player :refer [Player Player1 Player2]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))
(set! *warn-on-reflection* true)

(t/defprotocol
    GameResult
  "Game Result"
  (isDraw [this :- GameResult] :- boolean)
  (isWin [this :- GameResult] :- boolean)
  ([x] strictFold [this  :- GameResult
                   player1Wins :- x player2Wins :- x draw :- x] :- x)
  (toString [this :- GameResult] :- String)
  (winner [this :- GameResult] :- (t/Option Player)))

(t/ann Draw GameResult)
(declare Draw)
(t/ann Player1Wins GameResult)
(declare Player1Wins)
(t/ann Player2Wins GameResult)
(declare Player2Wins)

(t/ann-datatype game-result [r :- char])
(deftype ^:private game-result [r]
         GameResult
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

(def Draw (->game-result \D))
(def Player1Wins (->game-result \1))
(def Player2Wins (->game-result \2))

(t/ann valueOf [String -> GameResult])
(defn valueOf [name]
  (match name
         "Draw" Draw
         "Player 1 Wins" Player1Wins
         "Player 2 Wins" Player2Wins))

(def values [Draw Player1Wins Player2Wins])

(t/ann win [Player -> GameResult])
(defn win [p]
  (match p
         Player1 Player1Wins
         Player2 Player2Wins))
