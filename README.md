# poipoi

A small Clojure library for parsing raw osm.pbf (protocol buffer binary format) files into Clojure data structures

## Usage

```clojure
   [poipoi "0.1.0"]

;; In your require statement:
   [poipoi.core :as poi]

;; Datafy osm pbf
   (datafy-osm-pbf-r <path-to-osm-pbf-data> [] xform))
   (datafy-osm-pbf   <path-to-osm-pbf-data>)

  ;; if you would like to manage your own input-stream
   (datafy-osm-pbf-stream input-stream)
   (datafy-osm-pbf-r-stream input-stream) ;; returns a reducible collection

;; Where `input-stream` is an instance of `java.io.InputStream` representing the osm.pbf file. 
;; Note that the evaluation of the datafy-osm-pbf function is lazy.
```
## Osm pbf files
The raw pbf files this library is intended parse are located at
`download.geofabrik.de` i.e. https://download.geofabrik.de/north-america/us/rhode-island-latest.osm.pbf

## License
Release under the MIT license. See LICENSE for the full license.

Copyright © 2019 Robert Pierce

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
