(ns noliky.Types
  (:require #?(:clj [schema.core :as s]
               :cljs [schema.core :as s :include-macros true])
            [schema.utils :as su]))

(s/defrecord Player
    [name :- s/Str
     type :- s/Keyword])
(def PlayerType (su/class-schema Player))
(s/defn player :- PlayerType
  [name]
  (->Player name :Player))
(s/def Player1 :- PlayerType (player "Alice"))
(s/def Player2 :- PlayerType (player "Bob"))
(s/def Nobody :- PlayerType (player "Ghost"))

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
(s/defn empty-board :- EmptyBoardType []
  (->EmptyBoard :- :EmptyBoard))

(s/defrecord Board
    [moves :- [(s/pair PositionType "position"
                       PlayerType "player")]
     positions :- {Position Player}
     type :- s/Keyword])
(def BoardType (su/class-schema Board))
(s/defn board :- BoardType [moves positions]
  (->Board moves positions :Board))


(s/defrecord FinishedBoard
    [b :- Board
     gr :- GameResult
     type :- s/Keyword])
(def FinishedBoardType (su/class-schema FinishedBoard))
(s/defn finished-board :- FinishedBoardType [board game-result]
  (->FinishedBoard board game-result :FinishedBoard))

;; (t/defalias Unfinished (t/U UnfinishedEmpty UnfinishedBoard))
(s/defrecord UnfinishedEmpty
    [b :- Board
     type :- s/Keyword])
(def UnfinishedEmptyType (su/class-schema UnfinishedEmpty))

(s/defrecord UnfinishedBoard
    [b :- Board
     type :- s/Keyword])
(def UnfinishedBoardType (su/class-schema UnfinishedBoard))

;; (t/defalias Unempty (t/U UnemptyFinished UnemptyBoard))
(s/defrecord UnemptyBoard
    [b :- Board
     type :- s/Keyword])
(def UnemptyBoardType (su/class-schema UnemptyBoard))

(s/defrecord UnemptyFinished
    [b :- Board
     type :- s/Keyword])
(def UnemptyFinishedType (su/class-schema UnemptyFinished))

;; (t/defalias MoveResult (t/U PositionOccupied KeepPlaying GameFinished))
(s/defrecord PositionOccupied [type :- s/Keyword])
(def PositionOccupiedType (su/class-schema PositionOccupied))
(s/defn position-occupied :- PositionOccupiedType []
  (->PositionOccupied :PositionOccupied))

(s/defrecord KeepPlaying
    [board :- Board
     type :- s/Keyword])
(def KeepPlayingType (su/class-schema KeepPlaying))
(s/defn keep-playing :- KeepPlayingType
  [board :- BoardType]
  (->KeepPlaying board :KeepPlaying))

(s/defrecord GameFinished
    [board :- FinishedBoardType
     type :- s/Keyword])
(def GameFinishedType (su/class-schema GameFinished))
(s/defn game-finished :- GameFinishedType
  [board :- FinishedBoardType]
  (->GameFinished board :GameFinished))

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


(s/defn error [msg :- s/Str]
  (throw #?(:clj (Exception. msg)
            :cljs (js/Error. msg))))
(s/defn abstract [s :- s/Any] (error (str "abstract " s)))
(s/defn undefined [] (abstract "TBI"))

(defmulti show (fn [x] (:type x)))
(s/defmethod ^:always-validate show :default :- s/Str
  [x :- PlayerType] (abstract "show"))

(defprotocol strategy
  ;; this -> Board
  (first-move [this])
  ;; this -> Board -> Position
  (next-move [this board]))
