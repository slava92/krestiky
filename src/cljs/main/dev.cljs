(ns main.dev)

;;; This is a reload helper for piggiback/weasel

(defn refresh []
  (set! (.-href (.-location js/window))
        (.-href (.-location js/window))))
