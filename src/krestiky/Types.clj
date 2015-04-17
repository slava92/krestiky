(ns krestiky.Types
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

;; (t/defalias TakenBack t/Any)

(t/ann-record [x] P1 [_1 :- x])
(defrecord P1 [_1])

(t/defprotocol Show
  "Convertable to string"
  ([x] to-string [this :- x] :- String
   "Returns string representation"))

(t/defprotocol Player
  "A Player"
  (alternate [this :- Player] :- Player
             "Returns player that has the next move.")
  (to-symbol [this :- Player] :- char
            "Returns symbol representing the player."))

(t/defprotocol Position
  "Position on Board"
  (to-char [this :- Position] :- char
           "Convert Position to character")
  (to-int [this :- Position] :- t/AnyInteger
         "Convert Position to integer"))

(t/defprotocol
    GameResult
  "Game Result"
  (draw? [this :- GameResult] :- boolean)
  (win? [this :- GameResult] :- boolean)
  ([x] strict-fold [this  :- GameResult
                   player1Wins :- x player2Wins :- x draw :- x] :- x)
  (winner [this :- GameResult] :- (t/Option Player)))

(t/defprotocol
    MoveResult
  "Move Result"
  (keep-playing [move :- MoveResult] :- (t/Option Board))
  ([a] keep-playing-or [move :- MoveResult
                        els :- (P1 a)
                        fb :- (t/IFn [Board -> a])] :- a)
  (try-move [move :- MoveResult pos :- Position] :- MoveResult))

(t/defprotocol Board
  "There is at least one move available"
  (move-to [this :- Board pos :- Position] :- MoveResult))

(t/defprotocol Empty
  "Empty starting board")

(t/defprotocol Started
  "At least one move has been made"
  (take-back [board :- Started] :- (t/U Board Empty)))
  
(t/defprotocol FinishedBoard
  "Implementation specific methods"
  (result [board :- FinishedBoard] :- GameResult))

