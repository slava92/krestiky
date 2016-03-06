(ns main.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [noliky.Types :as T]
            [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as GR]
            [noliky.MoveResult :as MR]
            [noliky.Position :as Pos]
            [noliky.Player :as PR]
            [noliky.Blind :as BlindRobot]
            [noliky.Chase :as SlowRobot]
            [noliky.Space :as Robot]
            [noliky.Types :as T]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]))
(enable-console-print!)

(def initial-state
  {:player-name (T/show PR/Player1)
   :move-result (T/->KeepPlaying (B/empty-board) :KeepPlaying)
   :board (B/empty-board)
   :robot Robot/deep-thought})

;; -- Event Handlers ----------------------------------------------------------

(register-handler                 ;; setup initial state
  :initialize                     ;; usage:  (submit [:initialize])
  (fn 
    [db [_ name]]
    (-> db
        (merge initial-state)
        (assoc-in [:player-name] name))))    ;; what it returns becomes the new state

(defn robot [strategy board]
  (let [pos (T/next-move strategy board)]
    (B/--> pos board)))

(defn handle-cell-click
  [app-state [_ position board]]
  (if (not= (BL/whoseTurn board) PR/Player1)
    app-state ;; ignore the click if it is not Player1
    (let [board (get-in app-state [:board])
          strategy (get-in app-state [:robot])
          attempt (B/--> position board)
          move-result (MR/foldMoveResult
                       (T/->KeepPlaying board :KeepPlaying)
                       (partial robot strategy)
                       (constantly attempt)
                       attempt)
          board' (MR/foldMoveResult board identity identity move-result)]
      (-> app-state
          (assoc-in [:move-result] move-result)
          (assoc-in [:board] board')))))

(register-handler
 :pos-click handle-cell-click)

(defn handle-name-update
  [app-state [_ new-name]]
  (assoc-in app-state [:player-name] new-name))

(register-handler
 :name-update handle-name-update)

(register-handler
 :new-game
 (fn [db _]
   (assoc-in initial-state
             [:player-name]
             (get-in db [:player-name]))))

;; -- Subscription Handlers ---------------------------------------------------

(register-sub
 :board
 (fn [db _] (reaction (get-in @db [:board]))))

(register-sub
 :move-result
 (fn [db _] (reaction (get-in @db [:move-result]))))

(register-sub
 :player-name
 (fn [db _] (reaction (get-in @db [:player-name]))))
              
;; -- View Components ---------------------------------------------------------

(defn greeting
  [message]
  [:h2 message])


(defn name-input
  []
  (let [player-name (subscribe [:player-name])]
    (fn name-input-render
      []
      [:div.ask
       "Имя: "
       [:input.ask
        {:type "text"
         :value @player-name
         :on-change #(dispatch
                      [:name-update (-> % .-target .-value)])}]])))

(defn cell
  [idx]
  (let [position (get Pos/positions idx)
        board (subscribe [:board])
        taken (reaction (BL/occupiedPositions @board))]
    (fn []
      [:div {:id (:pos position) :class "cell"
             :on-click #(dispatch [:pos-click
                                   position
                                   @board])}
       (if (contains? @taken position)
         (PR/toSymbol (BL/playerAt @board position))
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
  (let [name (subscribe [:player-name])]
    (fn []
      [:div.new-game-wrapper
       [:input.new-game {:type "submit" :value "New Game"
                         :on-click #(dispatch [:new-game @name])}]])))

(defn status
  []
  (let [move (subscribe [:move-result])
        name (subscribe [:player-name])]
    (fn []
      [:h3
       (MR/foldMoveResult
        "Занято"
        #(str "Ходит " @name)
        #(GR/playerGameResult
          (str "Выиграл " @name "!")
          (str "Выиграл " (T/show PR/Player2) "!")
          "Ничья!"
          (B/getResult %))
        @move)])))

(defn simple-example
  []
  [:div
   [greeting "Крестики Нолики"]
   [name-input]
   [board]
   [status]
   [new-game]])


;; -- Entry Point -------------------------------------------------------------


(defn ^:export client
  []
  (reagent/render [simple-example]
                  (js/document.getElementById "app")))

(defn ^:export init []
  (dispatch-sync [:initialize (T/show PR/Player1)])
  (client))
