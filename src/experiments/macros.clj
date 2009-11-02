; scheme-like case expression
(defmacro case [test-value & clauses]
  (let [make-cond-clause (fn [clause]
                           (if (= (first clause) :else)
                             clause
                             (list (list = (first clause) test-value)
                                   (second clause))))]
    `(cond ~@(apply concat (map make-cond-clause clauses)))))


(defmacro avg [& args]
  `(/ (+ ~@args) ~(count args)))