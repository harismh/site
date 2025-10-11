(ns harism.ui
  (:require [harism.three :as three]))

(defn anchor [{:keys [href on-click c cl target id]}]
  [:a.text-zinc-900.dark:text-zinc-100.hover:text-sky-400.transition.duration-150
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

(defn unordered-list [& body]
  `[:ul.flex.flex-col.gap-2 ~@body])

(defn footer [& body]
  `[:footer.flex.flex-row.gap-4.justify-end.items-center.text-sm.font-sans.text-zinc-500.dark:text-zinc-400
    ~@body])

(defn canvas-dims []
  [(- (/ (.-innerWidth js/window) 2) 25)
   (/ (.-innerHeight js/window) 1.75)])

(defn color-scheme []
  (if (.-matches (.matchMedia js/window "(prefers-color-scheme: dark)"))
    :dark
    :light))

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
          color    "#55A5DA"
          effect   (three/ascii-effect {:color color})
          composer (three/composer renderer scene camera effect)
          orbit    (three/orbit-controls camera (.-domElement renderer))
          _        (js/window.addEventListener
                    "resize"
                    (fn []
                      (let [[width' height'] (canvas-dims)]
                        (.set (.-position camera) 0 0 (* width' 0.05))
                        (set! (.-aspect camera) (/ width' height'))
                        (.updateProjectionMatrix camera)
                        (.setSize composer width' height'))))]
      (.set (.-position camera) 0 0 (* width 0.05))
      (set! (.-autoRotate orbit) true)
      (set! (.-autoRotateSpeed orbit) 2)
      (set! (.-enableZoom orbit) false)
      (set! (.-enableDamping  orbit) true)
      (.add scene camera)
      (.add scene mesh)
      (three/animate! orbit composer scene camera)
      :ok)))

(def spinner-fs
  [:div.grid.h-screen.place-items-center.bg-zinc-50.dark:bg-zinc-900
   {:aria-label "Loading..." :role "status"}
   [:svg.h-10.w-10.stroke-zinc-900.dark:stroke-zinc-100
    {:view-box "0 0 256 256"}
    [:line {:x1 "128", :y1 "32", :x2 "128", :y2 "64", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "195.9", :y1 "60.1", :x2 "173.3", :y2 "82.7", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "224", :y1 "128", :x2 "192", :y2 "128", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "195.9", :y1 "195.9", :x2 "173.3", :y2 "173.3", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "128", :y1 "224", :x2 "128", :y2 "192", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "60.1", :y1 "195.9", :x2 "82.7", :y2 "173.3", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "32", :y1 "128", :x2 "64", :y2 "128", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]
    [:line {:x1 "60.1", :y1 "60.1", :x2 "82.7", :y2 "82.7", :stroke-linecap "round", :stroke-linejoin "round", :stroke-width "20"}]]])

(defn todo [_store]
  [:article (paragraph {:c "Under construction."})])

(defn home [store]
  (let [state (deref store)
        meta (:meta state)
        positions (:positions meta)
        currents (:currents meta)
        open-source (:open-source meta)]

    [:article.flex.flex-col.gap-20
     [section
      (paragraph
       {:c [:span "Hello, I'm Haris. I'm a Software Engineer of 8 years who led the development of a "
            (anchor {:href "https://flip.cards"
                     :c "learning start up"
                     :cl "underline underline-offset-4"
                     :target "_blank"})
            [:span " that helped over 2 million users. I now work at "]
            (anchor {:href "https://meetsmore.com"
                     :c "MeetsMore"
                     :cl "underline underline-offset-4"
                     :target "_blank"})
            [:span ", a Field Service SaaS in Japan. Outside of work, I enjoy reading, writing, running, and dabbling in "]
            (anchor {:href "https://clojure.org"
                     :c "Clojure."
                     :cl "underline underline-offset-4"
                     :target "_blank"})]})]
     [:div.flex.flex-col.gap-2
      (heading {:c "Currently"})
      [:div.flex.flex-col.gap-2
       (for [{:keys [key heading title]} currents]
         [:div.flex.flex-row.items-center
          {:key key}
          [:p.text-zinc-500.dark:text-zinc-400.mr-6
           {:class "w-1/5"}
           heading]
          (paragraph {:c title
                      :cl "w-4/5"})])]]
     (section
      (heading {:c "Work Experience"})
      (unordered-list
       (for [{:keys [key title link link-text years]} positions]
         [:li.flex.items-center.justify-between
          {:key key}
          (paragraph {:c title})
          [:div.flex.text-zinc-500.dark:text-zinc-400.gap-4
           (anchor {:href link
                    :target (when link "_blank")
                    :c link-text
                    :cl "underline underline-offset-4 text-zinc-500 dark:text-zinc-400"})
           [:p years]]]))
      (paragraph
       {:c [:span "Download full resume "
            (anchor {:c "here."
                     :cl "underline underline-offset-4"
                     :href "resources/resume.pdf"
                     :target "_blank"})]}))
     (section
      (heading {:c "Open Source"})
      (unordered-list
       (for [{:keys [title url]} open-source]
         [:li
          {:key url}
          (paragraph
           {:c (anchor
                {:c title
                 :cl "underline underline-offset-4"
                 :href url
                 :target "_blank"})})])))
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
                            :c "GitHub."})]}))]))

(def routes
  {:home home
   :writing todo
   :projects todo
   :contact todo})

(defn render-route [store]
  (let [state (deref store)
        route (:route state)
        render (or
                (get routes route)
                (:home routes))]
    (render store)))

(defn main [store]
  [:main.grid.md-grid-cols-2.lg:grid-cols-2.lg:w-full.h-screen
   [:div.basis-full.bg-zinc-50.dark:bg-zinc-900.text-zinc-100.flex.flex-col.gap-20.p-12.sm:p-20
    {:class "sm:basis-1/2"}
    [:nav.flex.flex-row.gap-8.items-center
     (heading {:c "Haris Muhammad" :cl "flex-1 text-5xl font-bold"})]

    (render-route store)

    (footer
     [:p "© 2025"]
     [:p "ハレイス"])]

   [:div.basis-full.bg-zinc-100.dark:bg-zinc-950.text-zinc-900.hidden.lg:flex.justify-center
    {:class "sm:basis-1/2"}
    [:canvas#three-canvas.sticky
     {:style {:top (/ (get (canvas-dims) 1) 2.5)}}]]])
