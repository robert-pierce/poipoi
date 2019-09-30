(ns resources.mock-osm-data
  (:require [clojure.java.io :refer [input-stream output-stream]])
  (:import [de.topobyte.osm4j.core.model.iface EntityType OsmEntity]
           [de.topobyte.osm4j.core.model.impl Node Relation RelationMember Way Tag]
           [de.topobyte.osm4j.pbf.seq PbfWriter]
           [com.slimjars.dist.gnu.trove.list.array TLongArrayList]))

(def fixture-output "test/resources/mock_osm_data.pbf")
(def mock-lat 24.240000000000002)
(def mock-lon  -42.42)
(def datafied-mock-tags {"this-is-a-tag-key-1" "this-is-a-tag-value-1"
                         "this-is-a-tag-key-2" "this-is-a-tag-value-2"})
(def mock-tags
  (java.util.ArrayList. 
   [(Tag. "this-is-a-tag-key-1" "this-is-a-tag-value-1")
    (Tag. "this-is-a-tag-key-2" "this-is-a-tag-value-2")]))

(def mock-node
  (let [id 42]
    {:datafied {:osm/type :node
                :osm/id id
                :geo/lat mock-lat
                :geo/lng mock-lon
                :osm/tags datafied-mock-tags}
     :java-obj  (Node. id mock-lon mock-lat mock-tags)}))

(def mock-way
  (let [id 4200
        nodes  (TLongArrayList. (long-array [10, 20, 30, 40]))]
    {:datafied {:osm/type :way
                :osm/id id
                :osm/nodes (seq (.toArray nodes))
                :osm/tags datafied-mock-tags}
     :java-obj (Way. id nodes mock-tags)}))

(def mock-relation-members
 (java.util.ArrayList.
  [(RelationMember. 10 EntityType/Way "west")
   (RelationMember. 20 EntityType/Way "east")]))

(def mock-relation
  (let [id 420]
    {:datafied {:osm/type :relation
                :osm/id id
                :osm/tags datafied-mock-tags
                :osm/members (seq [{:id 10 :role "west" :type :way}
                                   {:id 20 :role "east" :type :way}])}
     :java-obj (Relation. id mock-relation-members mock-tags)}))

(def mock-osm-payload
  ;; the payload should be in the order of
  ;; nodes then ways then relations
  (map :java-obj [mock-node mock-way mock-relation]))

(def mock-osm-datafied-entities
  {:node     (:datafied mock-node)
   :way      (:datafied mock-way)
   :relation (:datafied mock-relation)})

(defn serialize-osm-pbf
  "Serializes osm pbf fixture data. Payload should 
   be a collection of OsmEntity objects"
  [payload]
  (with-open [os (output-stream fixture-output)]
    (let [pbfw (PbfWriter. os false)]
      (doall (map (fn [^OsmEntity entity]
                    (.write pbfw entity))
                  payload))
      (.complete pbfw))))
