(ns samplequery
  (:use [clojure.set :only (union)]
        [util]
        [bfg])
  (:import [com.metaweb.hadoop.bfg BFGMem]))


;; query
(defmacro compile-query [store ids start & query]
  (let [bindings (apply union (map get-bindings query))]
    bindings))


(declare run-query-)

(defn run-subquery- [store query queries bindings triples]
  (let [bfg-query (nullify-free-vars query)]
    (dorun (for [bfg-triple (triples- store bfg-query)]
	     (let [triple     (triple->vec bfg-triple)
               bindings-δ (match query triple)
               bindings+  (into bindings bindings-δ)]
	       (do 
             (. triples add bfg-triple)
             (run-query- store queries bindings+ triples)))))
    triples))

;; execute subqueries of relevant queries
(defn run-query- [store queries bindings triples]
  (let [[relevant _] (partition-with #(has-unbound? % bindings) queries)]
    (when-let [bound-subqueries (seq (substitute-with bindings relevant))]
      (dorun (for [query bound-subqueries]
               (run-subquery- store query queries bindings triples))))
    triples))


(defn run-query-from-start [store ids start & queries]
  (let [unbound (apply union (map get-bindings queries))
        result (BFGMem.)]
    (when (contains? unbound start)
      (dorun (for [id ids] (run-query- store queries {start id} result))))
    result))


(defn run-query [store & queries]
  (let [result (BFGMem.)]
    (run-query- store queries {} result)))
