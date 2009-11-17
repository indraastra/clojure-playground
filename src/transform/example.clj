(ns transform.example
  (:use [transform :only (bfg-triples bfg-store triples run-query)]))

(def my-triples (bfg-triples "data/5editions.json"))
(def my-db (bfg-store my-triples))
(def ol-ids 
     (map :s (triples my-db nil "ol:id" nil)))

(def my-query 
     [[:b "ol:id" :uid]
      [:b "ol:isbn_10" :isbn10]
      [:b "ol:isbn_13" :isbn13]])

(def result (run-query my-db ol-ids :b 
		       [:b "ol:id" :uid]
		       [:b "ol:isbn_10" :isbn10]
		       [:b "ol:isbn_13" :isbn13]))



