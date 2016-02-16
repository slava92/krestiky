(ns noliky.Board
  (:require [noliky.Types :as T]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as GR]
            [noliky.MoveResult :as MR]
            [noliky.Player :as PR]
            [noliky.Position :as P]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [clojure.set]
            [clojure.string :as str]))

(s/defn board :- T/BoardType [moves positions]
  (T/->Board moves positions :Board))

(s/defn finished-board :- T/FinishedBoardType [board game-result]
  (T/->FinishedBoard board game-result :FinishedBoard))

(s/defmethod  T/show :EmptyBoard :- s/Str
  [eb :- T/EmptyBoardType]
  ".=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=.=?=. [ Player 1 to move ]")

;; class Move from to | from -> to where
;;   (-->) :: Position -> from -> to
(defmulti --> (fn [pos board] (:type board)))

;; instance Move EmptyBoard MoveResult where
(defmethod  --> :EmptyBoard
  [pos from]
  (T/->KeepPlaying
   (T/->Board [[pos PR/Player1]] {pos PR/Player1} :Board)
   :KeepPlaying))

;; instance Move Board MoveResult where
(defmethod  --> :Board
  [pos
   {:keys [moves positions] :as bd}]
  (let [w (BL/whoseTurn bd)
        j (BL/playerAt bd pos)]
    (if j
      (T/->PositionOccupied :PositionOccupied)
      (let [m' (assoc positions pos w)
            b' (T/->Board (apply list (cons [pos w] moves)) m' :Board)
            wins [[P/NW P/W  P/SW] [P/N  P/C  P/S ]
                  [P/NE P/E  P/SE] [P/NW P/N  P/NE]
                  [P/W  P/C  P/E ] [P/SW P/S  P/SE]
                  [P/NW P/C  P/SE] [P/SW P/C  P/NE]]
            pos->plrs
            (fn [diag] (map #(get m' %) diag))
            allEq
            (fn [diag]
              (let [pls (pos->plrs diag)
                    same? (distinct pls)]
                (and (= 1 (count same?)) (not= nil (first same?)))))
            isWin (not= nil (some true? (map allEq wins)))
            isDraw (= (count (keys m')) (count P/positions))]
        (cond
          isWin (T/->GameFinished (T/->FinishedBoard b' (GR/win w) :FinishedBoard) :GameFinished)
          isDraw (T/->GameFinished (T/->FinishedBoard b' (GR/draw) :FinishedBoard) :GameFinished)
          :else (T/->KeepPlaying b' :KeepPlaying))))))

;; instance Move MoveResult MoveResult where
(defmethod  --> :PositionOccupied
  [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(defmethod  --> :KeepPlaying
  [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

(defmethod  --> :GameFinished
  [pos mr]
  (MR/keepPlayingOr mr #(--> pos %) mr))

;; | Return the result of a completed tic-tac-toe game.
(s/defn getResult :- T/GameResultType
  [b :- T/FinishedBoardType]
  (:gr b))

(declare showPositionMap)

;; instance Show Board where
(s/defmethod  T/show :Board :- s/Str
  [b :- T/BoardType]
  (str/join " " [(showPositionMap (:positions b)) "[" (T/show (BL/whoseTurn b)) "to move ]"]))

;; instance Show FinishedBoard where
(s/defmethod  T/show :FinishedBoard :- s/Str
  [fb :- T/FinishedBoardType]
  (let [summary (GR/gameResult T/show "draw" (:gr fb))]
    summary
    (str/join " " [(showPositionMap (:positions (:b fb))) "[[" summary "]]"])))

;; | Shows a board using ASCII notation and substituting the returned string for each position.
;;   k ^ The function returning the string to substitute each position.
(s/defn showEachPosition :- s/Str
  [k :- (s/=> T/PositionType s/Str)]
  (let [z ".===.===.===."
        each [z
              (str "| " (k P/NW) " | " (k P/N ) " | " (k P/NE) " |")
              z
              (str "| " (k P/W ) " | " (k P/C ) " | " (k P/E ) " |")
              z
              (str "| " (k P/SW) " | " (k P/S ) " | " (k P/SE) " |")
              z]]
    (str/join "\n" each)))

;;   k ^ The function returning the string to substitute each position.
(s/defn showEachPositionFlat :- s/Str
  [k :- (s/=> T/PositionType s/Str)]
  (str "1 2 3 4 5 6 7 8 9\n" (str/join " " (map k P/positions))))

(s/defn showLinePosition :- s/Str
  [k :- (s/=> T/PositionType s/Str)]
  (str "|" (k P/NW) (k P/N) (k P/NE) "|" (k P/W) (k P/C) (k P/E) "|" (k P/SW) (k P/S) (k P/SE) "|"))

;; -- not exported
(s/defn pos :- s/Str
  [m :- {T/PositionType T/PlayerType}
   d :- s/Str
   p :- T/PositionType]
  (let [s (get m p)] (if s (PR/toSymbol s) d)))

(s/defn showPositionMap :- s/Str
  [m :- {T/PositionType T/PlayerType}]
  (str ".="  (pos m "?" P/NW) "=.=" (pos m "?" P/N) "=.=" (pos m "?" P/NE)
       "=.=" (pos m "?" P/W)  "=.=" (pos m "?" P/C) "=.=" (pos m "?" P/E)
       "=.=" (pos m "?" P/SW) "=.=" (pos m "?" P/S) "=.=" (pos m "?" P/SE) "=."))

(s/defmethod  BL/showBlock :EmptyBoard :- s/Str
  [b :- T/EmptyBoardType]
  "00")

(s/defmethod  BL/showBlock :Board :- s/Str
  [b :- T/BoardType]
  (let [m (:positions b)]
    (str/join
    "" (map-indexed
        (fn [idx itm]
          (if (pos m nil itm)
            (str (inc idx) (pos m nil itm))
            ""))
        P/positions))))

(s/defmethod  BL/showBlock :FinishedBoard :- s/Str
  [b :- T/FinishedBoardType]
  (BL/showBlock (:b b)))

;; instance BoardLike EmptyBoard where
(defn empty-board
  []
  (T/->EmptyBoard :EmptyBoard))

(defmethod BL/whoseTurn :EmptyBoard [_] PR/Player1)
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
    (if (nil? last-move) PR/Player1 (PR/alternate (second last-move)))))
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
(defmethod BL/whoseTurn :FinishedBoard [_] PR/Nobody)
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

;;; test ;;;

;; (defn tryit []
;;   (->> (empty-board)
;;        (--> P/C)
;;        (--> P/E)
;;        (--> P/NE)
;;        (--> P/SE)
;;        ;; (--> P/SW)
;;        ))
