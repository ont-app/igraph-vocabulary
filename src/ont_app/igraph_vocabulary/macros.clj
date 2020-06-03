(ns ont-app.igraph-vocabulary.macros
  (:require
   [clojure.java.io :as io]
   [ont-app.igraph.core :as igraph :refer [normal-form]]
   [ont-app.igraph-vocabulary.io :as ig-io]
   )
  )
  
(defmacro graph-source 
  "Returns the contents of <edn-source> read in with updated ns metadata.
NOTE: typical usage is to embed contents of an ontology into clj(c/s) code."
  [edn-source]
  (normal-form (ig-io/read-graph-from-source edn-source)))
