# <img src="http://ericdscott.com/NaturalLexiconLogo.png" alt="NaturalLexicon logo" :width=100 height=100/> ont-app/igraph-vocabulary

Support for creating keyword identifiers (KWIs) in [IGraph](https://github.com/ont-app/igraph)-compliant graphs and aligning data in _IGraph_ graphs to data in other formats. 

## Contents
- [Dependencies](#h2-dependencies)
- [Overview](#h2-overview)
- [The `mint-kwi` method](#h2-the-mint-kwi-method)
- [The `resolve-namespace-prefixes` function](#h2-resolve-namespace-prefixes-function)
- [Supporting ontology](#h2-supporting-ontology)
  - [Default `subsumed-by` declarations](#h3-default-subsumed-by)
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
   [ont-app.igraph.core :as igraph]
   [ont-app.vocabulary.core :as voc]
   [ont-app.igraph-vocabulary.core :as igv]
   ))
```

<a name="h2-overview"></a>
## Overview

`ont-app/igraph` and `ont-app/vocabulary` are independent
libraries. The former defining a generic graph-based primitive
container, and the latter defining a way of treating namespaced
Keyword Identifies (KWIs) as though they were URIs comprising a public
vocabulary, aligned to RDF namespaces. `ont-app/igraph-vocabulary` is
responsible for basic functionality involving the intersection or
these two, providing some basic ontological constructs that express
common features of IGraph implementations.

For purposes of this discussion, the term
[_URI_](https://en.wikipedia.org/wiki/Uniform_Resource_Identifier) and
[_IRI_](https://en.wikipedia.org/wiki/Internationalized_Resource_Identifier)
will be used interchangably. In practice clojure keywords are encoded
as UTF-8, making them IRIs. The term _URI_ has a much greater
mind-share, to the extent that any of this stuff has mind-share at
all.

A KWI is a qualified clojure keyword used exactly the way a URI is
used in RDF. Ideally URIs should be importable as KWIs and integrated
into IGraph-based models, and conversely KWIs should be exportable as
standard RDF-type URIs.

Since this is the lowest level at which both _igraph_ and _vocabulary_
are in play. This is the level that defines a small supporting
ontology igraph-based graphs.

<a name="h2-the-mint-kwi-method"></a>
## The `mint-kwi` method

When building a model in a graph representation, it's a common
occurrence to need to create canonically named KWIs on the fly,
especially to identify newly encountered instances of some class of
entities. For example `:employee/123`, mappable to
`http://rdf.mycompany.com/employee#123`.

The _mint-kwi_ method returns a keyword identifier based on a _head_
keyword (which typically names a class) and any number of other
arguments (typically a set of predicate/value pairs). The method is
[dispatched](https://clojuredocs.org/clojure.core/defmulti) on the
head.

Best practice is to provide as many predicate/value pairs as are
necessary to uniquely distinguish the thing being identified within
whatever universe you're playing in.

Let's assume the following namespace declaration (note the metadata in
the ns):

```
(ns ont-app.igraph-vocabulary.core-test
  {:vann/preferredNamespacePrefix "eg"
   :vann/preferredNamespaceUri "http://example.com/"
   }
 (require
 [ont-app.igraph.core :as igraph]
 [ont-app.igraph-vocabulary.core :as igv :refer [mint-kwi]]
 [ont-app.vocabulary.core :as voc]
 ))
```

The default method will simply concatenate the names of the keywords provided:

```
> (mint-kwi :eg/Example :eg/number 1)
:eg/Example_number_1
>
``` 

Since we have the proper metadata declared for core-test module,
we can get RDF equivalents:

```
> (voc/qname-for :eg/Example_number_1)
"eg:Example_number_1"
>
> (voc/uri-for :eg/Example_number_1)
"http://example.com/Example_number_1"
> 
```

We can write a custom method dispatched on the head:

```
> (defmethod mint-kwi :eg/Example
    [head & {:keys [:eg/number]}]
    (keyword (namespace head)
      (str (name head)
         "_"
         number)))
#multifn[mint-kwi 0x535c5c20]
> (mint-kwi :eg/Example :eg/number 2)
:eg/Example_2
> 
```
<a name="h2-resolve-namespace-prefixes-function"></a>
## The `resolve-namespace-prefixes` function

It may be the case that you are referencing a graph whose URIs may be
in need of translating into KWIs. In such cases, you can apply this
function as a
[reduce-spo](https://cljdoc.org/d/ont-app/igraph/0.1.5/api/ont-app.igraph.core#reduce-spo):

```
> (def g (igraph/reduce-spo resove-namespace-prefixes g))
```

This will reset the value of igraph _g_ with its URIs translated into
their equivalent KWIs, wherever the appropriate namespace metadata has
been supplied.

<a name="h2-supporting-ontology"></a>
## Supporting ontology

These declarations are defined as the constant _igv/ontology_, an
instance of the simple IGraph implementation
_ont-app.igraph.graph/Graph_.

### The _igraph_ namespace.

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

| KWI (_:igraph/*_) | type | description |
| --- | --- | --- |
| CompiledObject | Class | A graph element compiled in the native execution environment, typically a function, a graph, or a vector. This is platform-specific. The intent here is to facilitate inter-operation with other platforms, e.g. python, using a shared ontology. |
| Function | CompiledObject | A compiled object executable in the native execution environment as a pure function |
| Vector | CompiledObject | A compiled container holding an integer-indexed sequence |
| Graph | CompiledObject | An compiled implementation of IGraph |
| projectedRange | property | "_p_ `projectedRange` _obj_" asserts that the property _p_ will have _obj_ in its range, where _obj_ is a subclass of _CompiledObject_. As a compiled object, `obj` is neither a class nor a literal. |
| compiledAs | property | "_kwi_ `compiledAs` _obj_" asserts that an entity identified across platforms as _kwi_ (or its equivalent URI) is implemented in the current exectuion environment as CompiledObject _obj_ |
| subsumedBy | property | "_x_ subsumedBy _y_" asserts a subsumption relationship between _x_ and _y_. This may be used in Clojure to [derive](https://clojuredocs.org/clojure.core/derive)/[underive](https://clojuredocs.org/clojure.core/underive) values to use in method dispatch, for example. |

<a name="h3-default-subsumed-by"></a>
### Default `subsumedBy` declarations

The following _subsumedBy_ declarations are defined in _ont-app.igraph-vocabulary.ont.cljc_:

```
  [:rdf/type :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subClassOf :rdfs/subPropertyOf :igraph/subsumedBy]
  [:rdfs/subPropertyOf :rdfs/subPropertyOf :igraph/subsumedBy]
```

<a name="h2-license"></a>
## License

Copyright © 2019-20 Eric D. Scott

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

<table>
<tr>
<td width=75>
<img src="http://ericdscott.com/NaturalLexiconLogo.png" alt="NaturalLexicon logo" :width=50 height=50/> </td>
<td>
<p>Natural Lexicon logo - Copyright © 2020 Eric D. Scott. Artwork by Athena M. Scott. </p>
<p>Released under <a href="https://creativecommons.org/licenses/by-sa/4.0/">Creative Commons Attribution-ShareAlike 4.0 International license</a>. Under the terms of this license, if you display this logo or derivates thereof, you must include an attribution to the original source, with a link to https://github.com/ont-app, or  http://ericdscott.com. </p> 
</td>
</tr>
<table>
