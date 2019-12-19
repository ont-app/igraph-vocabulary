(defproject ont-app/igraph-vocabulary "0.1.0-SNAPSHOT"
  :description "Utilities for using ont-app/vocabulary in ont-app/igraph"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async  "0.4.500"]
                 [ont-app/igraph "0.1.4-SNAPSHOT"]
                 [ont-app/vocabulary "0.1.0-SNAPSHOT"]
                 [lein-doo "0.1.11"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-doo "0.1.11"]
            ]

  :source-paths ["src"]
  :test-paths ["src" "test"]
  :cljsbuild
  {:test-commands {"test" ["lein" "doo" "node" "test" "once"]}
   :builds
   {:dev
    {
     :source-paths ["src"]
     :compiler {:main ont-app.igraph-vocabulary.core
                :asset-path "js/compiled/out"
                :output-to
                "resources/public/js/compiled/igraph_vocabulary.js"
                :output-dir "resources/public/js/compiled/out"
                :source-map-timestamp true
                ;; To console.log CLJS data-structures make sure you
                ;; enable devtools in Chrome
                ;; https://github.com/binaryage/cljs-devtools
                :preloads [devtools.preload]
                :optimizations :none
                }}
    :test
    {:source-paths ["src" "test"]
     :compiler {
                :main ont-app.igraph-vocabulary.doo
                :target :nodejs
                :asset-path "resources/test/js/compiled/out"
                :output-to "resources/test/compiled.js"
                :output-dir "resources/test/js/compiled/out"
                :optimizations :none ;; :advanced ;;:none
                }
     }
    ;; This next build is a compressed minified build for
    ;; production. You can build this with:
    ;; lein cljsbuild once min
    :min
    {
     :source-paths ["src"]
     :compiler {:output-to
                "resources/public/js/compiled/igraph_vocabulary.js"
                :main ont-app.igraph-vocabulary.core
                :optimizations :advanced
                :pretty-print false}
     }
    } ;; value of :builds
   } ;; vallue of :cljsbuild

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  ]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                      :target-path]}}
  
  ) ;; defproject
