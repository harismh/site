(ns harism.matrix)

(def characters
  ["ア" "イ" "ウ" "エ" "オ"
   "カ" "キ" "ク" "ケ" "コ"
   "サ" "シ" "ス" "セ" "ソ"
   "タ" "チ" "ツ" "テ" "ト"
   "ナ" "ニ" "ヌ" "ネ" "ノ"
   "ハ" "ヒ" "フ" "ヘ" "ホ"
   "マ" "ミ" "ム" "メ" "モ"
   "ヤ" "ユ" "ヨ"
   "ラ" "リ" "ル" "レ" "ロ"
   "ワ" "ヲ" "ン"
   "0" "1"
   "T" "F"
   "[" "]"
   "(" ")"
   "{" "}"
   "#" "@"])

(defn rand-clamped [min max]
  (+ min (rand-int max)))

(defn create-column [{:keys [index count container-width column-width]}]
  (let [column (.createElement js/document "div")
        x (+ column-width (* (/ index count) (- container-width column-width)))
        speed (+ 8 (rand-int 12))
        delay (* (rand) 2)
        easter-egg? (< (rand) 0.01)
        chars (if easter-egg?
                ["ハ" "レ" "イ" "ス"]
                (repeatedly
                 (rand-clamped 5 20)
                 #(rand-nth characters)))]

    (set! (.-className column) "matrix-column")

    (set! (.-innerHTML column)
          (->> chars
               (map #(str "<div>" % "</div>"))
               (apply str)))

    (set! (.. column -style -left) (str x "px"))
    (set! (.. column -style -animationName) "matrix-fall")
    (set! (.. column -style -animationDuration) (str speed "s"))
    (set! (.. column -style -animationDelay) (str delay "s"))
    (set! (.. column -style -animationIterationCount) "infinite")
    (set! (.. column -style -animationTimingFunction) "linear")

    column))

(defn render-matrix!
  [container-id &
   {:keys [column-width] :or {column-width 40}}]
  (if-let [container (js/document.getElementById container-id)]
    (let [container-width (.-offsetWidth container)
          count (js/Math.floor (/ container-width column-width))]

      (set! (.-innerHTML container) "")
      (.add (.-classList container) "matrix-rain")

      (doseq [i (range count)]
        (.appendChild container
                      (create-column {:index i
                                      :count count
                                      :container-width container-width
                                      :column-width column-width}))))

    (js/console.error "Container not found with id:" container-id)))
