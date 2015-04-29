(ns hrest.Player
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types Player])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann player (t/All [x] (t/IFn [x x Player -> x])))
(defn player [a b p] (if (= p Player1) a b))

(t/defn isPlayer1 [p :- Player] (player true false p))

(t/defn isPlayer2 [p :- Player] (player false true p))

(t/defn player1 [] Player1)

(t/defn player2 [] Player2)

(t/defn alternate [p :- Player] (player Player2 Player1 p))

(t/defn to-char [p :- Player] (player \X \O p))

(defmethod show Player [p]
  (case p
    Player1 "Player 1"
    Player2 "Player 2"))
