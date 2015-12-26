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
            [clojure.string :as str]))


(def pos-of-first (comp P/pos->idx :pos first))

(s/defn show-moves :- s/Str
  [moves :- [T/MoveType]]
    (apply
     str
     (map
      (fn [[pos player]]
        (str (P/pos->idx (:pos pos)) (PL/toSymbol player)))
      (sort-by pos-of-first moves))))

;; use s/Any for char since it is not in schema
(s/defn tag->move :- T/MoveType
  [pos :- s/Any plr :- s/Any]
  [(P/char->pos pos) (PL/from-symbol plr)])

(s/defn tag->moves :- [T/MoveType]
  [tag :- s/Str]
  (loop [[[pos plr] t] (split-at 2 tag) rs []]
    (if pos
      (recur (split-at 2 t) (conj rs (tag->move pos plr)))
      rs)))

(s/defn valid-moves :- [T/PositionType]
  [player :- T/PlayerType
   moves :- [T/MoveType]]
  (s/validate T/PlayerType player)
  (s/validate [T/MoveType] moves)
  (map first (filter #(= player (second %)) moves)))

(s/defn game-space :- {T/PlayerType #{T/PositionType}}
  [board :- T/BoardType]
  (let [player (BL/whoseTurn board)
        moves-tag (show-moves (:moves board))
        game-tags (filter #(re-find (re-pattern moves-tag) %)
                          (keys FS/moves))
        _ (prn [player moves-tag])
        games (map
               #(vector
                 ;; game-tag -> player-symbol
                 ;; outcome (X,O,.)
                 (PL/from-symbol (get FS/moves %))
                 (valid-moves player ;; valid moves
                              (tag->moves (str/replace-first % moves-tag ""))))
               game-tags)
        ;; game-sets :: {Player [(Player, [Position])]}
        game-sets (group-by first games)
        gs (reduce
            (fn [acc plr]
              (update acc plr #(into #{} (mapcat second %))))
            game-sets
            (keys game-sets))]
    gs))

(def deep-thought
  (reify T/strategy

    ;; this -> Board
    (first-move [this]
      (T/first-move BS/deep-thought))

    ;; this -> Board -> Position
    (next-move [this board]
      (let [gs (game-space board)
            player (BL/whoseTurn board)
            player2 (BL/whoseNotTurn board)
            wins (get gs player)
            draws (get gs PL/Nobody)
            losts (get gs player2)]
        (cond
          wins (rand-nth (vec wins))
          draws (rand-nth (vec draws))
          losts (rand-nth (vec losts))
          :else (T/abstract (str "corrupted game-space: "
                                 (pr-str gs))))))))
    
;;;;;;;;;;;;;;; testing ;;;;;;;;;;;;;;;;;;;;
(defn tst []
  (let [some (T/next-move deep-thought (T/first-move deep-thought))]
    some))

;; ["1O4O5X7X8X"]

;; (s/defn walk! :- [T/FinishedBoardType]
;;   [board :- T/BoardType]
;;   (s/validate T/BoardType board)
;;   (let [attempts (map #(B/--> % board) P/positions)
;;         done (filter some?
;;                      (map #(M/foldMoveResult nil (constantly nil) identity %)
;;                           attempts))
;;         _ (when (seq done)
;;             (doseq [w done]
;;               (println (show-moves w))))
;;         done' (filter some?
;;                       (mapcat #(M/foldMoveResult [nil] walk! (constantly [nil]) %)
;;                               attempts))]
;;     (dorun done')
;;     []))

;; (defn genall! []
;;   (with-open [w (io/writer "FullSpace.clj")]
;;     (binding [*out* w]
;;       (println "(def moves")
;;       (println "  {")
;;       (doseq [move-result
;;               (map #(B/--> % (B/empty-board)) P/positions)]
;;         (let [board (M/keepPlaying move-result)]
;;           (walk! board)))
;;       (println "  })"))))

;; (def b1 (T/first-move deep-thought))

;; (defn tryit []
;;   (->> (B/empty-board)
;;        (B/--> P/C)
;;        (B/--> P/E)
;;        (B/--> P/NE)
;;        (B/--> P/SE)
;;        (B/--> P/SW)))
;; (def b2 (:board (tryit)))
