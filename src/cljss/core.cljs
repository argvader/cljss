(ns cljss.core
  (:require [cljss.sheet :refer [create-sheet insert!]]
            [cljss.utils :refer [build-css]]))

(defonce ^:private sheet (create-sheet))

(defn- css [static cls vars]
  (when-not (empty? static)
    (insert! sheet static))
  (if (seq vars)
    (let [var-cls (str "vars-" (hash vars))]
      (insert! sheet (build-css var-cls vars))
      (str "css-" cls " " var-cls))
    (str "css-" cls)))

(defn styled [tag cls static vars attrs]
  (fn [props & children]
    (let [[props children] (if (map? props) [props children] [{} (into [props] children)])
          varClass (->> vars (map (fn [[cls v]] (if (ifn? v) [cls (v props)] [cls v]))) (css static cls))
          className (get props :className)
          className (str (when className (str className " ")) varClass)
          props (assoc props :className className)
          props (apply dissoc props attrs)]
      (apply vector tag props children))))
