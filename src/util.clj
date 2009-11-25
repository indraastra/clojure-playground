(ns util)

(defn nullify-free-vars [query]
  (map #(if (keyword? %) nil %) query))


(defn substitute-with [bindings queries]
  (map #(replace bindings %) queries))


(defn get-bindings [triple]
     (set (filter keyword? triple)))


(defn has-unbound? [clause bindings]
  (some #(and (keyword? %)
              (not (find bindings %)))
        clause))


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


