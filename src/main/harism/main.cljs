(ns harism.main
  (:require
   [replicant.dom :as r]
   [harism.util :as util]
   [harism.ui :as ui]
   [harism.matrix :as matrix]))

(defonce store (atom {}))

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

(defn render-matrix! []
  (matrix/render-matrix! "matrix-canvas"))

(defn render! [state]
  (r/render
   (js/document.getElementById "root")
   (ui/main state)))

(defn ^:dev/after-load reload! []
  (render! (deref store)))

(defn ^:export init []
  (init-router!)
  (init-meta!)
  (render! (deref store))

  (add-watch
   store ::re-render
   (fn [_ _ _ next]
     (render! next)))

  (js/requestAnimationFrame
   (fn []
     (js/setTimeout
      (fn []
        (hydrate-email!)
        (render-matrix!))
      50))))
