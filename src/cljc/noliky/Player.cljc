(ns noliky.Player
  (:require  #?(:clj [schema.core :as s]
                :cljs [schema.core :as s :include-macros true])
             [noliky.Types :as T]))

(s/defn player :- T/PlayerType
  [name]
  (T/->Player name :Player))

(s/def Player1 :- T/PlayerType (player "Alice"))
(s/def Player2 :- T/PlayerType (player "Bob"))
(s/def Nobody :- T/PlayerType (player "Ghost"))

(s/defn fold-player :- s/Any
  [a :- s/Any
   b :- s/Any
   p :- T/PlayerType]
  (if (= p Player1) a b))

(s/defn isPlayer1 :- s/Bool
  [p :- T/PlayerType]
  (fold-player true false p))

(s/defn isPlayer2 :- s/Bool
  [p :- T/PlayerType]
  (fold-player false true p))

(s/defn player1 :- T/PlayerType
  [] Player1)

(s/defn player2 :- T/PlayerType
  [] Player2)

(s/defn alternate :- T/PlayerType
  [p :- T/PlayerType]
  (fold-player Player2 Player1 p))

(s/defn toSymbol :- s/Str
  [p :- T/PlayerType]
  ({Player1 "X"
    Player2 "O"
    Nobody "."} p))

;; use s/Any for char since it is not in schema
(s/defn from-symbol :- T/PlayerType
  [s :- s/Any]
  ({\X  Player1, \O  Player2, \.  Nobody,
    "X" Player1, "O" Player2, "." Nobody,} s))

(s/defmethod T/show :Player :- s/Str
  [player :- T/PlayerType]
  (:name player))
