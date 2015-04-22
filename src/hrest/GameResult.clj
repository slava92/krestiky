(ns hrest.GameResult
  (:require [hrest.Types :refer :all]
            [hrest.Player :as Plr])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann Draw (t/HVec [(t/Val ::Draw)]))
(def Draw [::Draw])

(t/ann WinPlayer1 (t/HVec [(t/Val ::Win)
                           (t/Val :hrest.Player/Player1)]))
(def WinPlayer1 [::Win Plr/Player1])

(t/ann WinPlayer2 (t/HVec [(t/Val ::Win)
                           (t/Val :hrest.Player/Player2)]))
(def WinPlayer2 [::Win Plr/Player2])

(t/defalias GameResult
  (t/U (t/HVec [(t/Val ::Draw)])
       (t/HVec [(t/Val ::Win) (t/U (t/Val :hrest.Player/Player1)
                                   (t/Val :hrest.Player/Player2))])))

(t/ann gameResult (t/All [x] [[Plr/Player -> x] x GameResult -> x]))
(defn gameResult [pwin draw [outcome player]]
  (if (= outcome ::Win) (pwin player) draw))

(t/ann playerGameResult (t/All [x] [x x x GameResult -> x]))
(defn playerGameResult [pwin1 pwin2 draw result]
  (case result
    [::Draw] draw
    [::Win Plr/Player1] pwin1
    [::Win Plr/Player2] pwin2))

(t/defn win [p :- Plr/Player] :- GameResult
  (if (= p Plr/Player1) WinPlayer1 WinPlayer2))

(t/defn player1Wins [] :- GameResult WinPlayer1)

(t/defn player2Wins [] :- GameResult WinPlayer2)

(t/defn draw [] :- GameResult Draw)

;; -- | Returns whether or not player 1 won for the game result.
;; isPlayer1Wins ::
;;   GameResult
;;   -> Bool
;; isPlayer1Wins =
;;   playerGameResult True False False

;; -- | Returns whether or not player 2 won for the game result.
;; isPlayer2Wins ::
;;   GameResult
;;   -> Bool
;; isPlayer2Wins =
;;   playerGameResult False True False

;; -- | Returns whether the game result is a draw.
;; isDraw ::
;;   GameResult
;;   -> Bool
;; isDraw =
;;   playerGameResult False False True
