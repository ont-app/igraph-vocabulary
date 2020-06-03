(ns ont-app.igraph-vocabulary.ont
  (:require
   [clojure.string :as str]
   ;;
   ;;
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph.core :as igraph
    :refer [add
            ]]
   [ont-app.igraph.graph :as g
    :refer [make-graph
            ]]

   ))

(voc/put-ns-meta!
 'ont-app.igraph.core
 {
  :vann/preferredNamespacePrefix "igraph"
  :vann/preferredNamespaceUri "http://rdf.naturallexicon.org/ont-app/igraph/ont#"
  }
 )

 
(def ontology-ref (atom (make-graph)))


(defn update-ontology! [to-add]
  (swap! ontology-ref add to-add))

(update-ontology!
 [
  [:igraph/CompiledObject
   :rdfs/comment "Refers to a graph element compiled in the native execution 
environment, typically a function, a graph or a vector"
   ]
  [:igraph/Function
   :rdfs/subClassOf :igraph/CompiledObject
   :rdfs/comment "Refers to a description of a graph element compiled as a 
function in the execution environment."
   ]
  [:igraph/Vector
   :rdfs/subClassOf :igraph/CompiledObject
   :rdfs/comment "Refers to a description of a graph element compiled in the 
execution environment as a vector or array, esp. a clojure vector."
   ]
  [:igraph/Graph
   :rdfs/subClassOf :igraph/CompiledObject
   :rdfs/comment "Refers to a description of a compiled object aligned with the 
IGraph protocol"
   ]
  [:igraph/projectedRange
   :rdf/type :rdf/Property
   :rdfs/domain :rdf/Property
   :rdfs/range :igraph/CompiledObject
   :rdfs/comment "Asserts a URI that Describes the range of objects which are 
compiled and therefore not URIs or Literals. Such objects may be described by 
subclasses of :igraph/CompiledObject, but this description exists separately 
from their actual implementation."
   ]
  [:igraph/compiledAs
   :rdf/type :rdf/Property
   :igraph/projectedRange :igraph/CompiledObject
   :proto/aggregation :proto/Exclusive ;; not part of igraph-vocabulary
   :rdfs/comment "<resource> compiledAs <compiled object>
Asserts that the construct named in the ontology as <resource> is implemented within the current execution environment as <compiled object>."
   ]
  [:igraph/subsumedBy
   :rdf/type :rdf/Property
   :rdfs/comment "Asserts <sub>/<super> in a taxonomy. Can be used to imply a 
derive/underive declaration in clojure."
   ]
  [:rdf/type :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subClassOf :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subPropertyOf :rdfs/subPropertyOf :igraph/subsumedBy]
  ]
 )

(def ontology @ontology-ref)
