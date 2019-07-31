(ns ont-app.igraph-vocabulary.core
  (:require
   [ont-app.igraph.core :as igraph :refer [traverse add]]
   [ont-app.igraph.graph :as g]
   [ont-app.vocabulary.core :as voc]
   )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FUN WITH READER MACROS
;;;;;;;;;;;;;;;;;;;;;;;;;;
#?(:cljs
   (enable-console-print!)
   ;; (defn on-js-reload [])
   )

;; NO READER MACROS BEYOND THIS POINT
^:reduce-s-p-o-fn
(defn resolve-namespace-prefixes [g s p o]
  "Returns <g'> with [<s'> <p'> <o'>] added
Where
Each of <s'> <p'> <o'> may have had its long URI abbreviated for currently
declared namespaces prefixes.
Note: This is typically used when some edn source was generated in an environment
  which only included the standard namespaces, but non-default namespaces
  were used in the origional ttl source.
"
  (letfn [(resolve-ns-prefix
            [maybe-uri]
            (if (keyword? maybe-uri)
              (voc/keyword-for (subs (str maybe-uri) 1))
              ;; else not a kw
              maybe-uri))
           ]
    (add g
            ^{:triples-format :vector}
            (vec (map resolve-ns-prefix [s p o])))))

;;;;;;;;;;;;;;;;
;; RDF-CENTRIC
;;;;;;;;;;;;;;;;

^traversal-fn
(defn rdfs-subsumed-by
  "Returns [context acc' #{}] for `g` `context` `acc` `queue`
  Where
  <g> implements igraph
  <context> is a traversal context, which will be returned unchanged.
    If there is a :seek function supplied, the traversal will stop early.
  <acc'> := #{<class> ...}
  <class> is a superclass of the type associated with some <target>
  <queue> := [<target>, ...]
  <target> is a subject in <g>, and member of <queue>
  SEE ALSO the docs for igraph traversal
  NOTE equivalent of SPARQL property path 'a/rdfs:subClassOf*'
  "
  ;; TODO: this should probably be moved elsewhere no longer used in this
  ;; module, but should be useful somewhere
  [g context acc queue]
  [context
   (->> queue
        (traverse g (igraph/traverse-link :rdf/type) context #{})
        (traverse g (igraph/transitive-closure :rdfs/subClassOf) context #{})
        )
   #{}])

;; ^traversal-fn
(defn has-owl-restriction
  "
  <context> may contain a :seek parameter, so we apply it to last traversal.
    NOTE equivalent of SPARQL property path 'a/rdfs:subClassOf*/owl:subClassOf+'
  "
  ;; TODO: this should probably be moved elsewhere no longer used in this
  ;; module, but should be useful somewhere
  [g context acc queue]
  [context
   (->> queue
        (traverse g rdfs-subsumed-by #{})
        (traverse g (igraph/traverse-link :owl/subClassOf) #{})
        (traverse g (igraph/transitive-closure :owl/subClassOf) context #{}))
   #{}])
