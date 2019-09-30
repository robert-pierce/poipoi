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

(defn- osm-iterator->datafied-reducible
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

(defn- datafy-osm-pbf*
  [^InputStream is]
  (-> is
      pbf-iterator
      datafy-osm-iterator))

(defn- datafy-osm-pbf-r*
  [^InputStream is]
  (-> is
      pbf-iterator
      osm-iterator->datafied-reducible))

;;----------------------------------------
(defn datafy-osm-pbf-r
  "Accepts a path and tries to open an input-stream on that path. The path should point to
  an osm-pbf resource. The input-stream is consumed and parsed into a reducible collection of
  osm data structures. The reduicible collecion is then transformed into a final collection using
  into and applying the xform supplied via the function params. The user can also supply a final
  collection structure such as a vector, list, or set. A vector is used by default"
  ([path] (datafy-osm-pbf-r path [] nil))
  ([path to] (datafy-osm-pbf-r path to nil))
  ([path to xf]
   (try
    (with-open [is (input-stream path)]
      (if xf
        (into to xf (datafy-osm-pbf-r* is))
        (into to (datafy-osm-pbf-r* is))))
    (catch Exception e (println "An error occured datafyin the osm-pbf stream:" e)))))

(defn datafy-osm-pbf
  "Accepts a path and tries to open an input-stream on that path. The path should point to
  an osm-pbf resource. The input-stream is consumed and parsed into osm data structures."
  [path]
  (try
    (with-open [is (input-stream path)]
      (doall (datafy-osm-pbf* is)))
    (catch Exception e (println "An error occured datafyin the osm-pbf stream:" e))))

(defn datafy-osm-pbf-r-stream
  "Accepts an input-stream opened by the user on an osm.pbf resource. The contained osm data
  structures are then datafied into a reducible collection. It is up to the user to force the
  concrete collection (ie via into) and to close the stream."
  [^InputStream is]
  (try
    (datafy-osm-pbf-r* is)
    (catch Exception e (println "An error occured datafyin the osm-pbf stream:" e))))

(defn datafy-osm-pbf-stream
  "Accepts an input-stream opened by the user on an osm.pbf resource. The contained osm data
  structures are then datafied lazily. It is up to the user to force the concrete collection
  (ie via doall) and to close the stream."
  [^InputStream is]
  (try
    (datafy-osm-pbf* is)
    (catch Exception e (println "An error occured datafyin the osm-pbf stream:" e))))
;;--------------------------------------------
(comment "usuage - datafy-osm-pbf"
  (datafy-osm-pbf "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf"))

(comment "usuage - datafy-osm-pbf-stream"
  (with-open [is (input-stream
                  "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
    (datafy-osm-pbf is)))

(comment "usuage - datafy-osm-pbf-r-stream"
  (with-open [is
              (input-stream
               "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf")]
    (datafy-osm-pbf-r-stream is)))

(comment "usuage - datafy-osm-pbf-r"
         (datafy-osm-pbf-r "https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf"
                           #{}
                           (filter (comp (partial = :node) :osm/type))))
