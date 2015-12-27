(ns noliky.full
  (:require [noliky.Board :as B]
            [noliky.BoardLike :as BL]
            [noliky.Chase :as C]
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
            #?(:clj [clojure.java.io :as io])
            [clojure.string :as str]))


;; ["1O4O5X7X8X"]

(def pos-of-first (comp P/pos->idx :pos first))

(s/defn show-moves :- s/Str
  [moves :- [T/MoveType]]
  (s/validate [T/MoveType] moves)
    (apply
     str
     (map
      (fn [[pos player]]
        (str (P/pos->idx (:pos pos)) (PL/toSymbol player)))
      (sort-by pos-of-first moves))))

(s/defn walk! :- [T/FinishedBoardType]
  [board :- T/BoardType]
  (s/validate T/BoardType board)
  (let [attempts (map #(B/--> % board) P/positions)
        done (mapcat #(M/foldMoveResult [] (constantly []) vector %)
                     attempts)]
    #?(:clj
       (when (seq done)
         (doseq [w done]
           (printf "\"%s\" \"%s\",\n"
                   (show-moves (:moves (:b w)))
                   (G/playerGameResult "X" "O" "." (:gr w))))))
    (dorun (mapcat #(M/foldMoveResult [] walk! (constantly []) %)
                   attempts))
    attempts))

(defn gen-all! []
  (doseq [move-result
          (map #(B/--> % (B/empty-board)) P/positions)]
    (let [board (M/keepPlaying move-result)]
      (walk! board))))

#?(:clj
   (defn print-all! []
     (with-open [w (io/writer "FullSpace.clj")]
       (binding [*out* w]
         (println "(def moves")
         (println "  {")
         (gen-all!)
         (println "  })")))))
