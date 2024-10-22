(ns harism.main
  (:require
   ["react-dom/client" :refer [createRoot]]
   [reagent.core :as r]
   [harism.util :as util]
   [harism.ui :as ui]))

(defonce root (createRoot (js/document.getElementById "root")))

(defonce *current-route (r/atom nil))

(defn render-main! []
  (.render root 
    (r/as-element [ui/main *current-route])))

(defn re-render! ^:dev/after-load []
  (render-main!))

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

(defn init []
  (do
    (init-router!)
    (render-main!)
    (js/setTimeout
      (fn []
        (hydrate-email!)
        (ui/render-knot! {:canvas-id "three-canvas" 
                          :width (get (ui/canvas-dims) 0)
                          :height (get (ui/canvas-dims) 1)}))
      100)))
