(ns noliky.GameResult
  (:require [noliky.Types :as T]
            [noliky.Player :as P]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

(s/def Draw :- T/GameResultType
  (T/->GameResult :Draw P/Nobody :GameResult))
(s/def WinPlayer1 :- T/GameResultType
  (T/->GameResult :Win P/Player1 :GameResult))
(s/def WinPlayer2 :- T/GameResultType
  (T/->GameResult :Win P/Player2 :GameResult))

(s/defn gameResult :- s/Any
  [pwin :- (s/=> T/PlayerType s/Any)
   draw :- s/Any
   gr :- T/GameResultType]
  (if (= (:result gr) :Win) (pwin (:player gr)) draw))

(s/defn playerGameResult :- s/Any
  [pwin1 :- s/Any  pwin2 :- s/Any  draw :- s/Any result :- T/GameResultType]
  (cond
    (= Draw result) draw
    (= WinPlayer1 result) pwin1
    (= WinPlayer2 result)  pwin2
    :else (T/error "Error in game design")))

(s/defn win :- T/GameResultType
  [p :- T/PlayerType]
  (if (= p P/Player1) WinPlayer1 WinPlayer2))

(s/defn player1Wins :- T/GameResultType []
  WinPlayer1)

(s/defn player2Wins :- T/GameResultType []
  WinPlayer2)

(s/defn draw :- T/GameResultType []
  Draw)

(s/defn isPlayer1Wins :- s/Bool [gr :- T/GameResultType]
  (playerGameResult true false false gr))

(s/defn isPlayer2Wins :- s/Bool [gr :- T/GameResultType]
  (playerGameResult false true false gr))

(s/defn isDraw :- s/Bool [gr :- T/GameResultType]
  (playerGameResult false false true gr))
