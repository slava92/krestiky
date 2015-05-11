(ns noliky.TakeBack
  (:require [noliky.Types :as T]))

;; class TakeBack to from | to -> from where
;;   takeBack :: to -> from
(defmulti takeBack :type)

;; instance TakeBack FinishedBoard TakenBack where
(defmethod takeBack :FinishedBoard [{:keys [b]}]
  (let [pos-plr (first (:moves b))
        pos (if (nil? pos-plr)
              (abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
              (first pos-plr))
        positions' (dissoc (:positions b) pos)
        moves' (apply list (rest (:moves b)))]
    (->TakeBackIsBoard (->Board moves' positions' :Board) :TakeBackIsBoard)))

;; instance TakeBack Board TakenBack where
(defmethod takeBack :Board [board]
  (if (= 1 (count (:moves board)))
    (->TakeBackIsEmpty :TakeBackIsEmpty)
    (let [pos-plr (first (:moves board))
          pos (if (nil? pos-plr)
                (abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
                (first pos-plr))
          positions' (dissoc (:positions board) pos)
          moves' (apply list (rest (:moves board)))]
      (->TakeBackIsBoard (->Board moves' positions' :Board) :TakeBackIsBoard))))

(defmulti foldTakenBack
  (fn [a fb tb] (:type tb)))

(defmethod foldTakenBack :TakeBackIsEmpty [e _ tb] e)
(defmethod foldTakenBack :TakeBackIsBoard [_ k tb] (k (:board tb)))

(defn takenBackBoard [tb]
  (foldTakenBack nil (fn [b] b) tb))
