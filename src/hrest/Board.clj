(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as GR]
            [hrest.MoveResult :as MR]
            [hrest.Position :as Pos])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defmethod show EmptyBoard [eb]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

;; class Move from to | from -> to where
;;   (-->) :: Position -> from -> to
(t/ann --> (t/All [from] [Position from -> MoveResult]))
(defmulti --> (fn [pos board] (clazz board)))

;; instance Move EmptyBoard MoveResult where
(defmethod --> EmptyBoard [pos from]
  (->KeepPlaying (->Board (list [pos Player1]) {pos Player1})))

;; instance Move Board MoveResult where
(defmethod --> Board [pos {:keys [moves positions] :as bd}]
  (let [w (BL/whoseTurn bd)
        j (BL/playerAt bd pos)]
    (if (nil? j)
      (let [m' (assoc positions pos w)
            b' (->Board (apply list (cons [pos w] moves)) m')
            wins [[NW W  SW] [N  C  S ]
                  [NE E  SE] [NW N  NE]
                  [W  C  E ] [SW S  SE]
                  [NW C  SE] [SW C  NE]]
            pos->plrs
            (t/fn [diag :- (t/Coll Position)] :- (t/Coll (t/Option Player))
              (map (t/fn [p :- Position] :- (t/Option Player) (get m' p))
                   diag))
            allEq
            (t/fn [diag :- (t/Coll Position)] :- boolean
              (let [pls (pos->plrs diag)
                    same? (distinct pls)]
                (and (= 1 (count same?)) (not= nil (first same?)))))
            isWin (not= nil (some true? (map allEq wins)))
            isDraw (= (count (keys m')) (count Pos/positions))]
        (cond
          isWin (->GameFinished (->FinishedBoard b' (GR/win w)))
          isDraw (->GameFinished (->FinishedBoard b' (GR/draw)))
          :else (->KeepPlaying b'))
        (undefined))
      (->PositionOccupied))))

;; instance Move MoveResult MoveResult where
(defmethod --> PositionOccupied [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(defmethod --> KeepPlaying [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(defmethod --> GameFinished [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(declare showPositionMap)
(t/ann showPositionMap [(t/Map Position Player) -> String])

;; instance Show Board where
;;   show b@(Board _ m) =
;;     intercalate " " [showPositionMap m, "[", show (whoseTurn b), "to move ]"]
(defmethod show Board [b] ;; {:keys [moves positions] :as b}]
  [" " (showPositionMap (:positions b)) "[" (show (BL/whoseTurn b)) "to move ]"]
  (undefined))

;; -- not exported
;; pos ::
;;   Ord k =>
;;   M.Map k Player
;;   -> String
;;   -> k
;;   -> String
;; pos m e p =
;;   maybe e (return . toSymbol) (p `M.lookup` m)

(defn showPositionMap [m] (undefined))
;; showPositionMap ::
;;   M.Map Position Player
;;   -> String
;; showPositionMap m =
;;   let pos' = pos m "?"
;;   in concat [ ".=",  pos' NW, "=.=", pos' N , "=.=", pos' NE
;;             , "=.=", pos' W , "=.=", pos' C , "=.=", pos' E
;;             , "=.=", pos' SW, "=.=", pos' S , "=.=", pos' SE, "=."
;;             ]
