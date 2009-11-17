(ns transform
  (:use [clojure.set :only (union)]
        [clojure.contrib.duck-streams :only (read-lines)]
	[clojure.contrib.json.read :only (read-json)]
	[clojure.contrib.datalog.database :only (make-database add-tuples)])
  (:import [com.metaweb.hadoop.bfg BFGMem Triple]))

; storage
(defn bfg-store 
  ([] (BFGMem.))
  ([triples]
     (let [bfg (BFGMem.)]
       (dorun (for [t triples] (. bfg add t)))
       bfg)))

(def clj-base
     (make-database 
      (relation :triple [:s :p :o])
      (index :triple :s)
      (index :triple :p)
      (index :triple :o)))

(defn clj-store [triples]
  (apply add-tuples clj-base triples))

(defn triple->map [triple]
  {:s (.getSub triple)
   :p (.getPred triple)
   :o (.getObj triple)})

(defn triple->vec [triple]
  [(.getSub triple)
   (.getPred triple)
   (.getObj triple)])

(defn map->triple [spo]
  (Triple. spo))

(defn read-json-lines [file]
  (map read-json (read-lines file)))

(defn bfg-triples [file]
  (map map->triple (read-json-lines file)))


; query
(defn get-bindings [triple]
     (set (filter keyword? triple)))

(defn triples- [store [s p o]]
  (iterator-seq (. store query s p o)))

(defn triples [store s p o]
  (map triple->map (query- store [s p o])))

(defmacro compile-query [store ids start & query]
  (let [bindings (apply union (map get-bindings query))]
    bindings))


(defn nullify-free-vars [query]
  (map #(if (keyword? %) nil %) query))


(defn substitute-with [bindings queries]
  (map #(replace bindings %) queries))

(defn partition-with [pred coll]
  (loop [l []
	 r []
	 hd (first coll)
	 tl (rest coll)]
    (if hd
      (if (pred hd)
	(recur (conj l hd) r (first tl) (rest tl))
	(recur l (conj r hd) (first tl) (rest tl)))
      [l r])))

(defn zip [& seqs]
  (lazy-seq 
   (when (and (seq seqs) (seq (first seqs)))
     (cons (map first seqs)
	   (apply zip (map rest seqs))))))


(defn match [pattern instance]
  (loop [bindings {}
	 pattern pattern
	 instance instance]
    (let [p (first pattern)
	  i (first instance)]
      (if (and p i)
	(recur (if (keyword? p) (assoc bindings p i) bindings)
	       (rest pattern)
	       (rest instance))
	bindings))))


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


(defn has-unbound? [clause bindings]
  (some #(and (keyword? %)
	      (not (find bindings %)))
	clause))


(defn run-query- [store queries bindings triples]
  ; execute subqueries of relevant queries
  (let [[relevant _] (partition-with #(has-unbound? % bindings) queries)]
    (when-let [bound-subqueries (seq (substitute-with bindings relevant))]
      (dorun (for [query bound-subqueries]
	       (run-subquery- store query queries bindings triples))))
    triples))


(defn run-query [store ids start & queries]
  (let [result (BFGMem.)]
    (dorun (for [id ids] (run-query- store queries {start id} result)))
    result))
  