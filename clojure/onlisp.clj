;(defstruct triple :s :p :o)
;
;(defn add-records [db & triples] (into db triples))
;
;(defn init-store []
;  {})

(def *default-db* (ref {}))

(defn make-db []
  {})

(defn clear-db [db]
  (empty db))

(defmacro db-query [db key]
  `(~db ~key))

(defn db-push [db key val]
  (assoc db key (cons val (db-query db key))))

(defmacro fact [db pred & args]
  `(do (db-push ~db '~pred '~args)
      '~args))
