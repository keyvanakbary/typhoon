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
              :compiler {
                :preamble ["reagent/react.js"]
                :output-to "typhoon.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
