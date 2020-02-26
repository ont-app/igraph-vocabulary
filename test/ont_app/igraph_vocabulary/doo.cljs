(ns ont-app.igraph-vocabulary.doo
  (:require [doo.runner :refer-macros [doo-tests]]
            [ont-app.igraph-vocabulary.core-test]
            ))

(doo-tests
 'ont-app.igraph-vocabulary.core-test
 )
