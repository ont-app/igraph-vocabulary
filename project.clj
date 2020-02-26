(defproject ont-app/igraph-vocabulary "0.1.0"
  :description "Utilities for using ont-app/vocabulary in ont-app/igraph"
  :url "https://github.com/ont-app/vocabulary"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.597"]
                 [ont-app/igraph "0.1.4"]
                 [ont-app/vocabulary "0.1.0"]
                 [lein-doo "0.1.11"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-codox "0.10.6"]
            [lein-doo "0.1.11"]
            ]

  :source-paths ["src"]
  :test-paths ["src" "test"]
  :cljsbuild
  {:test-commands {"test" ["lein" "doo" "node" "test" "once"]}
   :builds
   {
    :test
    {:source-paths ["src" "test"]
     :compiler {
                :main ont-app.igraph-vocabulary.doo
                :target :nodejs
                :asset-path "resources/test/js/compiled/out"
                :output-to "resources/test/compiled.js"
                :output-dir "resources/test/js/compiled/out"
                :optimizations :advanced ;;:none
                :pretty-print false
                }
     }
    } ;; value of :builds
   } ;; vallue of :cljsbuild

  :codox {:output-path "doc"}

  :profiles {:dev {;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   }}
                   
                   
  ;; need to add the compliled assets to the :clean-targets
  :clean-targets
  ^{:protect false}
  ["resources/public/js/compiled"
   "resources/test"
   :target-path
   ]
  ) ;; defproject
