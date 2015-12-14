(ns noliky.Player
  (:require  #?(:clj [schema.core :as s]
                :cljs [schema.core :as s :include-macros true])
             [noliky.Types :as T]))

(s/defn player :- s/Any
  [a :- s/Any
   b :- s/Any
   p :- T/PlayerType]
  (if (= p T/Player1) a b))

(s/defn isPlayer1 :- s/Bool
  [p :- T/PlayerType]
  (player true false p))

(s/defn isPlayer2 :- s/Bool
  [p :- T/PlayerType]
  (player false true p))

(s/defn player1 :- T/PlayerType
  [] T/Player1)

(s/defn player2 :- T/PlayerType
  [] T/Player2)

(s/defn alternate :- T/PlayerType
  [p :- T/PlayerType]
  (player T/Player2 T/Player1 p))

(s/defn toSymbol :- s/Str
  [p :- T/PlayerType]
  (player "X" "O" p))

(s/defmethod ^:always-validate T/show :Player :- s/Str
  [player :- T/PlayerType]
  (:name player))
