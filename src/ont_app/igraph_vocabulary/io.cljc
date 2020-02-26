(ns ont-app.igraph-vocabulary.io
  (:require
   [ont-app.igraph.core :as igraph :refer [add reduce-spo]]
   [ont-app.igraph.graph :as g]
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph-vocabulary.core :as igv]
   #?(:clj [clojure.java.io :as io])

   )
  )

#?(:clj
   (defn read-graph-from-source [edn-source]
     (as-> (g/make-graph) g
       (add g (-> edn-source
                  io/resource
                  slurp
                  read-string))
       (reduce-spo
        igv/resolve-namespace-prefixes
        (g/make-graph)
        g)
       ))
   :cljs
   (defn read-graph-from-source [edn-source]
     (throw

      (js/Error. "read-graph-from-source relies on i/o facilities not available in cljs"))))
