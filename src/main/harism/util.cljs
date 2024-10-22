(ns harism.util)

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
