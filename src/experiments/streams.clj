;; Exploring Lazy Sequences with lazy-seq (based on SICP 3.5)
;;
;; Note -- whatever you do, don't try to print these at the REPL

(ns
    #^{:author "Vishal Talwar"
       :doc "Definitions of some useful infinite lazy sequences (streams)"}
  experiments.streams)

; lazy-cons was deprecated, so let's define it
(defmacro lazy-cons [first rest]
  `(lazy-seq (cons ~first ~rest)))

(def ones
     (lazy-cons 1 ones))

(println (take 10 ones))
(println)

(defn ints-from [n]
  (lazy-cons n (ints-from (+ n 1))))

(println (take 10 (ints-from 42)))
(println)

(def fibs
     (lazy-cons 0
                (lazy-cons 1
                           (map + fibs (rest fibs)))))

(println (take 10 fibs))
(println)

; some gotchas...
(def sleepy-stream
     (lazy-cons (. Thread (sleep 1000)) sleepy-stream))

(println "We might expect this to be 10 seconds...")
(time (dorun 10 sleepy-stream))
(println)

(def rand-ints
     (lazy-cons (rand-int 100) rand-ints))

(println "We might expect this to be 10 random numbers...")
(println (take 10 rand-ints))
(println)