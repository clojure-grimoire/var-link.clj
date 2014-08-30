(ns var-link.core
  (:require [clojure.string :as str]
            [cemerick.url :as url])
  (:import [java.net URI]))


(def var-link-pattern
  #"(var(\+((doc)|(src)))?):/?/?([^/]*?)/([^/]*)/([^/]*)/([^/]*)")


(defn var-link->map
  "Deconstructs a var link represented either as a URI or as a String
  into a map having the following structure:

  {:proto      Keyword
  ,:groupId    String
  ,:artifactId String
  ,:version    String
  ,:namespace  String
  ,:name       String}"
  [arg]
  (cond (string? arg)
        ,,(when-let [match (re-matches var-link-pattern arg)]
            (let [proto            (nth match 1)
                  groupId          (nth match 6)
                  artifactId       (nth match 7)
                  version          (nth match 8)
                  [namespace name] (-> (nth match 9)
                                       (url/url-decode)
                                       (str/split #"/" 2))]
              {:proto      proto
               :groupId    groupId
               :artifactId artifactId
               :version    version
               :namespace  namespace
               :name       name}))

        (instance? URI arg)
        ,,(-> arg str var-link->map)

        (map? arg)
        ,,(let [keys [:proto :groupId :artifactId :version :namespace :name]]
            (-> arg (select-keys keys)))))


(defn var-link?
  "Predicate defined over strings, maps and URIs which validates that
  the Object represents a var URI. Returns true if and only if the
  object can be converted to a valid var URI map representation,
  otherwise returns false."
  [x]
  (cond (map? x)
        ,,(let [{:keys [proto groupId artifactId
                        version namespace name]} x]
            (and (#{"var" "var+doc" "var+src"
                    :var :var/doc :var/src}
                  proto)

                 (and (string? groupId)
                      (not (empty? groupId)))

                 (and (string? artifactId)
                      (not (empty? artifactId)))

                 (and (string? version)
                      (not (empty? version)))

                 (or (string? name)
                    (symbol? name))))

        (string? x)
        ,,(var-link? (var-link->map x))

        (instance? URI x)
        ,,(var-link? (var-link->map x))

        true
        ,,false))


(defn ->var-link
  "Constructs a URI object either from a var-link? object or from a
  sequence [proto group artifact version namespace name]."
  ([arg]
     (if-let [var-map (var-link->map arg)]
       (apply ->var-link
              (map var-map
                   [:proto :groupId :artifactId
                    :version :namespace :name]))))

  ([proto group artifact version namespace name]
     {:pre [(#{"var" "var+doc" "var+src"
               :var :var/doc :var/src}
             proto)

            (and (string? group)
                 (not (empty? group)))

            (and (string? artifact)
                 (not (empty? artifact)))

            (and (string? version)
                 (not (empty? version)))

            (or (string? name)
               (symbol? name))]}
     (let [proto (or ({:var     "var"
                      :var/doc "var+doc"
                      :var/src "var+src"}
                     proto)
                    proto)]
       (URI.
        (str proto ":"
             (str (url/url-encode group)
                  "/"  (url/url-encode artifact)
                  "/"  (url/url-encode version)
                  "/"  (url/url-encode
                        (str namespace "/" name)))
             nil)))))
