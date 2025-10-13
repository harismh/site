(ns build-content
  (:require [mapdown.core :as mapdown]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [markdown.core :as md]
            [net.cgrand.enlive-html :as html]))

(defn slugify [filename]
  (-> filename
      (str/replace #"\.md$" "")
      (str/lower-case)))

(defn node->hiccup [node]
  (cond
    (string? node) node
    (:tag node) (let [tag (keyword (:tag node))
                      attrs (:attrs node)
                      content (mapv node->hiccup (:content node))]
                  (if (seq attrs)
                    (into [tag attrs] content)
                    (into [tag] content)))
    :else ""))

(defn html->hiccup [html-str]
  (mapv node->hiccup (html/html-snippet html-str)))

(defn markdown->hiccup [md-text]
  (html->hiccup (md/md-to-html-string md-text)))

(defn process-markdown-file [file]
  (let [content (slurp file)
        parsed (mapdown/parse content)
        filename (.getName file)
        slug (or (:slug parsed) (slugify filename))
        body-hiccup (markdown->hiccup (:body parsed))
        output-data (assoc parsed :body body-hiccup)
        output-file (io/file "public/resources/content" (str slug ".edn"))]
    (println "\t" filename "→" (.getName output-file))
    (io/make-parents output-file)
    (spit output-file (pr-str output-data))
    {:slug slug
     :title (:title parsed)
     :date (:date parsed)
     :description (:description parsed)}))

(defn -main []
  (println "Building <project-root>/content/*.md...")
  (let [content-dir (io/file "content")
        md-files (filter #(.endsWith (.getName %) ".md")
                         (file-seq content-dir))
        index (mapv process-markdown-file md-files)
        sorted-index (vec (sort-by :date #(compare %2 %1) index))
        index-file (io/file "public/resources/content/index.edn")]
    (spit index-file (pr-str sorted-index))
    (println "Done! Built" (count index) "file(s).")))
