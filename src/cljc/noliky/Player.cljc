(ns noliky.Player
  (:require [noliky.Types :as T]))

(defn player [a b p] (if (= p T/Player1) a b))

(defn isPlayer1 [p] (player true false p))

(defn isPlayer2 [p] (player false true p))

(defn player1 [] T/Player1)

(defn player2 [] T/Player2)

(defn alternate [p] (player T/Player2 T/Player1 p))

(defn toSymbol [p] (player "X" "O" p))

(defmethod T/show :Player [p] (:name p))
