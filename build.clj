(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b] ; for b/git-count-revs
            [org.corfield.build :as bb]))

(def lib 'ont-app/igraph-vocabulary)

(def version "0.2.0")


(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/run-tests)
      (bb/clean)
      (bb/jar)))

(defn clean "Cleans any clj/s compilation output.
  Where:
  `opts` := m s.t. (keys m) #~ #{:include-caches?, ...}
  `include-caches?` when `true` indicates to clear .cpcache and .shadow-cljs directories.
  "
  [opts]
  (println (str "Cleaning with opts:" opts "."))
  ;; TODO: check opts
  (bb/clean opts)
  (b/delete {:path "./out"})  
  (b/delete {:path "./cljs-test-runner-out"})
  (when (= (:include-caches? opts) true)
    (println (str "Clearing caches"))
    (b/delete {:path "./.cpcache"})  
    (b/delete {:path "./.shadow-cljs"}))
  opts)

(defn install "Install the JAR locally." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/install)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))

