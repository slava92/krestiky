(ns noliky.Chase
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as G]
            [noliky.GameSpace :as GS]
            [noliky.FullSpace :as FS]
            [noliky.Blind :as BS]
            [noliky.MoveResult :as M]
            [noliky.Player :as PL]
            [noliky.Position :as P]
            [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s
                      :include-macros true])
            #?(:clj [clojure.pprint :as p])
            [clojure.string :as str]
            [clojure.set :as set]))

;; (s/defn game-space :- [(s/pair T/PlayerType "winner" #{T/MoveType} "board")]
;;   [board :- T/BoardType]
;;   (let [moves (:moves board)]
;;     (map
;;      (fn [[plr mvs]] [plr (set/difference mvs moves)])
;;      (filter (s/fn [[_ mvs] :- (s/pair T/PlayerType "winner" #{T/MoveType} "board")]
;;                (set/subset? moves mvs))
;;              FS/boards))))

(def CollatedType [(s/one [T/FinishedBoardType] "wins")
                   (s/one [T/FinishedBoardType] "draws")
                   (s/one [T/FinishedBoardType] "losses")])

(s/defn collate-boards :- CollatedType
  [player :- T/PlayerType
   boards :- [T/FinishedBoardType]]
  (reduce
   (fn [[wins draws losses] finished]
     (G/gameResult
      #(if (= player %)
         [(conj wins finished) draws losses]
         [wins draws (conj losses finished)])
      [wins (conj draws finished) losses]
      (:gr finished)))
   [[] [] []]
   boards))

(s/defn choose-bests :- [T/FinishedBoardType]
  [player :- T/PlayerType
   boards :- [(s/maybe T/FinishedBoardType)]]
  (let [fbs (collate-boards player (filter some? boards))
        [wins draws losses] fbs]
    (cond
      (not-empty wins) wins
      (not-empty draws) draws
      :else losses)))

(declare all-moves)
(s/defn one-move :- (s/maybe T/FinishedBoardType)
  [board :- T/BoardType
   position :- T/PositionType]
  (let [move (B/--> position board)
        over (M/foldMoveResult
              nil
              (constantly nil)
              identity
              move)]
    (if over
      over
      (M/foldMoveResult
       nil
       #(->> %
             all-moves
             (choose-bests (BL/whoseTurn board))
             first)
       nil
       move))))

(s/defn all-moves :- [(s/maybe T/FinishedBoardType)]
  [board :- T/NotFinishedBoardType]
  (map #(one-move board %) P/positions))

(s/defn best-pos :- T/PositionType
  [player :- T/PlayerType
   fbs :- [(s/maybe T/FinishedBoardType)]]
  (let [one-board (rand-nth (choose-bests player fbs))
        bps (map vector P/positions fbs)]
    (ffirst (filter #(= one-board (second %)) bps))))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (T/first-move BS/deep-thought))

    ;; this -> Board -> Position
    (next-move [this board]
      (best-pos (BL/whoseTurn board)
                (all-moves board)))))

;;;;;;;;;;;; test ;;;;;;;;;;;;;;;;;

(def b1 (T/first-move deep-thought))

(defn tryit []
  (->> (B/empty-board)
       (B/--> P/C)
       (B/--> P/N)
       (B/--> P/NW)
       ;; (B/--> P/NE)
       ))
(def b2 (:board (tryit)))

(defn tst [b]
  #?(:clj (println (BL/showBoard b)))
  (T/next-move deep-thought b))
