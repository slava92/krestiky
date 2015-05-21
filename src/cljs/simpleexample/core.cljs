(ns simpleexample.core
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [noliky.Types :as T]
            [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Position :as Pos]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [register-handler
                                   path
                                   register-sub
                                   dispatch
                                   dispatch-sync
                                   subscribe]]))

;; trigger a dispatch every second
(defonce time-updater (js/setInterval
                        #(dispatch [:timer (js/Date.)]) 1000))

(def initial-state
  {:timer (js/Date.)
   :time-color "#f34"
   :game {:board (B/empty-board)}})

;; -- Event Handlers ----------------------------------------------------------

(register-handler                 ;; setup initial state
  :initialize                     ;; usage:  (submit [:initialize])
  (fn 
    [db _]
    (merge db initial-state)))    ;; what it returns becomes the new state

(register-handler
  :time-color                     ;; usage:  (submit [:time-color 34562])
  (path [:time-color])            ;; this is middleware
  (fn
    [time-color [_ value]]        ;; path middleware adjusts the first parameter
    value))

(register-handler
  :timer
  (fn
    ;; the first item in the second argument is :timer the second is the 
    ;; new value
    [db [_ value]]
    (assoc db :timer value)))    ;; return the new version of db

;; -- Subscription Handlers ---------------------------------------------------

(register-sub
  :timer
  (fn 
    [db _]                       ;; db is the app-db atom
    (reaction (:timer @db))))    ;; wrap the compitation in a reaction

(register-sub
  :time-color
  (fn 
    [db _]
    (reaction (:time-color @db))))

(register-sub
 :board
 (fn
  [db _]
  (reaction (get-in @db [:game :board]))))

;; -- View Components ---------------------------------------------------------

(defn greeting
  [message]
  [:h2 message])

(defn clock
  []
  (let [time-color (subscribe [:time-color])
        timer (subscribe [:timer])]
    (fn clock-render
        []
        (let [time-str (-> @timer
                           .toTimeString
                           (clojure.string/split " ")
                           first)
              style {:style {:color @time-color}}]
             [:div.example-clock style time-str]))))

(defn color-input
  []
  (let [time-color (subscribe [:time-color])]

    (fn color-input-render
        []
        [:div.color-input
         "Цвет часов: "
         [:input {:type "text"
                  :value @time-color
                  :on-change #(dispatch
                               [:time-color (-> % .-target .-value)])}]])))

(defn cell
  [idx]
  (let [board (subscribe [:board])
        ;; taken (BL/occupiedPositions @board)
        taken (atom #{})
        position (get Pos/positions idx)]
    (fn []
      (if (contains? @taken position)
        [:div {:id (:pos position) :class "cell"} "X"]
        [:div {:id (:pos position) :class "cell"} "*"]))))

(defn board
  []
  [:div.board
   [:div.row
    [cell 0] [cell 1] [cell 2]]
   [:div.row
    [cell 3] [cell 4] [cell 5]]
   [:div.row
    [cell 6] [cell 7] [cell 8]]])

(defn simple-example
  []
  [:div
   [:div
    [greeting "Доброе утро, страна. Сейчас"]
    [clock]]
   [:div
    [greeting "Крестики Нолики"]
    [board]
    [greeting "На дне"]]])


;; -- Entry Point -------------------------------------------------------------


(defn ^:export client
  []
  (dispatch-sync [:initialize])
  (reagent/render [simple-example]
                  (js/document.getElementById "app")))
