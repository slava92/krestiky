(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as GR]
            [hrest.Position :as Pos])
  (:import [hrest.Types EmptyBoard Board FinishedBoard
            PositionOccupied KeepPlaying GameFinished])
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(defmethod show EmptyBoard [eb]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

(t/ann --> (t/All [from] [Position from -> MoveResult]))
(defmulti --> (fn [pos board] (clazz board)))

(defmethod --> EmptyBoard [pos from]
  (->KeepPlaying (->Board (list [pos Player1]) {pos Player1})))

(t/defalias Positions (t/Map Position Player))
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
        (throw (Exception. "TBI")))
      (->PositionOccupied))))
