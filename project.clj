(defproject typhoon "0.1.0-SNAPSHOT"
  :description ""
  :url "https://github.com/keyvanakbary/typhoon"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2234"]
                 [reagent "0.4.2"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {:optimizations :none
                                   :pretty-print true
                                   :output-to "build/dev.js"
                                   :output-dir "build/dev"
                                   :preamble ["reagent/react.js"]
                                   :source-map true}}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:optimizations :advanced
                                   :warning-level :verbose
                                   :pretty-print false
                                   :output-to "build/prod.js"
                                   :output-dir "build/prod"
                                   :preamble ["reagent/react.min.js"]
                                   :externs ["react/externs/react.js"]
                                   :closure-warnings {:externs-validation :off
                                                      :non-standard-jsdoc :off}}}]})
