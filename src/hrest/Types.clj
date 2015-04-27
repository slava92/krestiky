(ns hrest.Types
  (:require [clojure.core.typed :as t :refer [check-ns]]))
(set! *warn-on-reflection* true)

(t/ann Player1 (t/Val ::Player1))
(def Player1 ::Player1)

(t/ann Player2 (t/Val ::Player2))
(def Player2 ::Player2)

(t/defalias Player (t/U (t/Val ::Player1) (t/Val ::Player2)))

(t/ann NW (t/Val ::NW)) (def NW ::NW)
(t/ann N  (t/Val ::N))  (def N ::N)
(t/ann NE (t/Val ::NE)) (def NE ::NE)
(t/ann W  (t/Val ::W))  (def W ::W)
(t/ann C  (t/Val ::C))  (def C ::C)
(t/ann E  (t/Val ::E))  (def E ::E)
(t/ann SW (t/Val ::SW)) (def SW ::SW)
(t/ann S  (t/Val ::S))  (def S ::S)
(t/ann SE (t/Val ::SE)) (def SE ::SE)

(t/defalias Position
  (t/U (t/Val ::NW) (t/Val ::N) (t/Val ::NE)
       (t/Val ::W)  (t/Val ::C) (t/Val ::E)
       (t/Val ::SW) (t/Val ::S) (t/Val ::SE)))

(t/ann Draw (t/HVec [(t/Val ::Draw)]))
(def Draw [::Draw])

(t/ann WinPlayer1 (t/HVec [(t/Val ::Win)
                           (t/Val ::Player1)]))
(def WinPlayer1 [::Win Player1])

(t/ann WinPlayer2 (t/HVec [(t/Val ::Win)
                           (t/Val ::Player2)]))
(def WinPlayer2 [::Win Player2])

(t/defalias GameResult
  (t/U (t/HVec [(t/Val ::Draw)])
       (t/HVec [(t/Val ::Win) (t/U (t/Val ::Player1)
                                   (t/Val ::Player2))])))
(t/ann-record EmptyBoard [])
(defrecord EmptyBoard [])

(t/ann-record Board [moves :- (t/Coll (t/HVec [Position Player]))
                     poss :- (t/Map Position Player)])
(defrecord Board [moves poss])

(t/ann-record FinishedBoard [b :- Board gr :- GameResult])
(defrecord FinishedBoard [b gr])

(t/ann-record PositionOccupied [])
(defrecord PositionOccupied [])

(t/ann-record KeepPlaying [board :- Board])
(defrecord KeepPlaying [board])

(t/ann-record GameFinished [board :- FinishedBoard])
(defrecord GameFinished [board])

(t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))

(t/defn clazz [x :- t/Any] :- (t/U Class nil)
  (class x))

(t/defn keywordzz [x :- t/Any] :- (t/U t/Keyword Class nil)
  (if (keyword? x) x (class x)))

(defn abstract [s] (throw (Exception. (format "abstract '%s'" s))))

(t/ann show [t/Any -> String])
(defmulti show keywordzz)
(defmethod show :default [x] (abstract "show"))
