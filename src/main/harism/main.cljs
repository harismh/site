(ns harism.main
  (:require
   ["react" :as react]
   ["react-dom/client" :refer [createRoot]]
   [reagent.core :as r]
   [harism.util :as util]
   [harism.ui :as ui]))

(defonce root (createRoot (js/document.getElementById "root")))

(defonce *current-route (r/atom nil))

(defn ^:dev/after-load render-main!
  []
  (.render root
           (r/as-element
            [:> react/Suspense
             {:fallback (r/as-element ui/spinner-fs)}
             [ui/main *current-route]])))

(defn reset-fragment! []
  (when-let [fragment (:fragment (util/parse-url
                                  (str (.-location js/window))))]
    (reset! *current-route
            (util/keywordize-fragment fragment))))

(defn init-router! []
  (reset-fragment!)
  (js/window.addEventListener "hashchange" reset-fragment!))

(defn hydrate-email! []
  (when-let [a$ (js/document.getElementById "contact")]
    (set! (.-href a$) (str "mailto:" "hello@harism.dev"))
    (set! (.-innerText a$) (str "hello" "@harism.dev."))))

(defn ^:export init []
  (init-router!)
  (render-main!)
  (js/setTimeout
   (fn []
     (hydrate-email!)
     (ui/render-knot! {:canvas-id "three-canvas"
                       :width (get (ui/canvas-dims) 0)
                       :height (get (ui/canvas-dims) 1)}))
   100))