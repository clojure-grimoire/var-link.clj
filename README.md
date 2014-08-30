# var-link.clj

This project provides an encoder/decoder/verifier tripple for
[var-link](http://github.com/clojure-grimoire/var-link) URIs.

[![Clojars Project](http://clojars.org/org.clojure-grimoire/var-link/latest-version.svg)](http://clojars.org/org.clojure-grimoire/var-link)

## Usage

```Clojure
user> (use 'var-link.core)
nil
user> (->var-link :var "org.clojure" "clojure" "1.6.0" "clojure.core" "conj")
#<URI var:org.clojure/clojure/1.6.0/clojure.core%2Fconj>
user> (var-link->map *1)
{:proto "var", :groupId "org.clojure", :artifactId "clojure", :version "1.6.0", :namespace "clojure.core", :name "conj"}
user> (var-link? *1)
true
user> (->var-link *2)
#<URI var:org.clojure/clojure/1.6.0/clojure.core%2Fconj>
user>
```

## License

Copyright © 2014 Reid McKenzie

Distributed under the Eclipse Public License, the same as Clojure.

