(ns hrest.Board
  (:require [hrest.Types :refer :all]
            [hrest.BoardLike :as BL]
            [hrest.GameResult :as GR]
            [hrest.MoveResult :as MR]
            [hrest.Player :as Plr]
            [hrest.Position :as Pos :refer [NW N NE E SE S SW W C]]
            [clojure.set]
            [clojure.string :as str])
  (:import [hrest.Types EmptyBoard Board FinishedBoard GameResult
            UnfinishedEmpty UnfinishedBoard UnemptyBoard UnemptyFinished
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
          :else (->KeepPlaying b')))
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
  (str/join " " [(showPositionMap (:positions b)) "[" (show (BL/whoseTurn b)) "to move ]"]))

;; instance Show FinishedBoard where
(defmethod show FinishedBoard [fb]
  (let [summary (GR/gameResult show "draw" (:gr fb))]
    (t/ann-form summary String)
    (str/join " " [(showPositionMap (:positions (:b fb))) "[[" summary "]]"])))

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
    (str/join "\n" each)))

;;   k ^ The function returning the string to substitute each position.
(t/defn showEachPositionFlat [k :- [Position -> String]] :- String
  (str "1 2 3 4 5 6 7 8 9\n" (str/join " " (map k Pos/positions))))

(t/defn showLinePosition [k :- [Position -> String]] :- String
  (str "|" (k NW) (k N) (k NE) "|" (k W) (k C) (k E) "|" (k SW) (k S) (k SE) "|"))

;; -- not exported
(t/defn pos [m :- (t/Map Position Player) d :- String p :- Position] :- String
  (let [s (get m p)] (if (nil? s) d (Plr/toSymbol s))))

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

;; instance Show Unfinished where
(defmethod show UnfinishedEmpty [ub] (show (:b ub)))
(defmethod show UnfinishedBoard [ub] (show (:b ub)))

;; TODO: no dispatch by expected result
;; instance Move Unfinished Unempty where
;;   p --> UnfinishedEmpty b =
;;     UnemptyBoard (p --> b)
;;   p --> UnfinishedBoard b =
;;     case p --> b of PositionAlreadyOccupied -> UnemptyBoard b
;;                     KeepPlaying b' -> UnemptyBoard b'
;;                     GameFinished b' -> UnemptyFinished b'

(t/ann
 unfinished (t/All [a] [[EmptyBoard -> a]
                        [Board -> a]
                        Unfinished
                        -> a]))
(defmulti unfinished
  (t/fn [feb :- t/Any fb :- t/Any ub :- t/Any] (clazz ub)))
(defmethod unfinished UnfinishedEmpty [feb fb ub] (feb (:b ub)))
(defmethod unfinished UnfinishedBoard [feb fb ub] (fb (:b ub)))

;; instance BoardLike Unfinished where
(defmethod BL/whoseTurn UnfinishedEmpty [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseTurn UnfinishedBoard [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseNotTurn UnfinishedEmpty [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/whoseNotTurn UnfinishedBoard [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/isEmpty UnfinishedEmpty [ub] (BL/isEmpty (:b ub)))
(defmethod BL/isEmpty UnfinishedBoard [ub] (BL/isEmpty (:b ub)))
(defmethod BL/occupiedPositions UnfinishedEmpty [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/occupiedPositions UnfinishedBoard [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/moves UnfinishedEmpty [ub] (BL/moves (:b ub)))
(defmethod BL/moves UnfinishedBoard [ub] (BL/moves (:b ub)))
(defmethod BL/isSubboardOf [UnfinishedEmpty UnfinishedEmpty] [_ _] true)
(defmethod BL/isSubboardOf [UnfinishedEmpty UnfinishedBoard] [_ _] true)
(defmethod BL/isSubboardOf [UnfinishedBoard UnfinishedEmpty] [_ _] false)
(defmethod BL/isSubboardOf [UnfinishedBoard UnfinishedBoard] [b1 b2]
  (BL/isSubboardOf b1 b2))
(defmethod BL/isProperSubboardOf [UnfinishedEmpty UnfinishedEmpty] [_ _] false)
(defmethod BL/isProperSubboardOf [UnfinishedEmpty UnfinishedBoard] [_ _] true)
(defmethod BL/isProperSubboardOf [UnfinishedBoard UnfinishedEmpty] [_ _] false)
(defmethod BL/isProperSubboardOf [UnfinishedBoard UnfinishedBoard] [b1 b2]
  (BL/isProperSubboardOf b1 b2))
(defmethod BL/playerAt UnfinishedEmpty [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/playerAt UnfinishedBoard [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/showBoard UnfinishedEmpty [ub] (BL/showBoard (:b ub)))
(defmethod BL/showBoard UnfinishedBoard [ub] (BL/showBoard (:b ub)))
(defmethod BL/showLine UnfinishedEmpty [ub] (BL/showLine (:b ub)))
(defmethod BL/showLine UnfinishedBoard [ub] (BL/showLine (:b ub)))

;; instance Show Unempty where
(defmethod show UnemptyBoard [ub] (show (:b ub)))
(defmethod show UnemptyFinished [ub] (show (:b ub)))

;; unempty ::
;;   (Board -> a)
;;   -> (FinishedBoard -> a)
;;   -> Unempty
;;   -> a
;; unempty f _ (UnemptyBoard b) =
;;   f b
;; unempty _ g (UnemptyFinished b) =
;;   g b
(t/ann
 unempty (t/All [a] [[Board -> a]
                     [FinishedBoard -> a]
                     Unempty
                     -> a]))
(defmulti unempty
  (t/fn [fb :- t/Any ffb :- t/Any ue :- t/Any] (clazz ue)))
(defmethod unempty UnemptyBoard [fb ffb ue] (fb (:b ue)))
(defmethod unempty UnemptyFinished [fb ffb ue] (ffb (:b ue)))

;; instance BoardLike Unempty where
(defmethod BL/whoseTurn UnemptyBoard [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseTurn UnemptyFinished [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseNotTurn UnemptyBoard [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/whoseNotTurn UnemptyFinished [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/isEmpty UnemptyBoard [ub] (BL/isEmpty (:b ub)))
(defmethod BL/isEmpty UnemptyFinished [ub] (BL/isEmpty (:b ub)))
(defmethod BL/occupiedPositions UnemptyBoard [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/occupiedPositions UnemptyFinished [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/moves UnemptyBoard [ub] (BL/moves (:b ub)))
(defmethod BL/moves UnemptyFinished [ub] (BL/moves (:b ub)))
(defmethod BL/isSubboardOf [UnemptyBoard UnemptyBoard] [_ _] true)
(defmethod BL/isSubboardOf [UnemptyBoard UnemptyFinished] [_ _] true)
(defmethod BL/isSubboardOf [UnemptyFinished UnemptyBoard] [_ _] false)
(defmethod BL/isSubboardOf [UnemptyFinished UnemptyFinished] [b1 b2]
  (BL/isSubboardOf b1 b2))
(defmethod BL/isProperSubboardOf [UnemptyBoard UnemptyBoard] [_ _] false)
(defmethod BL/isProperSubboardOf [UnemptyBoard UnemptyFinished] [_ _] true)
(defmethod BL/isProperSubboardOf [UnemptyFinished UnemptyBoard] [_ _] false)
(defmethod BL/isProperSubboardOf [UnemptyFinished UnemptyFinished] [b1 b2]
  (BL/isProperSubboardOf b1 b2))
(defmethod BL/playerAt UnemptyBoard [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/playerAt UnemptyFinished [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/showBoard UnemptyBoard [ub] (BL/showBoard (:b ub)))
(defmethod BL/showBoard UnemptyFinished [ub] (BL/showBoard (:b ub)))
(defmethod BL/showLine UnemptyBoard [ub] (BL/showLine (:b ub)))
(defmethod BL/showLine UnemptyFinished [ub] (BL/showLine (:b ub)))
