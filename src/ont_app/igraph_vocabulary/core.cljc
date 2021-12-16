(ns ont-app.igraph-vocabulary.core
  (:require
   [clojure.string :as str]
   ;; local libraries
   [ont-app.igraph.core :as igraph :refer [traverse add]]
   [ont-app.igraph.graph :as g]
   [ont-app.vocabulary.core :as voc]
   [ont-app.vocabulary.format :as fmt]
   [ont-app.igraph-vocabulary.ont :as ont]
   )
  )

(voc/put-ns-meta!
 'ont-app.igraph-vocabulary.core
 {
  :voc/mapsTo 'ont-app.igraph-vocabulary.ont
  }
 )

(def ontology "Supporting ontology for IGraph stuff"
  ont/ontology)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FUN WITH READER MACROS
;;;;;;;;;;;;;;;;;;;;;;;;;;
#?(:cljs
   (enable-console-print!)
   ;; (defn on-js-reload [])
   )

;; NO READER MACROS BEYOND THIS POINT


;; MINTING NEW KEYWORD IDENTIFIERS
(defn mint-kwi-dispatch
  "Returns `head-kwi` as `dispatch-key` for the `mint-kwi` method. 
  Where:
  `head-kwi` is the first argument
  `dispatch-key` is a keyword"
  [head-kwi & _args]
  head-kwi)

(defmulti mint-kwi
  "Args: [`head-kwi` & `args`]. Returns a canonical kwi.
  Where
  - `head-kwi` initiates the KWI (typically the name of an existing class in some
    model).
  - `args` := [`property` `value`, ...], .s.t. the named value is uniquely distinguished.
  E.g: The default method simply joins arguments on _ as follows:
    (mint-kwi :myNs/MyClass :myNs/prop1 'foo' :myNs/prop2 'bar)
    -> :myNs/MyClass_prop1_foo_prop2_bar, but overriding methods will be
    dispatched on `head`
  Compiled arguments are rendered as their hashes.
  "
  mint-kwi-dispatch
  )

(defn ^:private igraph? [x]
  ;; TODO: quick temporary fix
  (= (type x) (type (g/make-graph))))

(defmethod mint-kwi :default
  [head-kwi & args]
  ;; <head-kwi> + hash of sorted args.
  (assert (not (some #(nil? %) args)))
  (let [_ns (namespace head-kwi)
        _name (name head-kwi)
        stringify (fn [x]
                    (cond (string? x) x
                          (keyword? x) (name x)
                          (sequential? x) (hash x)
                          (igraph? x) (hash x)
                          :else (str x))) 
        kwi (keyword _ns (str _name "_" (str/join "_" (map stringify args))))
        ]
    kwi))


;; READING EDN TRANSLATIONS OF RDF SOURCE
;; reduce-spo function 
(defn resolve-namespace-prefixes 
  "Returns `g'` with [`s'` `p'` `o'`] added
Where
Each of `s'` `p'` `o'` may have had its long URI abbreviated for currently
declared namespaces prefixes.
Note: This is typically used when some edn source was generated in an environment
  which only included the standard namespaces, but non-default namespaces
  were used in the origional ttl source.
"
  [g s p o]
  (letfn [(resolve-ns-prefix
            [maybe-uri]
            (if (keyword? maybe-uri)
              (let [_ns (namespace maybe-uri)
                    name (name maybe-uri)]
                (if _ns
                  ;; this is already assigned a namespace
                  (if (re-matches #"http(s)?" _ns)
                    (throw (ex-info "maybe-uri is a URL. use (voc/keyword-for ...)"
                                    {:type ::BadURIFormat
                                     :uri maybe-uri
                                     }))
                    maybe-uri)
                  ;; else no namespace assume encoded http uri...
                  (voc/keyword-for
                   (fmt/decode-kw-name name))))
              ;; else not a kw
              maybe-uri))
           ]
    (add g
            ^{:triples-format :vector}
            (vec (map resolve-ns-prefix [s p o])))))

;;;;;;;;;;;;;;;;
;; RDF-CENTRIC
;;;;;;;;;;;;;;;;
;; TODO: move to ont-app/rdf library

;; traversal function
(defn rdfs-subsumed-by
  "Returns [context acc' #{}] for `g` `context` `acc` `queue`
  Where
  - `g` implements igraph
  - `context` is a traversal context, which will be returned unchanged.
    If there is a :seek function supplied, the traversal will stop early.
  - `acc'` := #{`class` ...}
  - `class` is a superclass of the type associated with some `target`
  - `queue` := [`target`, ...]
  - `target` is a subject in `g`, and member of `queue`
  SEE ALSO the docs for igraph traversal
  NOTE equivalent of SPARQL property path 'a/rdfs:subClassOf*'
  "
  ;; TODO: this should probably be moved elsewhere no longer used in this
  ;; module, but should be useful somewhere
  [g context _acc queue]
  [context
   (->> queue
        (traverse g (igraph/traverse-link :rdf/type) context #{})
        (traverse g (igraph/transitive-closure :rdfs/subClassOf) context #{})
        )
   #{}])

