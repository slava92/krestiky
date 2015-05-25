(ns simpleexample.core
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [noliky.Types :as T]
            [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as GR]
            [noliky.MoveResult :as MR]
            [noliky.Position :as Pos]
            [noliky.Player :as Plr]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]))
(enable-console-print!)

(def initial-state
  {:move-result (T/->KeepPlaying (B/empty-board) :KeepPlaying)
   :attempt (T/->KeepPlaying (B/empty-board) :KeepPlaying)
   :board (B/empty-board)})

;; -- Event Handlers ----------------------------------------------------------

(register-handler                 ;; setup initial state
  :initialize                     ;; usage:  (submit [:initialize])
  (fn 
    [db _]
    (merge db initial-state)))    ;; what it returns becomes the new state

(defn handle-cell-click
  [app-state [_ position]]
  (let [board (get-in app-state [:board])
        move-result (get-in app-state [:move-result])
        attempt (B/--> position move-result)
        board' (MR/foldMoveResult board identity identity attempt)
        move-result' (MR/foldMoveResult
                      move-result
                      (constantly attempt)
                      (constantly attempt)
                      attempt)]
    (-> app-state
        (assoc-in [:move-result] move-result')
        (assoc-in [:board] board')
        (assoc-in [:attempt] attempt))))

(register-handler
 :pos-click handle-cell-click)

(register-handler
 :new-game
 (fn [db _] initial-state))

;; -- Subscription Handlers ---------------------------------------------------

(register-sub
 :board
 (fn [db _] (reaction (get-in @db [:board]))))

(register-sub
 :attempt
 (fn [db _] (reaction (get-in @db [:attempt]))))

(register-sub :new-game (fn [db _] db))

;; -- View Components ---------------------------------------------------------

(defn greeting
  [message]
  [:h2 message])

(defn cell
  [idx]
  (let [board (subscribe [:board])
        taken (reaction (BL/occupiedPositions @board))
        position (get Pos/positions idx)]
    (fn []
      [:div {:id (:pos position) :class "cell"
             :on-click  #(dispatch [:pos-click position])}
       (if (contains? @taken position)
         (Plr/toSymbol (BL/playerAt @board position))
         "")])))

(defn board
  []
  [:div.board
   [:div.row
    [cell 0] [cell 1] [cell 2]]
   [:div.row
    [cell 3] [cell 4] [cell 5]]
   [:div.row
    [cell 6] [cell 7] [cell 8]]])

(defn new-game
  []
  [:div.new-game-wrapper
   [:input.new-game {:type "submit" :value "New Game"
                     :on-click #(dispatch [:new-game])}]])

(defn status
  []
  (let [move (subscribe [:attempt])]
    (fn []
      [:h3
       (MR/foldMoveResult
        "Занято"
        #(str "Ходит " (T/show (BL/whoseTurn %)))
        #(GR/playerGameResult
          (str (T/show T/Player1) " wins!")
          (str (T/show T/Player2) " wins!")
          "Ничья!"
          (B/getResult %))
        @move)])))

(defn simple-example
  []
  [:div
   [greeting "Крестики Нолики"]
   [board]
   [status]
   [new-game]])


;; -- Entry Point -------------------------------------------------------------


(defn ^:export client
  []
  (dispatch-sync [:initialize])
  (reagent/render [simple-example]
                  (js/document.getElementById "app")))
