# igraph-vocabulary

Support for creating keyword identifiers (KWIs) in IGraph-compliant graphs and aligning them with URIs in the Linked Data world. 

## Overview

A KWI is a namespaced clojure keyword used exactly the way a URI is
used in RDF. Ideally URI should be importable as KWIs and integrated
into IGraph-based models, and conversely KWIs should be exportable as
standard URIs.

This builds on the ont-app.vocabulary library and the ont-app.igraph
library, and is responsible for basic functionality involving
both. ont-app.igraph and ont-app.vocabulary should not know about each
other.

Watch this space for more documentation.

## License

Copyright © 2019 Eric D. Scott

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
