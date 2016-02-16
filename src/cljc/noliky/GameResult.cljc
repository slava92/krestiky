(ns noliky.GameResult
  (:require [noliky.Types :as T]
            [noliky.Player :as P]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(def Draw
  (T/->GameResult :Draw P/Nobody :GameResult))
(def WinPlayer1
  (T/->GameResult :Win P/Player1 :GameResult))
(def WinPlayer2
  (T/->GameResult :Win P/Player2 :GameResult))

(defn gameResult
  [pwin
   draw
   gr]
  (if (= (:result gr) :Win) (pwin (:player gr)) draw))

(defn playerGameResult
  [pwin1  pwin2 draw
   result]
  (cond
    (= Draw result) draw
    (= WinPlayer1 result) pwin1
    (= WinPlayer2 result)  pwin2
    :else (T/error "Error in game design")))

(defn win
  [p]
  (if (= p P/Player1) WinPlayer1 WinPlayer2))

(defn player1Wins []
  WinPlayer1)

(defn player2Wins []
  WinPlayer2)

(defn draw []
  Draw)

(defn isPlayer1Wins
  [gr]
  (playerGameResult true false false gr))

(defn isPlayer2Wins
  [gr]
  (playerGameResult false true false gr))

(defn isDraw
  [gr]
  (playerGameResult false false true gr))
