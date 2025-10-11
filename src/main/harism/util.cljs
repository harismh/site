(ns harism.util
  (:require [clojure.edn :as edn]))

(defn parse-url [url]
  (let [[_ s d p q f]
        (re-matches #"([a-z]+)://([^/?#]+)(/[^?#]*)?(\?[^#]*)?(#.*)?" url)]
    {:schema s
     :domain d
     :path p
     :query q
     :fragment f}))

(defn keywordize-fragment [f]
  (when-let [k (re-find #"#.*" (or f ""))]
    (keyword (subs k 1))))

(defn fetch-edn [url callback]
  (let [promise
        (js/fetch url)
        handler
        (fn [response]
          (if (= (.-status response) 200)
            (.then (.text response)
                   (fn [text] (callback (edn/read-string text))))
            (throw (js/Error. response))))]
    (.then promise handler)))
