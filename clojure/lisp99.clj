(ns my99)

(def RUNTESTS true)

; run a test if RUNTESTS flag is set
(defmacro testfn [testfn & input-outputs]
  (let [make-test (fn [[in out]]
                    `(assert (= (apply ~testfn ~in) ~out)))]
    `(when RUNTESTS
       (do ~@(map make-test input-outputs) :pass))))


; P01 : Find the last box of a list.
(defn my-last [l]
  (if (empty? (rest l))
    (first l)
    (recur (rest l))))

; P02: Find the last but one box of a list.
(defn my-but-last [l]
  (if (empty? (rest (rest l)))
    (first l)
    (recur (rest l))))

; P03: Find the K'th element of a list.
(defn kth [l n]
  (nth l (- n 1)))

; P04: Find the number of elements of a list.
(def my-count count)

; P05: Reverse a list.
(def my-reverse reverse)

; P06: Find out whether a list is a palindrome.
(defn palindrome? [l]
  (= l (reverse l)))

; P07: Flatten a nested list structure.
(defn flatten [l]
  (if (empty? l) l
      (let [car (first l)
            cdr (rest l)]
        (if (coll? car)
          (concat (flatten car) (flatten cdr))
          (cons car (flatten cdr))))))



;; TESTS
(testfn my-last
        [['(1 2 3)] 3]
        [['(a b :this)] :this])

(testfn my-but-last
        [['(1 2 3)]     2]
        [['(a :this c)] :this])

(testfn kth
        [['(a b c d e) 3]          'c]
        [['(:left :this :right) 2] :this])

(testfn my-count
        [['(1 2 3)]  3]
        [[[:a :b :c :d]] 4])