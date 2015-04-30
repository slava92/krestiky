(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as GR]
            [hrest.MoveResult :as MR]
            [hrest.Player :as Plr]
            [hrest.Position :as Pos :refer [NW N NE E SE S SW W C]]
            [clojure.set])
  (:import [hrest.Types EmptyBoard Board FinishedBoard GameResult
            PositionOccupied KeepPlaying GameFinished]
           [hrest.Types Position Player])
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

;; | Return the result of a completed tic-tac-toe game.
(t/defn getResult [b :- FinishedBoard] :- GameResult (:gr b))

(declare showPositionMap)
(t/ann showPositionMap [(t/Map Position Player) -> String])

;; instance Show Board where
(defmethod show Board [b]
  (apply str (interpose " " [(showPositionMap (:positions b)) "[" (show (BL/whoseTurn b)) "to move ]"])))

;; instance Show FinishedBoard where
(defmethod show FinishedBoard [fb]
  (let [summary (GR/gameResult #(show %) "draw" (:gr fb))]
    (t/ann-form summary String)
    (apply str (interpose " " [(showPositionMap (:positions (:b fb))) "[[" summary "]]"]))))

;; | Shows a board using ASCII notation and substituting the returned string for each position.
;;   k ^ The function returning the string to substitute each position.
(t/defn showEachPosition [k :- [Position -> String]] :- String
  (let [z ".===.===.===."
        each [z
              (str "| " (k NW) " | " (k N ) " | " (k NE) " |")
              z
              (str "| " (k W ) " | " (k C ) " | " (k E ) " |")
              z
              (str "| " (k SW) " | " (k S ) " | " (k SE) " |")
              z]]
    (apply str (interpose "\n" each))))

;;   k ^ The function returning the string to substitute each position.
(t/defn showEachPositionFlat [k :- [Position -> String]] :- String
  (str "1 2 3 4 5 6 7 8 9\n" (apply str (interpose " " (map k Pos/positions)))))

(t/defn showLinePosition [k :- [Position -> String]] :- String
  (str "|" (k NW) (k N) (k NE) "|" (k W) (k C) (k E) "|" (k SW) (k S) (k SE) "|"))

;; -- not exported
(t/defn pos [m :- (t/Map Position Player) d :- String p :- Position] :- String
  (let [s (get m p)] (if (nil? s) d (show s))))

(t/defn showPositionMap [m :- (t/Map Position Player)] :- String
  (str ".=" (pos m "?" NW) "=.=" (pos m "?" N) "=.=" (pos m "?" NE)
       "=.=" (pos m "?" W) "=.=" (pos m "?" C) "=.=" (pos m "?" E)
       "=.=" (pos m "?" SW) "=.=" (pos m "?" S) "=.=" (pos m "?" SE) "=."))

;; instance BoardLike EmptyBoard where
(defmethod BL/whoseTurn EmptyBoard [_] Player1)
(defmethod BL/isEmpty EmptyBoard [_] true)
(defmethod BL/occupiedPositions EmptyBoard [_] #{})
(defmethod BL/moves EmptyBoard [_] 0)
(defmethod BL/isSubboardOf EmptyBoard [_ _] true)
(defmethod BL/isProperSubboardOf EmptyBoard [_ _] false)
(defmethod BL/playerAt EmptyBoard [_ _] nil)
(defmethod BL/showBoard EmptyBoard [_]
  (showEachPosition (t/fn [p :- Position] :- String (pos {} " " p))))
(defmethod BL/showLine EmptyBoard [_]
  (showEachPosition (t/fn [p :- Position] :- String (pos {} "." p))))

;; instance BoardLike Board where
(defmethod BL/whoseTurn Board [b]
  (let [last-move (first (:moves b))]
    (if (nil? last-move) Player1 (Plr/alternate (second last-move)))))
(defmethod BL/isEmpty Board [_] false)
(defmethod BL/occupiedPositions Board [b] (set (keys (:positions b))))
(defmethod BL/moves Board [b] (count (:positions b)))

(t/ann ^:no-check clojure.set/subset? [(t/Set t/Any) (t/Set t/Any) -> boolean])
(defmethod BL/isSubboardOf [Board Board] [b1 b2]
  (let [poss1 (set (:positions b1))
        poss2 (set (:positions b2))]
    (clojure.set/subset? poss1 poss2)))
(defmethod BL/isProperSubboardOf [Board Board] [b1 b2]
  (let [poss1 (set (:positions b1))
        poss2 (set (:positions b2))]
    (and (clojure.set/subset? poss1 poss2)
         (not= poss1 poss2))))
(defmethod BL/playerAt Board [b p]
  (get (:positions b) p))
(defmethod BL/showBoard Board [b]
  (let [poss (:positions b)]
    (showEachPosition (t/fn [p :- Position] :- String (pos poss " " p)))))
(defmethod BL/showLine Board [b]
  (let [poss (:positions b)]
    (showLinePosition (t/fn [p :- Position] :- String (pos poss "." p)))))

;; instance BoardLike FinishedBoard where
(defmethod BL/isEmpty FinishedBoard [fb] (BL/isEmpty (:b fb)))
(defmethod BL/occupiedPositions FinishedBoard [fb] (BL/occupiedPositions (:b fb)))
(defmethod BL/moves FinishedBoard [fb] (BL/moves (:b fb)))
(defmethod BL/isSubboardOf [FinishedBoard FinishedBoard] [b1 b2]
  (BL/isSubboardOf (:b b1) (:b b2)))
(defmethod BL/isProperSubboardOf [FinishedBoard FinishedBoard] [b1 b2]
  (BL/isProperSubboardOf (:b b1) (:b b2)))
(defmethod BL/playerAt FinishedBoard [fb p] (BL/playerAt (:b fb) p))
(defmethod BL/showBoard FinishedBoard [fb] (BL/showBoard (:b fb)))
(defmethod BL/showLine FinishedBoard [fb] (BL/showLine (:b fb)))
