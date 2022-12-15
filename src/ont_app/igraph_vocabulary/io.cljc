(ns ont-app.igraph-vocabulary.io
  {;; Ms Kondo is just wrong here...
   :clj-kondo/config '{:linters {:unused-namespace {:level :off}
                                 :unused-referred-var {:level :off}
                                 :unused-binding {:level :off}
                                 }}
   }
  (:require
   [ont-app.igraph.core :as igraph :refer [add reduce-spo]]
   [ont-app.igraph.graph :as native-normal]
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph-vocabulary.core :as igv]
   #?(:clj [clojure.java.io :as io])

   )
  )

#?(:clj
   (defn read-graph-from-source
     "Returns a native-normal graph from `edn-source`
  Where
  - `edn-source` is a file in EDN format"
     [edn-source]
     (as-> (native-normal/make-graph) g
       (add g (-> edn-source
                  io/resource
                  slurp
                  read-string))
       (reduce-spo
        igv/resolve-namespace-prefixes
        (native-normal/make-graph)
        g)
       ))
   :cljs
   (defn read-graph-from-source [edn-source]
     (throw

      (js/Error. "read-graph-from-source relies on i/o facilities not available in cljs"))))
