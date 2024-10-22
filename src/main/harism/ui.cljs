(ns harism.ui
  (:require [harism.three :as three]))

(defn anchor [{:keys [href on-click c cl target id]}]
  [:a.text-zinc-900.dark:text-zinc-100.hover:text-yellow-500.transition.duration-150
   {:href href 
    :class cl
    :on-click on-click 
    :target target
    :id id} 
   c])

(defn paragraph [{:keys [c cl]}]
  [:p.text-zinc-900.dark:text-zinc-100.font-serif
   {:class (str "text-lg/8 " cl)} 
   c])

(defn heading [{:keys [c cl]}]
  [:h1.text-zinc-900.dark:text-zinc-100
   {:class cl} 
   c])

(defn section [& body]
  `[:div.flex.flex-col.gap-4 ~@body])

(defn canvas-dims []
  [(- (/ (.-innerWidth js/window) 2) 50) 
   (/ (.-innerHeight js/window) 1.5)])

(defn render-knot! [{:keys [width height canvas-id]}]
  (when (js/document.getElementById canvas-id) 
    (let [scene    (three/scene)
          camera   (three/camera 30 (/ width height) 1 100)
          geometry (three/torus-knot-geometry
                     {:radius 4
                      :tube 0.2
                      :tubular-segments 512
                      :radial-segments 256
                      :p 7
                      :q 10})
          material (three/mesh-material)
          mesh     (three/mesh geometry material)
          renderer (three/renderer width height canvas-id)
          orbit    (three/orbit-controls camera (.-domElement renderer))
          _        (js/window.addEventListener 
                     "resize"
                     (fn []
                       (let [[width' height'] (canvas-dims)]
                         (set! (.-aspect camera) (/ width' height'))
                         (.updateProjectionMatrix camera)
                         (.setSize renderer width' height'))))]
      (do
        (.set (.-position camera) 0 0 (* width 0.05))
        (set! (.-autoRotate orbit) true)
        (set! (.-autoRotateSpeed orbit) 2)
        (set! (.-enableZoom orbit) false)
        (set! (.-enableDamping  orbit) true)
        (.add scene camera)
        (.add scene mesh)
        (three/animate! orbit renderer scene camera)
        :ok))))

(def todo
  [:article (paragraph {:c "Under construction."})])

(def home
  [:<> 
   [:article.flex.flex-col.gap-20
    [section
     (paragraph 
       {:c [:span "Hello, my name is Haris. Being a software engineer for
                   over 8 years, I had many oppertunities to work
                   on a variety of projects. Notably, as CTO at " 
            (anchor {:href "https://flip.cards"
                     :c "FLIP"
                     :cl "underline underline-offset-4"
                     :target "_blank"})
            [:span ", I led the development of a learning app 
                          that served over 2 million users."]]})
                      
     (paragraph
       {:c "I now consult independently, specializing in 
            full-stack Clojure and functional UIs. I studied 
            Computer Science at Hack Reactor in San Francisco
            and at the University of Michigan in Ann Arbor. 
            Outside of work, I enjoy reading, writing, running, 
            and brewing ☕️."})]
    [:div.flex.flex-col.gap-2
     (heading {:c "Currently"})
     [:div.flex.flex-row 
      [:p.text-zinc-900.dark:text-zinc-100 
       {:class "w-1/5"} 
       "Reading"]
      (paragraph {:c "Shape, the Hidden Geometry of Information, 
                      Strategy, Biology, Democracy, and Everything 
                      Else by Jordan Ellenberg."
                  :cl "w-4/5"})]
     [:div.flex.flex-row 
      [:p.text-zinc-900.dark:text-zinc-100 
       {:class "w-1/5"} 
       "Watching"]
      (paragraph {:c "Attention in LLM Transformers, 
                      Visually Explained by 3Blue1Brown."
                  :cl "w-4/5"})]
     [:div.flex.flex-row 
      [:p.text-zinc-900.dark:text-zinc-100 
       {:class "w-1/5"} 
       "Listening"]
      (paragraph {:c "Tears of the Star by Casiopea."
                  :cl "w-4/5"})]]
    (section
      (heading {:c "Work Experience"})
      [:ul
       [:li.flex.items-center.justify-between 
        (paragraph {:c "Chief Technology Officer"})
        [:div.flex.text-zinc-400.gap-4
         (anchor {:href "https://flip.cards" 
                  :target "_blank" 
                  :c "FLIP" 
                  :cl "text-zinc-400"})
         [:p "2021-2024"]]]
       [:li.flex.items-center.justify-between 
        (paragraph {:c "Client Development Lead"})
        [:div.flex.text-zinc-400.gap-4
         [:p "2020-2021"]]]
       [:li.flex.items-center.justify-between 
        (paragraph {:c "Fullstack React Developer"})
        [:div.flex.text-zinc-400.gap-4
         [:p "2019-2020"]]]]
      (paragraph 
        {:c [:span "Download full resume " 
             (anchor {:c "here." 
                      :cl "underline underline-offset-4"
                      :href "resources/resume.pdf"
                      :target "_blank"})]}))
    (section
      (heading {:c "Open Source"})
      [:ul
       [:li
        (paragraph 
          {:c (anchor 
                {:c "React Native Web Track Player" 
                 :cl "underline underline-offset-4"
                 :href "https://github.com/harismh/react-native-webview-track-player"
                 :target "_blank"})})]
       [:li
        (paragraph 
          {:c (anchor 
                {:c "ClojureScript Project Starter" 
                 :cl "underline underline-offset-4"
                 :href "https://github.com/harismh/utsb-cljs-starter"
                 :target "_blank"})})]
       [:li
        (paragraph 
          {:c (anchor 
                {:c "Data-driven UIs w/ Dumdom"
                 :cl "underline underline-offset-4"
                 :href "https://github.com/harismh/todomvc-dumdom"
                 :target "_blank"})})]])
    (section
      (heading {:c "Connect"})
      (paragraph {:c [:span "Reach me at " 
                      (anchor {:href "https://github.com/harismh"
                               :c "@harismh"
                               :cl "underline underline-offset-4"
                               :target "_blank"})
                      [:span " on GitHub or at "]
                      (anchor {:href ""
                               :c ""
                               :cl "underline underline-offset-4"
                               :id "contact"})]}))
    (section
      (heading {:c "Colophon"})
      (paragraph {:c 
                  [:span "Made with ClojureScript. Fonts are Untitled Sans 
                          from the Klim Foundry and Plex Sans from IBM. 
                          Torus knot made using Three.js. Code is open
                          source at "
                   (anchor {:href "https://github.com/harismh/site-v4"
                            :target "_blank"
                            :cl "underline underline-offset-4"
                            :c "GitHub."})]}))]])

(def routes
  {:home home
   :writing todo
   :projects todo
   :contact todo})

(defn route-to! [atom route]
  (fn [] (reset! atom route)))

(defn render-route [current-route]
  (or 
    (get routes current-route)
    (:home routes)))

(defn main [*current-route]
  [:main.grid.md-grid-cols-2.lg:grid-cols-2.lg:w-full.h-screen
   [:div.basis-full.bg-zinc-50.dark:bg-zinc-900.text-zinc-100.flex.flex-col.gap-20.p-12.sm:p-20
    {:class "sm:basis-1/2"}
    [:nav.flex.flex-row.gap-8.items-center
     (heading {:c "Haris Muhammad" :cl "flex-1 text-5xl font-bold"})
     #_(anchor {:href "#writing" :on-click (route-to! *current-route :writing) :c "Writing"})
     #_(anchor {:href "#resume" :on-click (route-to! *current-route :resume) :c "Resume"})
     #_(anchor {:href "#contact" :on-click (route-to! *current-route :contact) :c "Contact"})]
    [:section
     (render-route (deref *current-route))]
    [:footer.flex.flex-row.gap-4.justify-end
     [:p.text-sm.font-sans.text-zinc-400 "© 2024"]
     [:img {:src "resources/signature.svg"}]]]
   [:div.basis-full.bg-zinc-100.dark:bg-zinc-950.text-zinc-900.hidden.lg:flex.justify-center
    {:class "sm:basis-1/2"}
    [:canvas#three-canvas.sticky
     {:style {:top (/ (get (canvas-dims) 1) 4)}}]]])
    