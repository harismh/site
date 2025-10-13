(ns harism.util
  (:require [clojure.edn :as edn]
            [clojure.string :as string]))

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
    (let [fragment (subs k 1)
          parts (string/split fragment #"/")]
      (cond-> {:fragment fragment}
        (= 2 (count parts))
        (assoc
         :type (keyword (first parts))
         :slug (second parts))))))

(defn at-page? [route page]
  (= page (:type route)))

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

(def month-names
  ["Jan." "Feb." "Mar." "Apr." "May" "Jun."
   "Jul." "Aug." "Sep." "Oct." "Nov." "Dec."])

(defn format-date [date-string]
  (let [date (js/Date. date-string)
        year (.getFullYear date)
        month (get month-names (.getMonth date))
        day (.getDate date)]
    (str year " " month " " day)))
