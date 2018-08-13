(ns snake.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

(enable-console-print!)

(def game-size 500)

(defonce game (p/create-game game-size game-size))
(def default-snake {:head       [100 50]
                    :layout     (vec (repeat 10 :l))
                    :direction  :r
                    :max-length 10})
(def initial-state {:snake                default-snake
                    :time-between-updates 100
                    :last-update-time     0
                    :food                 nil
                    :score 0})
(defonce state (atom initial-state))

(def snake-step 25)

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

(defn make-segment [x y]
  [:fill {:color "green"}
   (let [size (- snake-step 5)]
     [:rect {:x      (- x (/ size 2))
             :y      (- y (/ size 2))
             :width  (- snake-step 5)
             :height (- snake-step 5)}])])

(defn rand-coord []
  (-> (rand-int (/ game-size snake-step))
      (* snake-step)))

(defn get-snake-coords
  "returns a seq of coordinates for the snake"
  ([snake] (get-snake-coords snake []))
  ([{:keys [head layout :as snake]} sections]
   (if layout
     (let [[direction & layout-tail] layout
           section-coord (step-in-direction head direction snake-step)
           snake-tail (-> snake
                          (assoc :head (step-in-direction head direction snake-step))
                          (assoc :layout layout-tail))]
       (get-snake-coords snake-tail (conj sections section-coord)))
     sections)))

(defn render-snake [snake]
  (->> snake
       get-snake-coords
       (map (partial apply make-segment))))

(defn render-food [[food-x food-y :as food-coords]]
  (when food-coords
    [:fill {:color "lightblue"}
     [:ellipse {:x food-x :y food-y :width (- snake-step 7) :height (- snake-step 7)}]]))

(def game-over-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (let [{:keys [score]} @state]
        (p/render game
                  [[:image {:name "background.jpg" :x 0 :y 0 :width game-size :height game-size}]
                   [:fill {:color "white"} [:text {:value (str "Game Over\nScore: " score) :x (/ game-size 2) :y (/ game-size 2)
                                                   :size  30 :font "Georgia" :style :bold :halign :center :valign :center}]]])))))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state #(assoc % :snake default-snake)))
    (on-hide [this])
    (on-render [this]
      (let [{:keys [snake last-update-time time-between-updates food score] :as current-state} @state
            time (p/get-total-time game)]
        (p/render game
                  [[:image {:name "background.jpg" :x 0 :y 0 :width game-size :height game-size}]
                   [:fill {:color "lightgray"}
                    [:text {:value score :x (/ game-size 2) :y (/ game-size 2)
                            :size  30 :font "Georgia" :style :bold :halign :center}]]
                   (render-snake snake)
                   (render-food food)])
        (when (> time (+ last-update-time time-between-updates))
          (swap! state update-in [:snake :head] (fn [head]
                                                  (step-in-direction head (:direction snake) snake-step)))
          (swap! state update-in [:snake :layout] #(->> %
                                                        (cons (opposite-dir (:direction snake)))
                                                        (take (:max-length snake))))
          (swap! state assoc :last-update-time time)

          (when (= (:head snake) food)
            (swap! state update-in [:snake :max-length] inc)
            (swap! state update :score (partial + 10))
            (swap! state assoc :food nil))

          (when (or (some out-of-bounds? (:head snake))
                    (->> snake
                         get-snake-coords
                         contains-duplicates?))
            (p/set-screen game game-over-screen))

          (when (nil? food)
            (swap! state assoc :food (repeatedly 2 rand-coord))))))))

(doto game
  (p/start)
  (p/set-screen main-screen))

(.addEventListener js/document "keydown"
                   (fn [event]
                     (condp = (p/get-screen game)
                       game-over-screen (when (= " " (.-key event))
                                          (println "here")
                                          (reset! state initial-state)
                                          (p/set-screen game main-screen))
                       main-screen (swap! state update-in [:snake :direction] #(case (.-key event)
                                                                                 "ArrowDown" :d
                                                                                 "ArrowUp" :u
                                                                                 "ArrowLeft" :l
                                                                                 "ArrowRight" :r
                                                                                 %)))))

