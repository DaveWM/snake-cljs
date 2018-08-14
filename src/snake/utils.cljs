(ns snake.utils
  (:require
    [snake.constants :refer [game-size snake-step]]))

(defn opposite-dir [d]
  (case d
    :u :d
    :d :u
    :l :r
    :r :l))

(defn out-of-bounds? [x]
  (not (<= 0 x game-size)))

(defn contains-duplicates? [xs]
  (not= xs (distinct xs)))

(defn step-in-direction [[start-x start-y] direction step]
  [(case direction
     :l (- start-x step)
     :r (+ start-x step)
     start-x)
   (case direction
     :u (- start-y step)
     :d (+ start-y step)
     start-y)])

(defn rand-coord []
  (-> (rand-int (/ game-size snake-step))
      (* snake-step)))