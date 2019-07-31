(ns ont-app.igraph-vocabulary.io
  (:require
   [ont-app.igraph.core :as igraph :refer :all]
   [ont-app.igraph.graph :as g]
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph-vocabulary.core :as igv] ;; 
   )
  )

(defn read-graph-from-source [edn-source]
  (as-> (g/make-graph) g
    (add g (read-string (slurp edn-source)))
    (reduce-s-p-o
     igv/resolve-namespace-prefixes
     (g/make-graph)
     g)
    ))
