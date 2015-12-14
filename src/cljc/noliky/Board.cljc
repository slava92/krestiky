(ns noliky.Board
  (:require [noliky.Types :as T]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as GR]
            [noliky.MoveResult :as MR]
            [noliky.Player :as Plr]
            [noliky.Position :as Pos :refer [NW N NE E SE S SW W C]]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [clojure.set]
            [clojure.string :as str]))

(s/defmethod ^:always-validate T/show :EmptyBoard :- s/Str
  [eb :- T/EmptyBoardType]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

;; class Move from to | from -> to where
;;   (-->) :: Position -> from -> to
(defmulti --> (fn [pos board] (:type board)))

;; instance Move EmptyBoard MoveResult where
(s/defmethod ^:always-validate --> :EmptyBoard :- T/KeepPlayingType
  [pos :- T/PositionType from :- T/EmptyBoardType]
  (T/->KeepPlaying (T/->Board (list [pos T/Player1]) {pos T/Player1} :Board) :KeepPlaying))

;; instance Move Board MoveResult where
(s/defmethod ^:always-validate --> :Board :- T/MoveResultType
  [pos :- T/PositionType {:keys [moves positions] :as bd}]
  (let [w (BL/whoseTurn bd)
        j (BL/playerAt bd pos)]
    (if (nil? j)
      (let [m' (assoc positions pos w)
            b' (T/->Board (apply list (cons [pos w] moves)) m' :Board)
            wins [[NW W  SW] [N  C  S ]
                  [NE E  SE] [NW N  NE]
                  [W  C  E ] [SW S  SE]
                  [NW C  SE] [SW C  NE]]
            pos->plrs
            (fn [diag]
              (map (fn [p] (get m' p))
                   diag))
            allEq
            (fn [diag]
              (let [pls (pos->plrs diag)
                    same? (distinct pls)]
                (and (= 1 (count same?)) (not= nil (first same?)))))
            isWin (not= nil (some true? (map allEq wins)))
            isDraw (= (count (keys m')) (count Pos/positions))]
        (cond
          isWin (T/->GameFinished (T/->FinishedBoard b' (GR/win w) :FinishedBoard) :GameFinished)
          isDraw (T/->GameFinished (T/->FinishedBoard b' (GR/draw) :FinishedBoard) :GameFinished)
          :else (T/->KeepPlaying b' :KeepPlaying)))
      (T/->PositionOccupied :PositionOccupied))))

;; instance Move MoveResult MoveResult where
(s/defmethod ^:always-validate --> :PositionOccupied :- T/MoveResultType
  [pos :- T/PositionType mr :- T/MoveResultType]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(s/defmethod ^:always-validate --> :KeepPlaying :- T/MoveResultType
  [pos :- T/PositionType mr :- T/MoveResultType]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(s/defmethod ^:always-validate --> :GameFinished :- T/MoveResultType
  [pos :- T/PositionType mr :- T/MoveResultType]
  (MR/keepPlayingOr mr #(--> pos %) mr))

;; | Return the result of a completed tic-tac-toe game.
(s/defn getResult :- T/GameResultType
  [b :- T/FinishedBoardType]
  (:gr b))

(declare showPositionMap)

;; instance Show Board where
(s/defmethod ^:always-validate T/show :Board :- s/Str
  [b :- T/BoardType]
  (str/join " " [(showPositionMap (:positions b)) "[" (T/show (BL/whoseTurn b)) "to move ]"]))

;; instance Show FinishedBoard where
(s/defmethod ^:always-validate T/show :FinishedBoard :- s/Str
  [fb :- T/FinishedBoardType]
  (let [summary (GR/gameResult T/show "draw" (:gr fb))]
    summary
    (str/join " " [(showPositionMap (:positions (:b fb))) "[[" summary "]]"])))

;; | Shows a board using ASCII notation and substituting the returned string for each position.
;;   k ^ The function returning the string to substitute each position.
(defn showEachPosition [k]
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
(defn showEachPositionFlat [k]
  (str "1 2 3 4 5 6 7 8 9\n" (str/join " " (map k Pos/positions))))

(defn showLinePosition [k]
  (str "|" (k NW) (k N) (k NE) "|" (k W) (k C) (k E) "|" (k SW) (k S) (k SE) "|"))

;; -- not exported
(defn pos [m d p]
  (let [s (get m p)] (if (nil? s) d (Plr/toSymbol s))))

(defn showPositionMap [m]
  (str ".=" (pos m "?" NW) "=.=" (pos m "?" N) "=.=" (pos m "?" NE)
       "=.=" (pos m "?" W) "=.=" (pos m "?" C) "=.=" (pos m "?" E)
       "=.=" (pos m "?" SW) "=.=" (pos m "?" S) "=.=" (pos m "?" SE) "=."))

;; instance BoardLike EmptyBoard where
(defn empty-board [] (T/->EmptyBoard :EmptyBoard))
(defmethod BL/whoseTurn :EmptyBoard [_] T/Player1)
(defmethod BL/isEmpty :EmptyBoard [_] true)
(defmethod BL/occupiedPositions :EmptyBoard [_] #{})
(defmethod BL/moves :EmptyBoard [_] 0)
(defmethod BL/isSubboardOf :EmptyBoard [_ _] true)
(defmethod BL/isProperSubboardOf :EmptyBoard [_ _] false)
(defmethod BL/playerAt :EmptyBoard [_ _] nil)
(defmethod BL/showBoard :EmptyBoard [_]
  (showEachPosition (fn [p] (pos {} " " p))))
(defmethod BL/showLine :EmptyBoard [_]
  (showEachPosition (fn [p] (pos {} "." p))))

;; instance BoardLike Board where
(defmethod BL/whoseTurn :Board [b]
  (let [last-move (first (:moves b))]
    (if (nil? last-move) T/Player1 (Plr/alternate (second last-move)))))
(defmethod BL/isEmpty :Board [_] false)
(defmethod BL/occupiedPositions :Board [b] (set (keys (:positions b))))
(defmethod BL/moves :Board [b] (count (:positions b)))

(defmethod BL/isSubboardOf [:Board :Board] [b1 b2]
  (let [poss1 (set (:positions b1))
        poss2 (set (:positions b2))]
    (clojure.set/subset? poss1 poss2)))
(defmethod BL/isProperSubboardOf [:Board :Board] [b1 b2]
  (let [poss1 (set (:positions b1))
        poss2 (set (:positions b2))]
    (and (clojure.set/subset? poss1 poss2)
         (not= poss1 poss2))))
(defmethod BL/playerAt :Board [b p]
  (get (:positions b) p))
(defmethod BL/showBoard :Board [b]
  (let [poss (:positions b)]
    (showEachPosition (fn [p] (pos poss " " p)))))
(defmethod BL/showLine :Board [b]
  (let [poss (:positions b)]
    (showLinePosition (fn [p] (pos poss "." p)))))

;; instance BoardLike FinishedBoard where
(defmethod BL/whoseTurn :FinishedBoard [_] T/Nobody)
(defmethod BL/isEmpty :FinishedBoard [fb] (BL/isEmpty (:b fb)))
(defmethod BL/occupiedPositions :FinishedBoard [fb] (BL/occupiedPositions (:b fb)))
(defmethod BL/moves :FinishedBoard [fb] (BL/moves (:b fb)))
(defmethod BL/isSubboardOf [:FinishedBoard :FinishedBoard] [b1 b2]
  (BL/isSubboardOf (:b b1) (:b b2)))
(defmethod BL/isProperSubboardOf [:FinishedBoard :FinishedBoard] [b1 b2]
  (BL/isProperSubboardOf (:b b1) (:b b2)))
(defmethod BL/playerAt :FinishedBoard [fb p] (BL/playerAt (:b fb) p))
(defmethod BL/showBoard :FinishedBoard [fb] (BL/showBoard (:b fb)))
(defmethod BL/showLine :FinishedBoard [fb] (BL/showLine (:b fb)))

;; instance Show Unfinished where
(defmethod T/show :UnfinishedEmpty [ub] (T/show (:b ub)))
(defmethod T/show :UnfinishedBoard [ub] (T/show (:b ub)))

;; TODO: no dispatch by expected result
;; instance Move Unfinished Unempty where
;;   p --> UnfinishedEmpty b =
;;     UnemptyBoard (p --> b)
;;   p --> UnfinishedBoard b =
;;     case p --> b of PositionAlreadyOccupied -> UnemptyBoard b
;;                     KeepPlaying b' -> UnemptyBoard b'
;;                     GameFinished b' -> UnemptyFinished b'

(defmulti unfinished
  (fn [feb fb ub] (:type ub)))
(defmethod unfinished :UnfinishedEmpty [feb fb ub] (feb (:b ub)))
(defmethod unfinished :UnfinishedBoard [feb fb ub] (fb (:b ub)))

;; instance BoardLike Unfinished where
(defmethod BL/whoseTurn :UnfinishedEmpty [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseTurn :UnfinishedBoard [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseNotTurn :UnfinishedEmpty [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/whoseNotTurn :UnfinishedBoard [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/isEmpty :UnfinishedEmpty [ub] (BL/isEmpty (:b ub)))
(defmethod BL/isEmpty :UnfinishedBoard [ub] (BL/isEmpty (:b ub)))
(defmethod BL/occupiedPositions :UnfinishedEmpty [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/occupiedPositions :UnfinishedBoard [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/moves :UnfinishedEmpty [ub] (BL/moves (:b ub)))
(defmethod BL/moves :UnfinishedBoard [ub] (BL/moves (:b ub)))
(defmethod BL/isSubboardOf [:UnfinishedEmpty :UnfinishedEmpty] [_ _] true)
(defmethod BL/isSubboardOf [:UnfinishedEmpty :UnfinishedBoard] [_ _] true)
(defmethod BL/isSubboardOf [:UnfinishedBoard :UnfinishedEmpty] [_ _] false)
(defmethod BL/isSubboardOf [:UnfinishedBoard :UnfinishedBoard] [b1 b2]
  (BL/isSubboardOf b1 b2))
(defmethod BL/isProperSubboardOf [:UnfinishedEmpty :UnfinishedEmpty] [_ _] false)
(defmethod BL/isProperSubboardOf [:UnfinishedEmpty :UnfinishedBoard] [_ _] true)
(defmethod BL/isProperSubboardOf [:UnfinishedBoard :UnfinishedEmpty] [_ _] false)
(defmethod BL/isProperSubboardOf [:UnfinishedBoard :UnfinishedBoard] [b1 b2]
  (BL/isProperSubboardOf b1 b2))
(defmethod BL/playerAt :UnfinishedEmpty [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/playerAt :UnfinishedBoard [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/showBoard :UnfinishedEmpty [ub] (BL/showBoard (:b ub)))
(defmethod BL/showBoard :UnfinishedBoard [ub] (BL/showBoard (:b ub)))
(defmethod BL/showLine :UnfinishedEmpty [ub] (BL/showLine (:b ub)))
(defmethod BL/showLine :UnfinishedBoard [ub] (BL/showLine (:b ub)))

;; instance Show Unempty where
(defmethod T/show :UnemptyBoard [ub] (T/show (:b ub)))
(defmethod T/show :UnemptyFinished [ub] (T/show (:b ub)))

;; unempty ::
;;   (Board -> a)
;;   -> (FinishedBoard -> a)
;;   -> Unempty
;;   -> a
;; unempty f _ (UnemptyBoard b) =
;;   f b
;; unempty _ g (UnemptyFinished b) =
;;   g b
(defmulti unempty
  (fn [fb ffb ue] (:type ue)))
(defmethod unempty :UnemptyBoard [fb ffb ue] (fb (:b ue)))
(defmethod unempty :UnemptyFinished [fb ffb ue] (ffb (:b ue)))

;; instance BoardLike Unempty where
(defmethod BL/whoseTurn :UnemptyBoard [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseTurn :UnemptyFinished [ub] (BL/whoseTurn (:b ub)))
(defmethod BL/whoseNotTurn :UnemptyBoard [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/whoseNotTurn :UnemptyFinished [ub] (BL/whoseNotTurn (:b ub)))
(defmethod BL/isEmpty :UnemptyBoard [ub] (BL/isEmpty (:b ub)))
(defmethod BL/isEmpty :UnemptyFinished [ub] (BL/isEmpty (:b ub)))
(defmethod BL/occupiedPositions :UnemptyBoard [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/occupiedPositions :UnemptyFinished [ub] (BL/occupiedPositions (:b ub)))
(defmethod BL/moves :UnemptyBoard [ub] (BL/moves (:b ub)))
(defmethod BL/moves :UnemptyFinished [ub] (BL/moves (:b ub)))
(defmethod BL/isSubboardOf [:UnemptyBoard :UnemptyBoard] [_ _] true)
(defmethod BL/isSubboardOf [:UnemptyBoard :UnemptyFinished] [_ _] true)
(defmethod BL/isSubboardOf [:UnemptyFinished :UnemptyBoard] [_ _] false)
(defmethod BL/isSubboardOf [:UnemptyFinished :UnemptyFinished] [b1 b2]
  (BL/isSubboardOf b1 b2))
(defmethod BL/isProperSubboardOf [:UnemptyBoard :UnemptyBoard] [_ _] false)
(defmethod BL/isProperSubboardOf [:UnemptyBoard :UnemptyFinished] [_ _] true)
(defmethod BL/isProperSubboardOf [:UnemptyFinished :UnemptyBoard] [_ _] false)
(defmethod BL/isProperSubboardOf [:UnemptyFinished :UnemptyFinished] [b1 b2]
  (BL/isProperSubboardOf b1 b2))
(defmethod BL/playerAt :UnemptyBoard [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/playerAt :UnemptyFinished [ub p] (BL/playerAt (:b ub) p))
(defmethod BL/showBoard :UnemptyBoard [ub] (BL/showBoard (:b ub)))
(defmethod BL/showBoard :UnemptyFinished [ub] (BL/showBoard (:b ub)))
(defmethod BL/showLine :UnemptyBoard [ub] (BL/showLine (:b ub)))
(defmethod BL/showLine :UnemptyFinished [ub] (BL/showLine (:b ub)))

;;; test ;;;

(defn tryit []
  (->> (empty-board)
       (--> C)
       (--> E)
       (--> NE)
       (--> SE)
       (--> SW)
       (--> W)
       (T/show)))
