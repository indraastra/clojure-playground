; START:ns
(ns experiments.onlisp)
; END:ns

(defn make-db []
  (atom {}))

(def *default-db* (make-db))

(defn clear-db
  ([] (clear-db *default-db*))
  ([db]
     (dosync
      (swap! db
             empty))))


(defmacro db-query
  ([key]
     `(db-query *default-db* ~key))
  ([db key]
     `(@~db ~key)))


(defn db-push
  ([key val] (db-push *default-db* key val))
  ([db key val]
     (dosync
      (swap! db
             assoc key (cons val (db-query db key))))))


(defmacro fact [pred & args]
     `(do (db-push *default-db* '~pred '~args)
          '~args))
