# igraph-vocabulary

Support for creating keyword identifiers (KWIs) in IGraph-compliant graphs and aligning them with URIs in the Linked Data world. 

## Contents
- [Dependencies](#h2-dependencies)
- [Overview](#h2-overview)
- [The `mint-kwi` method](#h2-the-mint-kwi-method)
- [Supporting ontology](#h2-supporting-ontology)
- [License](#h2-license)

<a name="h2-dependencies"></a>
## Dependencies

Available at [Clojars](https://clojars.org/ont-app/igraph-vocabulary).

[![Clojars Project](https://img.shields.io/clojars/v/ont-app/igraph-vocabulary.svg)](https://clojars.org/ont-app/igraph-vocabulary)

Require thus:

```
(ns ont-app.igraph-vocabulary.core-test
(:require 
  [
   [ont-app.igraph-vocabulary.core :as igv]
   ))
```

<a name="h2-overview"></a>
## Overview

This builds on the ont-app.vocabulary library and the ont-app.igraph
library, and is responsible for basic functionality involving
both. ont-app.igraph and ont-app.vocabulary are independent libraries,
and this library is provided to host basic logic that combines the
two.


A KWI is a namespaced clojure keyword used exactly the way a URI is
used in RDF. Ideally URI should be importable as KWIs and integrated
into IGraph-based models, and conversely KWIs should be exportable as
standard RDF-type URIs.

<a name="h2-the-mint-kwi-method"></a>

## The `mint-kwi` method

When building a model in a graph representation, it's a common
occurrence to need to create canonically named KWIs on the fly,
especially to identify newly encountered instances of some class of
entities.

The _mint-kwi_ method returns a keyword identifier based on a _head_
keyword (which typically names a class) and any number of arguments
(typically a set of predicate/value pairs). The method is dispatched
on the head.

Best practice is to provide as many predicate/value pairs as are
necessary to uniquely distinguish the thing being identified within
whatever universe you're playing in.

Let's assume the following namespace declaration (note the metadata in
the ns):

```
(ns ont-app.igraph-vocabulary.core-test
  {:vann/preferredNamespacePrefix "test"
   :vann/preferredNamespaceUri "http://example.com/"
   }
 (require
 [ont-app.igraph-vocabulary.core :as igv :refer [mint-kwi]]
 [ont-app.vocabulary.core :as voc]
 ))

```

The default method will simply concatenate the names of the keywords provided:

```
> (mint-kwi :test/Example :test/number 1)
:test/Example_number_1
>
``` 

Since we have the proper metadata declared for core-test module,
we can get RDF equivalents:

```
> (voc/qname-for :test/Example_number_1)
"test:Example_number_1"
>
> (voc/iri-for :test/Example_number_1)
"http://example.com/Example_number_1"
> 
```

We can write a custom method dispatched on the head:

```
> (defmethod mint-kwi :test/Example
    [head & {:keys [:test/number]}]
    (keyword (namespace head)
      (str (name head)
         "_"
         number)))
#multifn[mint-kwi 0x535c5c20]
> (mint-kwi :test/Example :test/number 2)
:test/Example_2
> 
```


<a name="h2-supporting-ontology"></a>
## Supporting ontology
<a name="h2-license"></a>
## License

Copyright © 2019-20 Eric D. Scott

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
