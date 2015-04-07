(ns krestiky.misc
  (:require [clojure.core.typed :as t]))

;; fj.P1
;; (t/defalias P1
;;   (TFn [[x :variance :covariant]] (t/Option x)))

;; (t/defprotocol P1
;;   ([a] _1 [this :- P1] :- a))

;; (t/ann y P1)
;; (defn y [_] nil)

(t/defprotocol Monad
  ([a] pure [this :- Monad v :- a])
  ([a b m] >>= [this :- Monad m :- Monad
                f :- (t/Fn a -> (m a) -> (m b))]))

;; (t/defn :forall [x] some??
;;   [a :- x] :- boolean (not= a nil))
;; (t/ann clojure.core/map (t/All [a b] [[a -> b] (t/Seqable a) -> (t/Seqable b)]))
;; (t/ann clojure.core/partition-all (t/All [a] [int (t/Seqable a) -> (t/Seqable (t/Seqable a))]))
;; (t/ann clojure.core/interpose (t/All [a] [a (t/Seqable a) -> (t/Seqable a)]))
