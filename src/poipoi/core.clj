(ns poipoi.core
  (:require [clojure.core.protocols :refer [Datafiable]]
            [clojure.datafy :refer [datafy]]
            [poipoi.datafy-poi])
  (:import [java.io InputStream]
           [de.topobyte.osm4j.core.model.iface EntityContainer]
           [de.topobyte.osm4j.pbf.seq PbfIterator]))

(defn pbf-iterator
  [^InputStream is]
  (PbfIterator. is true))

(defn- datafy-osm-iterator
  [^PbfIterator pbfi]
  (map datafy pbfi))

(defn osm-iterator->datafied-reducible
  [^PbfIterator pbf-iter]
  (reify clojure.lang.IReduceInit
    (reduce [_ rf init]
      (loop [acc init]
        (if (.hasNext pbf-iter)
          (let [^EntityContainer entity (.next pbf-iter)
                datafied-entity         (datafy entity)
                ret                     (rf datafied-entity)]
            (if (reduced? ret)
              @ret
              (recur ret)))
          acc)))))


;;----------------------------------------
(defn datafy-osm-pbf
  [^InputStream is]
  (try
    (-> is
        pbf-iterator
        datafy-osm-iterator)
    (catch Exception e (println "An error occured datafying the osm-pbf stream: " e))))

(defn datafy-osm-pbf-reducible
  [^InputStream is]
  (try
    (-> is
        pbf-iterator
        osm-iterator->datafied-reducible)
    (catch Exception e (println "An error occured datafying the osm-pbf stream: " e))))

;;--------------------------------------------
(comment "sequential usage"
  (with-open [is
              (clojure.java.io/input-stream
               "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
                (into #{} identity (datafy-osm-pbf is))))

(comment "reducible usage"
  (with-open [is
              (clojure.java.io/input-stream
               "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
                (into #{} identity (datafy-osm-pbf-reducible is))))
