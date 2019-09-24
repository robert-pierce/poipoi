(ns poipoi.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer [input-stream]]
            [resources.mock-osm-data :as mock-osm]
            [poipoi.core :as sut]))

(deftest datafy-osm-pbf-test
  (let [test-output (with-open [is (input-stream mock-osm/fixture-output)]
                     (doall (sut/datafy-osm-pbf is)))
        test-node     (first test-output)
        test-way      (second test-output)
        test-relation (nth test-output 2)
        {mock-node :node mock-way :way mock-relation :relation} mock-osm/mock-osm-datafied-entities]
    (testing "datafy-osm-pbf properly parses a OsmNode object from pbf stream"
      (is (= test-node mock-node)))
    (testing "datafy-osm-pbf properly parses a OsmWay object from pbf stream"
      (is (= test-way mock-way)))
    (testing "datafy-osm-pbf properly parses a OsmRelation object from pbf stream"
      (is (= test-relation mock-relation)))))
