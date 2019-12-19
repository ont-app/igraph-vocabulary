(ns ont-app.igraph-vocabulary.io
  (:require
   [clojure.java.io :as io]
   [ont-app.igraph.core :as igraph :refer :all]
   [ont-app.igraph.graph :as g]
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph-vocabulary.core :as igv] 
   )
  )

(defn read-graph-from-source [edn-source]
  (as-> (g/make-graph) g
    (add g (-> edn-source
               io/resource
               slurp
               read-string))
    (reduce-s-p-o
     igv/resolve-namespace-prefixes
     (g/make-graph)
     g)
    ))
