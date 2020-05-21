# igraph-vocabulary

Support for creating keyword identifiers (KWIs) in IGraph-compliant graphs and aligning them with URIs in the Linked Data world. 

## Contents
- [Dependencies](#h2-dependencies)
- [Overview](#h2-overview)
- [The `mint-kwi` method](#h2-the-mint-kwi-method)
- [The `resolve-namespace-prefixes` function](#resolve-namespace-prefixes-function)
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

`ont-app/igraph` and `ont-app/vocabulary` are independent
libraries. The former defining a generic graph-based primitive
container, and the latter defining a way of treating namespaced
Keyword Identifies (KWIs) as though they were URIs comprising a public
vocabulary, integrated with URIs. `ont-app/igraph-vocabulary` is
responsible for basic functionality involving the intersection or
these two, providing some basic ontological constructs that express
common features of IGraph implementations.

A KWI is a namespaced clojure keyword used exactly the way a URI is
used in RDF. Ideally URI should be importable as KWIs and integrated
into IGraph-based models, and conversely KWIs should be exportable as
standard RDF-type URIs.

<a name="h2-the-mint-kwi-method"></a>

## The `mint-kwi` method

When building a model in a graph representation, it's a common
occurrence to need to create canonically named KWIs on the fly,
especially to identify newly encountered instances of some class of
entities. For example `:employee/123`, mappable to
`http://rdf.mycompany.com/employee#123`.

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
<a name="h2-resolve-namespace-prefixes-function"></a>
## The `resolve-namespace-prefixes` function

It may be the case that you are referencing a graph whose URIs may be
in need of translating into KWIs. In such cases, you can apply this function as a map-spo:

```
> (def g (igraph/reduce-spo resove-namespace-prefixes g))
```

This will reset the value of igraph _g_ with its URIs translated into
their equivalent KWIs, wherever the appropriate namespace metadata has
been supplied.

<a name="h2-supporting-ontology"></a>
## Supporting ontology

The _igraph_ namespace.

This dedicated to naming constructs pertinent to implementations of IGraph.

```
(voc/put-ns-meta!
 'ont-app.igraph.core
 {
  :vann/preferredNamespacePrefix "igraph"
  :vann/preferredNamespaceUri "http://rdf.naturallexicon.org/ont-app/igraph/ont#"
  }
 )
```

| KWI | description |
| --- | --- | --- |
| CompiledObject | A graph element compiled in the native execution environment, typically a function, a graph, or a vector. This is platform-specific. The intent here is to facilitate inter-operation with other platforms, e.g. python, using a shared ontology. |
| Function | A compiled object executable in the native execution environment as a pure function |
| Vector | A compiled container holding an integer-indexed sequence |
| Graph | An compiled implementation of IGraph |
| projectedRange | Asserts that some property will have _y_ in its range, where _y_ is a subclass of _CompiledObject_. As a compiled object, this is neither a class nor a literal. |
| compiledAs | "_kwi_ compiledAs _obj_" asserts that an entity identified across platforms as _kwi_ (or its equivalent URI) is implemented in the current exectuion environment as CompiledObject _obj_ |
| subsumedBy | "_x_ subsumedBy _y_" asserts a subsumption relationship between _x_ and _y_. This may be used in Clojure to derive/underive values to use in method dispatch, for example. |

### default `subsumedBy` declarations

The following subsumedBy declarations are defined in _ont.cljc_:

```
  [:rdf/type :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subClassOf :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subPropertyOf :rdfs/subPropertyOf :igraph/subsumedBy]

```


<a name="h2-license"></a>
## License

Copyright © 2019-20 Eric D. Scott

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
