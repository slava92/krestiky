(ns noliky.Types
  (:require #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.utils :as su]))

(s/defrecord Player
    [name :- s/Str
     type :- s/Keyword])
(def PlayerType (su/class-schema Player))

(s/defrecord Position
    [pos :- s/Str
     type :- s/Keyword])
(def PositionType (su/class-schema Position))

(s/defrecord GameResult
    [result :- s/Keyword
     player :- Player
     type :- s/Keyword])
(def GameResultType (su/class-schema GameResult))

(s/defrecord EmptyBoard
    [type :- s/Keyword])
(def EmptyBoardType (su/class-schema EmptyBoard))

(def MoveType (s/pair PositionType "position"
                      PlayerType "player"))
(s/defrecord Board
    [moves :- [MoveType]
     positions :- {Position Player}
     type :- s/Keyword])
(def BoardType (su/class-schema Board))

(s/defrecord FinishedBoard
    [b :- Board
     gr :- GameResult
     type :- s/Keyword])
(def FinishedBoardType (su/class-schema FinishedBoard))

;; (t/defalias Unfinished (t/U UnfinishedEmpty UnfinishedBoard))
(s/defrecord UnfinishedEmpty
    [b :- Board
     type :- s/Keyword])
(def UnfinishedEmptyType (su/class-schema UnfinishedEmpty))

(s/defrecord UnfinishedBoard
    [b :- Board
     type :- s/Keyword])
(def UnfinishedBoardType (su/class-schema UnfinishedBoard))

(def UnfinishedType
  (s/conditional #(= (:type %) :UnfinishedEmpty) UnfinishedEmptyType
                 #(= (:type %) :UnfinishedBoard) UnfinishedBoardType))

;; (t/defalias Unempty (t/U UnemptyFinished UnemptyBoard))
(s/defrecord UnemptyBoard
    [b :- Board
     type :- s/Keyword])
(def UnemptyBoardType (su/class-schema UnemptyBoard))

(s/defrecord UnemptyFinished
    [b :- Board
     type :- s/Keyword])
(def UnemptyFinishedType (su/class-schema UnemptyFinished))

(def UnemptyType
  (s/conditional #(= (:type %) :UnemptyBoard) UnemptyBoardType
                 #(= (:type %) :UnemptyFinished) UnemptyFinishedType))

;; (t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))
(s/defrecord PositionOccupied [type :- s/Keyword])
(def PositionOccupiedType (su/class-schema PositionOccupied))

(s/defrecord KeepPlaying
    [board :- Board
     type :- s/Keyword])
(def KeepPlayingType (su/class-schema KeepPlaying))

(s/defrecord GameFinished
    [board :- FinishedBoardType
     type :- s/Keyword])
(def GameFinishedType (su/class-schema GameFinished))

(def MoveResultType
  (s/conditional #(= (:type %) :PositionOccupied) PositionOccupiedType
                 #(= (:type %) :KeepPlaying) KeepPlayingType
                 #(= (:type %) :GameFinished) GameFinishedType))

;; (t/defalias TakenBack (t/U TakeBackIsEmpty TakeBackIsBoard))
(s/defrecord TakeBackIsEmpty [type :- s/Keyword])
(def TakeBackIsEmptyType (su/class-schema TakeBackIsEmpty))

(s/defrecord TakeBackIsBoard
    [board :- Board
     type :- s/Keyword])
(def TakeBackIsBoardType (su/class-schema TakeBackIsBoard))

(def TakenBackType
  (s/conditional #(= (:type %) :TakeBackIsEmpty) TakeBackIsEmptyType
                 #(= (:type %) :TakeBackIsBoard) TakeBackIsBoardType))

(defprotocol strategy
  ;; this -> Board
  (first-move [this])
  ;; this -> Board -> Position
  (next-move [this board]))

(s/defn error [msg :- s/Str]
  (throw #?(:clj (Exception. msg)
            :cljs (js/Error. msg))))
(s/defn abstract [s :- s/Any] (error (str "abstract " s)))
(s/defn undefined [] (abstract "TBI"))

(defmulti show (fn [x] (:type x)))
(s/defmethod ^:always-validate show :default :- s/Str
  [x :- PlayerType] (abstract "show"))
