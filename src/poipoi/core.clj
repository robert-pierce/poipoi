(ns poipoi.core
  (:require [clojure.core.protocols :refer [Datafiable]]
            [clojure.datafy :refer [datafy]]
            [clojure.java.io :refer [input-stream]]
            [poipoi.datafy-poi])
  (:import [java.io InputStream]
           [de.topobyte.osm4j.core.model.iface EntityContainer]
           [de.topobyte.osm4j.pbf.seq PbfIterator]))

;; todo
;; - fix exception handling
;; - is this idiomatic/cannonical
;; - better handle options???
;; - make into  collection a param
;; - create an api that accepts a stream and returns a reducible collection
;;      let the user manage the stream

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
                ret                     (rf acc datafied-entity)]
            (if (reduced? ret)
              @ret
              (recur ret)))
          acc)))))

(defn datafy-osm-pbf*
  [^InputStream is]
  (try
    (-> is
        pbf-iterator
        datafy-osm-iterator)
    (catch Exception e (println "An error occured datafying the osm-pbf stream: " e))))

(defn datafy-osm-pbf-r*
  [^InputStream is]
  (try
    (-> is
        pbf-iterator
        osm-iterator->datafied-reducible)
    (catch Exception e (println "An error occured datafying the osm-pbf stream: " e))))

;;----------------------------------------
(defn datafy-osm-pbf
  ([path] (datafy-osm-pbf path {}))
  ([path opts]
   (try
     (with-open [is (input-stream path)]
       (let [{:keys [reducible]} opts]
         (if reducible
           (into [] (datafy-osm-pbf-r* is))
           (doall (datafy-osm-pbf* is)))))
     (catch Exception e (println "An error occured datafyin the osm-pbf stream:" e)))))

;;--------------------------------------------
(comment "sequential usage -- DEPRECATED"
  (with-open [is
              (clojure.java.io/input-stream
               "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
                (into #{} identity (datafy-osm-pbf is))))

(comment "reducible usage -- DEPRECATED"
  (with-open [is
              (clojure.java.io/input-stream
               "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
                (into #{} identity (datafy-osm-pbf-reducible is))))
