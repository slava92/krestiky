(ns simpleexample.core
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [noliky.Types :as T]
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
   :game (T/->EmptyBoard :EmptyBoard)})


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

(defn board
  []
  [:div.board
   [:div.row
    [:div#NW.cell "."] [:div#N.cell "."] [:div#NE.cell "."]]
   [:div.row
    [:div#W.cell "."] [:div#C.cell "."] [:div#E.cell "."]]
   [:div.row
    [:div#SW.cell "."] [:div#S.cell "."] [:div#SE.cell "."]]])

(defn simple-example
  []
  [:div
   [:div
    [greeting "Доброе утро, страна. Сейчас"]
    [clock]]
   [:div
    [greeting "Крестики Нолики"]
    [board]
    [greeting "дно"]]])


;; -- Entry Point -------------------------------------------------------------


(defn ^:export client
  []
  (dispatch-sync [:initialize])
  (reagent/render [simple-example]
                  (js/document.getElementById "app")))
