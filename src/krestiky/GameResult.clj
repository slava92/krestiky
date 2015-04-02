(ns krestiky.GameResult
  (:require [clojure.core.match :refer [match]]
            [clojure.core.typed :as t]))

(t/defprotocol GameResult
  "Game Result"
  (isDraw [this] :- boolean)
  (isWin [this] :- boolean)
  (strictFold [this p1 p2 dr])
  (toString [this] :- String)
  (winner [this] :- (t/U nil t/Any)))

(def ^:private win1 :win1)
(def ^:private win2 :win2)
(def ^:private draw :draw)

(declare Draw)
(declare Player1Wins)
(declare Player2Wins)

(t/ann-datatype position [c :- char i :- int])
(deftype ^:private game-result [r]
         GameResult
         (isDraw [this] :- boolean)
         (isWin [this] :- boolean)
         (strictFold [this p1 p2 dr])
         (toString [this] :- String)
         (winner [this] :- (t/U nil t/Any)))

;; boolean	isDraw() 
;; boolean	isWin() 
;; <X> X	strictFold(X player1Wins, X player2Wins, X draw) 
;; java.lang.String	toString() 
;; fj.data.Option<Player>	winner()
;; Returns the enum constant of this type with the specified name.
;; Returns an array containing the constants of this enum type, in the order they are declared.
;; static GameResult	valueOf(java.lang.String name)
;; static GameResult[]	values()
;; static GameResult	win(Player p) 
