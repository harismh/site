(ns harism.main
  (:require
   ["react" :as react]
   ["react-dom/client" :refer [createRoot]]
   [reagent.core :as r]
   [harism.util :as util]
   [harism.ui :as ui]))

(defonce root (createRoot (js/document.getElementById "root")))

(defonce store (r/atom {}))

(defn ^:dev/after-load render-main!
  []
  (.render root
           (r/as-element
            [:> react/Suspense
             {:fallback (r/as-element ui/spinner-fs)}
             [ui/main store]])))

(defn reset-fragment! []
  (when-let [fragment (:fragment (util/parse-url
                                  (str (.-location js/window))))]
    (swap! store assoc :route (util/keywordize-fragment fragment))))

(defn init-router! []
  (reset-fragment!)
  (js/window.addEventListener "hashchange" reset-fragment!))

(defn init-meta! []
  (util/fetch-edn
   "resources/meta.edn"
   (fn [edn] (swap! store assoc :meta edn))))

(defn hydrate-email! []
  (when-let [a$ (js/document.getElementById "contact")]
    (set! (.-href a$) (str "mailto:" "hello@harism.dev"))
    (set! (.-innerText a$) (str "hello" "@harism.dev."))))

(defn render-knot! []
  (let [[width height] (ui/canvas-dims)]
    (ui/render-knot! {:canvas-id "three-canvas"
                      :width width
                      :height height})))

(defn ^:export init []
  (init-router!)
  (init-meta!)
  (render-main!)

  (js/setTimeout
   (fn []
     (hydrate-email!)
     (render-knot!))
   100))
