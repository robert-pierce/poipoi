(ns poipoi.core
  (:require [clojure.core.protocols :refer [Datafiable]]
            [clojure.datafy :refer [datafy]]
            [poipoi.datafy-poi])
  (:import [java.io InputStream]
           [de.topobyte.osm4j.pbf.seq PbfIterator]))

(defn- pbf-iterator
  [^InputStream is]
  (PbfIterator. is true))

(defn- datafy-osm-iterator
  [^PbfIterator pbfi]
  (map datafy pbfi))

(defn datafy-osm-pbf
  [^InputStream is]
  (try
    (-> is
        pbf-iterator
        datafy-osm-iterator)
    (catch Exception e (println "An error occured datafying the osm-pbf stream: " e))))
