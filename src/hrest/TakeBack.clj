(ns hrest.TakeBack
  (:require [hrest.Types :refer :all])
  (:import [hrest.Types Board FinishedBoard
            TakeBackIsEmpty TakeBackIsBoard]
           [hrest.Types Position Player])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

;; class TakeBack to from | to -> from where
;;   takeBack :: to -> from
(t/ann takeBack (t/All [from] [from -> TakenBack]))
(defmulti takeBack (fn [board] (clazz board)))

;; instance TakeBack FinishedBoard TakenBack where
(defmethod takeBack FinishedBoard [{:keys [b]}]
  (let [pos-plr (first (:moves b))
        pos (t/ann-form (if (nil? pos-plr)
                          (abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
                          (first pos-plr))
                        Position)
        positions' (dissoc (:positions b) pos)
        moves' (apply list (rest (:moves b)))]
    (->TakeBackIsBoard (->Board moves' positions'))))

;; instance TakeBack Board TakenBack where
(defmethod takeBack Board [board]
  (if (= 1 (count (:moves board)))
    (->TakeBackIsEmpty)
    (let [pos-plr (first (:moves board))
          pos (t/ann-form (if (nil? pos-plr)
                            (abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
                            (first pos-plr))
                          Position)
          positions' (dissoc (:positions board) pos)
          moves' (apply list (rest (:moves board)))]
      (->TakeBackIsBoard (->Board moves' positions'))
      (undefined))))

(t/ann foldTakenBack
       (t/All [a] [a [Board -> a] TakenBack -> a]))
(defmulti foldTakenBack
  (t/fn [a :- t/Any fb :- t/Any tb :- t/Any] (clazz tb)))

(defmethod foldTakenBack TakeBackIsEmpty [e _ tb] e)
(defmethod foldTakenBack TakeBackIsBoard [_ k tb] (k (:board tb)))

(t/defn takenBackBoard [tb :- TakenBack] :- (t/Option Board)
  (foldTakenBack nil (t/fn [b :- Board] :- Board b) tb))
