(ns poipoi.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer [input-stream]]
            [poipoi.core :refer :all])
  (:import [de.topobyte.osm4j.core.model.iface EntityContainer EntityType]
           [de.topobyte.osm4j.core.model.impl Node Relation RelationMember Way Tag]
           [de.topobyte.osm4j.pbf.seq PbfIterator]
           [com.slimjars.dist.gnu.trove.list.array TLongArrayList]))

(def mock-lat  24.24)
(def mock-lon  -42.42)
(def mock-tags
  (java.util.ArrayList. 
   [(Tag. "this-is-a-tag-key-1" "this-is-a-tag-value-1")
    (Tag. "this-is-a-tag-key-2" "this-is-a-tag-value-2")]))

(def mock-node 
  (EntityContainer. 
   EntityType/Node
   (Node. 42 mock-lon mock-lat mock-tags)))


(def mock-relation-members
 (java.util.ArrayList.
  [(RelationMember. 10 EntityType/Way "west")
   (RelationMember. 20 EntityType/Way "east")]))

(def mock-relation 
  (EntityContainer. 
   EntityType/Relation
   (Relation. 420 mock-relation-members mock-tags)))

(def mock-way
  (EntityContainer.
   EntityType/Way
   (Way. 4200 (TLongArrayList. (long-array [10 20 30 40])) mock-tags)))

(def mock-payload ;; THIS IS FAILING, can't parse mock array as byte-array  
  (input-stream 
   (byte-array [mock-node mock-relation mock-way])))

(deftest datafy-osm-pbf-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
