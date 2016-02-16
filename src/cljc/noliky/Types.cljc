(ns noliky.Types
  (:require #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.utils :as su]))

(defrecord Player
    [name
     type])
(def PlayerType (su/class-schema Player))

(defrecord Position
    [pos
     type])
(def PositionType (su/class-schema Position))

(defrecord GameResult
    [result
     player
     type])
(def GameResultType (su/class-schema GameResult))

(defrecord EmptyBoard
    [type])
(def EmptyBoardType (su/class-schema EmptyBoard))

(def MoveType (s/pair PositionType "position" PlayerType "player"))

(defrecord Board
    [moves
     positions
     type])
(def BoardType (su/class-schema Board))
(def NotFinishedBoardType
  (s/conditional #(= (:type %) :EmptyBoard) EmptyBoardType
                 #(= (:type %) :Board) BoardType))

(defrecord FinishedBoard
    [b
     gr
     type])
(def FinishedBoardType (su/class-schema FinishedBoard))
(def NotEmptyBoardType
  (s/conditional #(= (:type %) :Board) BoardType
                 #(= (:type %) :FinishedBoard) FinishedBoardType))

;; (t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))
(defrecord PositionOccupied [type])
(def PositionOccupiedType (su/class-schema PositionOccupied))

(defrecord KeepPlaying
    [board
     type])
(def KeepPlayingType (su/class-schema KeepPlaying))

(defrecord GameFinished
    [board
     type])
(def GameFinishedType (su/class-schema GameFinished))

(def MoveResultType
  (s/conditional #(= (:type %) :PositionOccupied) PositionOccupiedType
                 #(= (:type %) :KeepPlaying) KeepPlayingType
                 #(= (:type %) :GameFinished) GameFinishedType))

;; (t/defalias TakenBack (t/U TakeBackIsEmpty TakeBackIsBoard))
(defrecord TakeBackIsEmpty [type])
(def TakeBackIsEmptyType (su/class-schema TakeBackIsEmpty))

(defrecord TakeBackIsBoard
    [board
     type])
(def TakeBackIsBoardType (su/class-schema TakeBackIsBoard))

(def TakenBackType
  (s/conditional #(= (:type %) :TakeBackIsEmpty) TakeBackIsEmptyType
                 #(= (:type %) :TakeBackIsBoard) TakeBackIsBoardType))

(defprotocol strategy
  ;; this -> Board
  (first-move [this])
  ;; this -> Board -> Position
  (next-move [this board]))

(defn error [msg]
  (throw #?(:clj (Exception. msg)
            :cljs (js/Error. msg))))
(defn abstract [s] (error (str "abstract " s)))
(defn undefined [] (abstract "TBI"))

(defmulti show (fn [x] (:type x)))
(defmethod show :default
  [x] (abstract "show"))
