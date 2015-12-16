(ns noliky.TakeBack
  (:require [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])))

;; class TakeBack to from | to -> from where
;;   takeBack :: to -> from
(defmulti takeBack :type)

;; instance TakeBack FinishedBoard TakenBack where
(s/defmethod takeBack :FinishedBoard :- T/TakeBackIsBoardType
  [{:keys [b]} {:b T/BoardType}]
  (let [pos-plr (first (:moves b))
        pos (if (nil? pos-plr)
              (T/abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
              (first pos-plr))
        positions' (dissoc (:positions b) pos)
        moves' (apply list (rest (:moves b)))]
    (T/->TakeBackIsBoard
     (T/->Board moves' positions' :Board)
     :TakeBackIsBoard)))

;; instance TakeBack Board TakenBack where
(s/defmethod takeBack :Board :- T/TakenBackType
  [board :- T/BoardType]
  (if (= 1 (count (:moves board)))
    (T/->TakeBackIsEmpty :TakeBackIsEmpty)
    (let [pos-plr (first (:moves board))
          pos (if (nil? pos-plr)
                (T/abstract "Broken invariant: board-in-play with empty move list. This is a program bug.")
                (first pos-plr))
          positions' (dissoc (:positions board) pos)
          moves' (apply list (rest (:moves board)))]
      (T/->TakeBackIsBoard
       (T/->Board moves' positions' :Board)
       :TakeBackIsBoard))))

(defmulti foldTakenBack
  (fn [a fb tb] (:type tb)))

(s/defmethod foldTakenBack :TakeBackIsEmpty :- s/Any
  [e _ tb :- T/TakeBackIsEmptyType] e)
(s/defmethod foldTakenBack :TakeBackIsBoard :- s/Any
  [_ k tb :- T/TakeBackIsBoardType]
  (k (:board tb)))

(s/defn takenBackBoard :- T/UnfinishedType
  [tb :- T/TakenBackType]
  (foldTakenBack nil (fn [b] b) tb))
