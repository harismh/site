(ns harism.ui
  (:require [harism.util :as util]))

(defn anchor [{:keys [href on-click c cl target id]}]
  [:a.text-zinc-900.dark:text-zinc-100.hover:text-sky-400.transition.duration-150
   {:href href
    :class cl
    :on (when on-click {:click on-click})
    :target target
    :id id}
   c])

(defn paragraph [{:keys [c cl]}]
  [:p.text-zinc-900.dark:text-zinc-100.font-serif
   {:class cl}
   c])

(defn heading [{:keys [c cl]}]
  [:h1.text-zinc-900.dark:text-zinc-100
   {:class cl}
   c])

(defn section [& body]
  (into [:div.flex.flex-col.gap-4] body))

(defn unordered-list [& body]
  (into [:ul.flex.flex-col.gap-2] body))

(defn footer [& body]
  (into [:footer.flex.flex-row.gap-4.justify-end.items-center.text-sm.font-sans.text-zinc-500.dark:text-zinc-400]
        body))

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

(defn panel [title & body]
  [:div.panel-slide.fixed.top-0.h-screen.overflow-y-auto.p-10.z-10.bg-zinc-100.dark:bg-zinc-900
   {:style {:transform "translateX(0)"
            :transition "transform 500ms ease-in-out"}
    :replicant/mounting {:style {:transform "translateX(100%)"}}
    :replicant/unmounting {:style {:transform "translateX(100%)"}}
    :class ["w-full" "md:w-1/2"]}
   [:nav.flex.flex-row.justify-between.items-center.mt-12
    (heading {:c title
              :cl ["text-4xl" "font-bold"]})
    (anchor {:href "#"
             :c [:svg {:xmlns "http://www.w3.org/2000/svg"
                       :width "24"
                       :height "24"
                       :viewBox "0 0 24 24"
                       :fill "none"
                       :stroke "currentColor"
                       :stroke-width "2"
                       :stroke-linecap "round"
                       :stroke-linejoin "round"}
                 [:path {:d "M18 6 6 18"}]
                 [:path {:d "m6 6 12 12"}]]
             :cl ["hover:text-sky-400" "transition"]})]
   [:aside.flex.flex-col
    body]])

(defn writing [state]
  (let [route (:route state)
        slug (:slug route)
        content (get-in state [:content slug])]
    (panel
     (:title content)
     (into [:div.prose.prose-lg.dark:prose-invert.max-w-none.font-serif.mt-20]
           (:body content)))))

(defn home [state]
  (let [meta (:meta state)
        positions (:positions meta)
        currents (:currents meta)
        open-source (:open-source meta)]

    [:article.flex.flex-col.gap-20
     (section
      (paragraph
       {:c [:span "Hello, I'm Haris. I'm a Software Engineer of 8 years who led the development of a "
            (anchor {:href "https://flip.cards"
                     :c "learning start up"
                     :cl ["underline" "underline-offset-4"]
                     :target "_blank"})
            [:span " that helped over 2 million users. I now work at "]
            (anchor {:href "https://meetsmore.com"
                     :c "MeetsMore"
                     :cl ["underline" "underline-offset-4"]
                     :target "_blank"})
            [:span ", a Field Service SaaS in Japan. Outside of work, I enjoy reading, writing, running, and dabbling in "]
            (anchor {:href "https://clojure.org"
                     :c "Clojure."
                     :cl ["underline" "underline-offset-4"]
                     :target "_blank"})]}))
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
                      :cl ["w-4/5"]})])]]
     (section
      (heading {:c "Writing"})
      (unordered-list
       (for [{:keys [slug title date url]} (:index state)]
         [:li.flex.items-center.justify-between
          {:key key}
          (paragraph
           {:c (anchor
                {:c title
                 :cl ["underline" "underline-offset-4"]
                 :href (or url (str "#writing/" slug))
                 :target (when url "_blank")})})
          [:div.flex.text-zinc-500.dark:text-zinc-400.gap-4
           [:p (util/format-date date)]]])))
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
                    :cl ["underline" "underline-offset-4" "text-zinc-500" "dark:text-zinc-400"]})
           [:p years]]]))
      (paragraph
       {:c [:span "Download full resume "
            (anchor {:c "here."
                     :cl ["underline" "underline-offset-4"]
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
                 :cl ["underline" "underline-offset-4"]
                 :href url
                 :target "_blank"})})])))
     (section
      (heading {:c "Connect"})
      (paragraph {:c [:span "Reach me at "
                      (anchor {:href "https://github.com/harismh"
                               :c "@harismh"
                               :cl ["underline" "underline-offset-4"]
                               :target "_blank"})
                      [:span " on GitHub or at "]
                      (anchor {:href ""
                               :c ""
                               :cl ["underline" "underline-offset-4"]
                               :id "contact"})]}))
     (section
      (heading {:c "Colophon"})
      (paragraph {:c
                  [:span "Made with "
                   (anchor {:href "https://clojurescript.org"
                            :target "_blank"
                            :cl ["underline" "underline-offset-4"]
                            :c "ClojureScript"}) " and "
                   (anchor {:href "https://replicant.fun"
                            :target "_blank"
                            :cl ["underline" "underline-offset-4"]
                            :c "Replicant"})
                   ". Fonts are Satoshi and Erode. Code is open source at "
                   (anchor {:href "https://github.com/harismh/site-v4"
                            :target "_blank"
                            :cl ["underline" "underline-offset-4"]
                            :c "GitHub"})
                   "."]}))]))

(defn main [state]
  (let [route (:route state)
        at-writing? (util/at-page? route :writing)]
    [:main.grid.md:grid-cols-2.lg:grid-cols-2.lg:w-full.h-screen.overflow-hidden
     [:div.basis-full.bg-zinc-100.dark:bg-zinc-900.text-zinc-100.flex.flex-col.gap-20.p-12.sm:p-20.overflow-y-auto
      {:class "md:basis-1/2"}
      [:nav.flex.flex-row.gap-8.items-center
       (heading {:c "Haris Muhammad" :cl ["flex-1" "text-5xl" "font-bold"]})]

      (home state)

      (footer
       [:p "© 2025"]
       [:p "ハレイス"])]

     [:div#matrix-canvas.basis-full.hidden.md:block
      {:class ["md:basis-1/2"]}]

     (when at-writing?
       (writing state))]))
