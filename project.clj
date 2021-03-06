(defproject davewm/snake "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [play-cljs "0.10.2"]]
  :plugins [[lein-figwheel "0.5.16"]
            [lein-cljsbuild "1.1.7"]]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/"]
                        :figwheel     true
                        :compiler     {:main       "snake.core"
                                       :asset-path "js/out"
                                       :output-to  "resources/public/js/main.js"
                                       :output-dir "resources/public/js/out"}}
                       {:id           "prod"
                        :source-paths ["src/"]
                        :compiler     {:main       "snake.core"
                                       :asset-path "js/out"
                                       :output-to  "resources/public/js/main.js"
                                       :optimizations :advanced
                                       :infer-externs true}}]}
  :figwheel {:nrepl-port 9002}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.4-6"]]}})