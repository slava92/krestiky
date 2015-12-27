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
            [clojure.string :as str]
            [clojure.set :as set]))

(s/defn game-space :- [(s/pair T/PlayerType "winner" #{T/MoveType} "board")]
  [board :- T/BoardType]
  (let [moves (:moves board)]
    (map
     (fn [[plr mvs]] [plr (set/difference mvs moves)])
     (filter (s/fn [[_ mvs] :- (s/pair T/PlayerType "winner" #{T/MoveType} "board")]
               (set/subset? moves mvs))
             FS/boards))))

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
            wins (filter #(= player (first %)) gs)
            losts (filter #(= player2 (first %)) gs)
            draws (filter #(= PL/Nobody (first %)) gs)]
        (first
         (rand-nth
          (filter
           #(= player (second %))
           (seq
            (second
             (cond
               wins (rand-nth (vec wins))
               draws (rand-nth (vec draws))
               losts (rand-nth (vec losts))
               :else (T/abstract (str "corrupted game-space: "
                                      (pr-str gs)))))))))))))

;;;;;;;;;;;; test ;;;;;;;;;;;;;;;;;
(defn tst []
  (let [some (T/next-move deep-thought (T/first-move deep-thought))]
    some))

(def b1 (T/first-move deep-thought))
