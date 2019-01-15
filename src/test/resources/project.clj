(defproject clojure-sonar-example "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [org.tukaani/xz "1.7"]]
            :plugins [[lein-ancient "0.6.15"]
                      [jonase/eastwood "0.3.3"]
                      [lein-cloverage "1.0.13"]
                      [lein-kibit "0.1.6"]
                      [lein-nvd "0.6.0"]])
