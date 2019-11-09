(ns ont-app.igraph-vocabulary.core-test
  {:vann/preferredNamespacePrefix "test"
   :vann/preferredNamespaceUri "http://example.com/"
   }
  (:require [clojure.test :refer :all]
            [ont-app.igraph.core :refer [normal-form add reduce-s-p-o]]
            [ont-app.igraph.graph :as g]
            [ont-app.vocabulary.core :as voc]
            [ont-app.igraph-vocabulary.core :as igv]
            ))

(deftest mint-kwi-test
  (is (= (igv/mint-kwi ::Blah ::blah "blah")
         ::Blah_blah_blah)))

(deftest resolve-namespace-prefixes-test
  (testing "namespace prefixes should be resolved"
    (is (= (normal-form
            (reduce-s-p-o
             igv/resolve-namespace-prefixes
             (g/make-graph)
             (add (g/make-graph)
                  [[(keyword
                     "http://www.w3.org/1999/02/22-rdf-syntax-ns#blah")
                    :p
                    :o]])))
           {:rdf/blah {:p #{:o}}}))))

                             
