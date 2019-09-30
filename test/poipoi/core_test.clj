(ns poipoi.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer [input-stream]]
            [resources.mock-osm-data :as mock-osm]
            [poipoi.core :as sut]))

(deftest datafy-osm-pbf-streams-test
  (let [test-output   (with-open [is (input-stream mock-osm/fixture-output)]
                        (doall (sut/datafy-osm-pbf-stream is)))
        test-output-r (with-open [is (input-stream mock-osm/fixture-output)]
                               (into [] (sut/datafy-osm-pbf-r-stream is )))
        test-node       (first test-output)
        test-node-r     (first test-output-r)
        test-way        (second test-output)
        test-way-r      (second test-output-r)
        test-relation   (nth test-output 2)
        test-relation-r (nth test-output-r 2)
        {mock-node :node mock-way :way mock-relation :relation} mock-osm/mock-osm-datafied-entities]
    (testing "datafy-osm-pbf-stream properly parses a OsmNode object from pbf stream"
      (is (= test-node mock-node)))
    (testing "datafy-osm-pbf-stream properly parses a OsmWay object from pbf stream"
      (is (= test-way mock-way)))
    (testing "datafy-osm-pbf-stream properly parses a OsmRelation object from pbf stream"
      (is (= test-relation mock-relation)))
    (testing "datafy-osm-pbf-r-stream properly parses a OsmNode object from pbf stream"
      (is (= test-node-r mock-node)))
    (testing "datafy-osm-pbf-r-stream properly parses a OsmWay object from pbf stream"
      (is (= test-way-r mock-way)))
    (testing "datafy-osm-pbf-r-stream properly parses a OsmRelation object from pbf stream"
      (is (= test-relation-r mock-relation)))))

(deftest datafy-osm-pbf-test
  (let [node-filter-xf (filter (comp (partial = :node) :osm/type))
        test-output    (sut/datafy-osm-pbf mock-osm/fixture-output)
        test-output-r  (sut/datafy-osm-pbf-r mock-osm/fixture-output [] node-filter-xf)
        test-node      (first test-output)
        test-node-r    (first test-output-r)
        test-way       (second test-output)
        test-relation  (nth test-output 2)
        {mock-node :node mock-way :way mock-relation :relation} mock-osm/mock-osm-datafied-entities]
    (testing "datafy-osm-pbf properly parses a OsmNode object from pbf stream"
      (is (= test-node mock-node)))
    (testing "datafy-osm-pbf properly parses a OsmWay object from pbf stream"
      (is (= test-way mock-way)))
    (testing "datafy-osm-pbf properly parses a OsmRelation object from pbf stream"
      (is (= test-relation mock-relation)))
    (testing "datafy-osm-pbf-r properly parses a OsmNode object from pbf stream"
      (is (= test-node-r mock-node)))
    (testing "datafy-osm-pbf-r properly applies a transform"
      (is (every? (comp (partial = :node) :osm/type) test-output-r)))))
