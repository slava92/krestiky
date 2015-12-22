(ns noliky.Chase
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.GameResult :as G]
            [noliky.GameSpace :as GS]
            [noliky.MoveResult :as M]
            [noliky.Player :as PL]
            [noliky.Position :as P]
            [noliky.Types :as T]
            #?(:clj [schema.core :as s]
               :cljs [schema.core :as s
                      :include-macros true])
            [clojure.pprint :as p]
            [clojure.string :as str]))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (let [board (B/empty-board)
            tag (BL/showBlock board)
            moves (get GS/moves tag)
            pos (rand-nth moves)]
        (M/keepPlaying (B/--> pos board))))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [tag (BL/showBlock board)
            moves (get GS/moves tag)]
        (rand-nth moves)))))

;;;;;;;;;;;;;;; testing ;;;;;;;;;;;;;;;;;;;;

(s/defn show-moves :- s/Str
  [fb :- T/FinishedBoardType]
  (let [moves (get-in fb [:b :moves])
        result (:gr fb)
        x (G/playerGameResult "1" "2" "0" result)]
    (str/join
     ""
     (concat
      (map
       (fn [[pos player]]
         (str (P/index-of pos) (PL/toSymbol player)))
       moves)
      [" " x]))))

(s/defn nstep :- T/FinishedBoardType
  [board :- T/BoardType]
  (let [player (BL/whoseTurn board)
        ;; _ (s/validate T/PlayerType player)
        attempts (map #(B/--> % board) P/positions)
        ;; _ (s/validate [T/MoveResultType] attempts)
        ;; results :- [FinishedBoardType]
        finished-boards (filter
                         some?
                         (map #(M/foldMoveResult nil (constantly nil) identity %)
                              attempts))
        ;; _ (s/validate [T/FinishedBoardType] finished-boards)
        wins (when (seq finished-boards)
               (filter #(= player (get-in % [:gr :player])) finished-boards))
        ;; _ (s/validate [T/FinishedBoardType] wins)
        ]
    (when (seq finished-boards)
      (doseq [w finished-boards]
        (p/pprint (show-moves w))))
    (if (seq wins)
      (first wins)
      (let [fbs (filter
                 some?
                 (map #(M/foldMoveResult nil nstep identity %)
                      attempts))
            wins' (when (seq fbs)
                    (filter #(= player (get-in % [:gr :player])) fbs))
            draws' (when-not (seq wins')
                     (filter #(= G/Draw (:gr %)) fbs))]
        (cond
          (seq wins') (rand-nth wins')
          (seq draws') (rand-nth draws')
          :else (rand-nth fbs))))))

(def b1 (T/first-move deep-thought))
