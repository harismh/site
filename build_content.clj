(ns build-content
  (:require [mapdown.core :as mapdown]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
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

(defn external-link? [href]
  (boolean (and href (re-matches #"(?i)(?:https?:)?//.*" href))))

(defn apply-rules-to-node [node rules]
  (if (and (vector? node) (keyword? (first node)))
    (let [tag (first node)
          tail (rest node)
          attrs? (map? (first tail))
          attrs (when attrs? (first tail))
          children (if attrs?
                     (rest tail)
                     tail)
          updated-attrs
          (reduce (fn [acc rule]
                    (rule tag acc)) attrs rules)
          base (if updated-attrs
                 [tag updated-attrs]
                 [tag])]
      (into base children))
    node))

(defn apply-rules-to-tree [tree rules]
  (mapv #(walk/postwalk
          (fn [node] (apply-rules-to-node node rules)) %)
        tree))

(defn anchor-target-rule [tag attrs]
  (if (and (= :a tag) (external-link? (:href attrs)))
    (assoc (or attrs {}) :target "_blank")
    attrs))

(defn process-markdown-file [file]
  (let [content (slurp file)
        parsed (mapdown/parse content)
        filename (.getName file)
        slug (or (:slug parsed) (slugify filename))
        body-hiccup (apply-rules-to-tree
                     (markdown->hiccup (:body parsed))
                     [anchor-target-rule])
        output-data (assoc parsed :body body-hiccup)
        output-file (io/file "public/resources/content" (str slug ".edn"))]
    (println "\t" filename "→" (.getName output-file))
    (io/make-parents output-file)
    (spit output-file (pr-str output-data))
    {:slug slug
     :title (:title parsed)
     :date (:date parsed)
     :url (:url parsed)
     :kind (or (keyword (:kind parsed)) :writing)
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
