(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defmethod show EmptyBoard [eb]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

(t/ann --> (t/All [from] [Position from -> MoveResult]))
(defmulti --> (fn [pos board] (clazz board)))

(defmethod --> EmptyBoard [pos from]
  (->KeepPlaying (->Board [[pos Player1]] {pos Player1})))

(defmethod --> Board [pos {:keys [moves poss] :as bd}]
  (let [w (BL/whoseTurn bd)]
    (throw (Exception. "TBI"))))
;; instance Move Board MoveResult where
;;   p --> bd@(Board q m) =
;;     let w       = whoseTurn bd
;;         (j, m') = M.insertLookupWithKey (\_ x _ -> x) p w m
;;         wins =
;;           [
;;             (NW, W , SW)
;;           , (N , C , S )
;;           , (NE, E , SE)
;;           , (NW, N , NE)
;;           , (W , C , E )
;;           , (SW, S , SE)
;;           , (NW, C , SE)
;;           , (SW, C , NE)
;;           ]
;;         allEq (d:e:t) = d == e && allEq (e:t)
;;         allEq _       = True
;;         isWin         = any (\(a, b, c) -> any allEq $ mapM (`M.lookup` m') [a, b, c]) wins
;;         isD           = all (`M.member` m') [minBound ..]
;;         b'            = Board ((p, w):q) m'
;;     in maybe (if isWin
;;               then
;;                 GameFinished (b' `FinishedBoard` win w)
;;               else
;;                 if isD
;;                 then
;;                   GameFinished (b' `FinishedBoard` draw)
;;                 else
;;                   KeepPlaying b') (const PositionAlreadyOccupied) j
