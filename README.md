# poipoi

A small Clojure library for parsing raw osm.pbf (protocolbuffer binary format) files into Clojure data structures

## Usage

```clojure
   [poipoi "0.1.0"]

;; In your require statement:
   [poipoi.core :refer [datafy-osm-pbf]]

;; Datafy osm pbf
   (datafy-osm-pbf input-stream)

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
