(defproject davewm/snake "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [play-cljs "0.10.2"]]
  :plugins [[lein-figwheel "0.5.16"]]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src/"]
                        :figwheel     true
                        :compiler     {:main       "snake.core"
                                       :asset-path "js/out"
                                       :output-to  "resources/public/js/main.js"
                                       :output-dir "resources/public/js/out"}}]}
  :figwheel {:nrepl-port 9002}

  :profiles {:dev {:dependencies [[cider/piggieback "0.3.8"]
                                  [org.clojure/tools.nrepl "0.2.13"]]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}})