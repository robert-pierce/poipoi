(defproject poipoi "0.1.0"
  :description "A small Clojure library for parsing raw osm.pbf (protocolbuffer binary format) files into Clojure data structures."
  :url "https://github.com/robert-pierce/poipoi"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [de.topobyte/osm4j-pbf "1.2.0"]
                 [com.slimjars.trove4j/trove4j-long-array-list "1.0.1"]]
  :repositories [["slimjars" "https://mvn.slimjars.com"]
                 ["topobyte" "https://mvn.topobyte.de"]])
