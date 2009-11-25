(ns transform
  (:gen-class)
  (:use [clojure.contrib.fcase :only (case)])
  (:require [clojure-hadoop.wrap :as wrap]
            [clojure-hadoop.defjob :as defjob]))

(defmacro s [_s]
  [_s :p :o])

(defmacro p [_p]
  [:s _p :o])

(defmacro o [_o]
  [:s :p _o])

(defn relabel-nodes- [[in out]]
  `((:rule (:match   ~(s in))
           (:rewrite ~(s out)))
    (:rule (:match   ~(o in))
           (:rewrite ~(o out)))))

(defn relabel-nodes [pairs]
  (apply concat (map relabel-nodes- pairs)))

(defn relabel-edges- [[in out]]
  `(:rule (:match   ~(p in))
          (:rewrite ~(p out))))

(defn relabel-edges [pairs]
  (map relabel-edges- pairs))

(defn match-rewrite- [[in out]]
  `(:rule (:match   ~in)
          (:rewrite ~out)))

(defn match-rewrite [pairs]
  (map match-rewrite- pairs))

(def rule-compilers 
     {:relabel-nodes relabel-nodes
      :relabel-edges relabel-edges
      :match-rewrite match-rewrite
      :rule          list})

(defmacro transform [& rules]
  (loop [rules (seq rules)
         crules []]
    (if (not (empty? rules))
      (let [[rule-type & rule-body] (first rules)
            rule-compiler (rule-compilers rule-type)]
        (recur (rest rules)
               (into crules (rule-compiler rule-body))))
      `(quote ~crules))))
        

(defmacro apply-transform [xform graph] )