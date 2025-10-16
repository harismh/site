(ns harism.main
  (:require
   [replicant.dom :as r]
   [harism.util :as util]
   [harism.ui :as ui]
   [harism.matrix :as matrix]))

(defonce store (atom {}))

(defn fetch-content! [slug]
  (when-not (get-in (deref store) [:content slug])
    (util/fetch-edn
     (str "resources/content/" slug ".edn")
     (fn [edn] (swap! store assoc-in [:content slug] edn)))))

(defn route-fragment! []
  (when-let [fragment (:fragment (util/parse-url
                                  (str (.-location js/window))))]
    (let [route (util/keywordize-fragment fragment)]
      (swap! store assoc :route route)
      (when (util/at-page? route :writing)
        (fetch-content! (:slug route))))))

(defn init-router! []
  (route-fragment!)
  (js/window.addEventListener
   "hashchange"
   route-fragment!))

(defn init-meta! []
  (util/fetch-edn
   "resources/meta.edn"
   (fn [edn] (swap! store assoc :meta edn))))

(defn init-content! []
  (util/fetch-edn
   "resources/content/index.edn"
   (fn [edn] (swap! store assoc :index edn))))

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
  (init-content!)
  (render! (deref store))

  (add-watch
   store ::re-render
   (fn [_ _ _ next]
     (render! next)))

  (js/requestAnimationFrame
   (fn []
     (hydrate-email!)
     (render-matrix!)))
