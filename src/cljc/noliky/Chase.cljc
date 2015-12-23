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
            [clojure.java.io :as io]
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

(def pos-of-first (comp :pos first))

(s/defn show-moves :- s/Str
  [fb :- T/FinishedBoardType]
  (let [moves (get-in fb [:b :moves])
        result (:gr fb)
        x (G/playerGameResult "X" "O" "." result)]
    (apply
     str
     (concat
      ["   \""]
      (map
       (fn [[pos player]]
         (str (P/index-of pos) (PL/toSymbol player)))
       (sort-by pos-of-first moves))
      ["\" \"" x "\","]))))

(s/defn walk! :- [T/FinishedBoardType]
  [board :- T/BoardType]
  (s/validate T/BoardType board)
  (let [attempts (map #(B/--> % board) P/positions)
        done (mapcat #(M/foldMoveResult [nil] walk! vector %) attempts)
        done' (filter some? done)]
    (when (seq done')
      (doseq [w done']
        (println (show-moves w))))
    done'))

(defn genall []
  (with-open [w (io/writer "FullSpace.clj")]
    (binding [*out* w]
      (doseq [board
              (map #(B/--> % (B/empty-board)) P/positions)]
        (println "(def moves")
        (println "  {")
        (walk! (M/keepPlaying board))
        (println "  })")))))

(def b1 (T/first-move deep-thought))
