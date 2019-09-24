(ns poipoi.datafy-poi
  (:require [clojure.core.protocols :refer [Datafiable]]
            [clojure.datafy :refer [datafy]])
  (:import [de.topobyte.osm4j.core.model.iface EntityContainer EntityType]
           [de.topobyte.osm4j.core.model.impl Node Relation Way Tag]))

(def entity-type->kwd
  {EntityType/Node     :node
   EntityType/Relation :relation
   EntityType/Way      :way})

(defn- unpack-tag
  [^Tag t]
  (let [k (.getKey t)
        v (.getValue t)]
    [k v]))

(defn- datafy-node
  [^Node n]
  {:osm/id  (.getId n)
   :geo/lat (.getLatitude n)
   :geo/lng (.getLongitude n)
   :osm/tags (into {}
                   (map unpack-tag)
                   (.getTags n))})

(defn- datafy-relation
  [^Relation r]
  {:osm/id      (.getId r)
   :osm/tags    (into {}
                      (map unpack-tag)
                      (.getTags r))
   :osm/members (map (fn [m]
                   {:id   (.getId m)
                    :role (.getRole m)
                    :type (entity-type->kwd (.getType m))})
                 (.getMembers r))})

(defn- datafy-way
  [^Way w]
  {:osm/id    (.getId w)
   :osm/nodes (seq (.toArray (.getNodes w)))
   :osm/tags  (into {}
                (map unpack-tag)
                (.getTags w))})

(defn- datafy-entity-container
  [^EntityContainer this]
  (let [entity-pojo (.getEntity this)
        entity-data (datafy entity-pojo)]
    (-> {:osm/type (entity-type->kwd (.getType this))}
        (merge entity-data)
        (with-meta {:osm/entity entity-pojo}))))

(extend-protocol Datafiable
  EntityContainer
  (datafy [this] (datafy-entity-container this))

  Node
  (datafy [this] (datafy-node this))

  Relation
  (datafy [this] (datafy-relation this))

  Way
  (datafy [this] (datafy-way this)))
