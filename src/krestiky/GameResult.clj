(ns krestiky.GameResult
  (:require [krestiky.Types :refer :all]
            [krestiky.Player :refer [Player1 Player2]]
            [clojure.core.match :refer [match]]
            [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann Draw GameResult)
(declare Draw)
(t/ann Player1Wins GameResult)
(declare Player1Wins)
(t/ann Player2Wins GameResult)
(declare Player2Wins)

(t/ann-record game-result [r :- char])
(defrecord game-result [r]
  GameResult
  (draw? [this]
    (match this Draw true :else false))
  (win? [this] (match this Draw false :else true))
  (strict-fold [this p1 p2 dr] p1)
  (winner [this]
    (match this Player1Wins Player1 Player2Wins Player2 :else nil))
  Show
  (to-string [this]
    (match this
           Draw "Draw"
           Player1Wins "Player 1 Wins"
           Player2Wins "Player 2 Wins")))

(t/ann value-of [String -> GameResult])
(defn value-of [name]
  (match name
         "Draw" Draw
         "Player 1 Wins" Player1Wins
         "Player 2 Wins" Player2Wins))

(t/ann values (t/Coll GameResult))
(def values [Draw Player1Wins Player2Wins])

(t/ann win [Player -> GameResult])
(defn win [p]
  (match p
         Player1 Player1Wins
         Player2 Player2Wins))

(def Draw (->game-result \D))
(def Player1Wins (->game-result \1))
(def Player2Wins (->game-result \2))
