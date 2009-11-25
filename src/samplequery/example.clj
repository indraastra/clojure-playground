(ns samplequery.example
  (:use [samplequery :only (run-query)]
        [bfg :only (triples bfg-triples bfg-store vec->triple)]))

;;(def my-triples (bfg-triples "data/5editions.json"))
(def my-triples
     (map vec->triple
          [["b1" "ol:id" "olb1"]
           ["b1" "ol:isbn_10" "isbn1"]
           ["b1" "ol:authors" "a1"]
           ["a1" "ol:books" "b1"]
           ["a1" "ol:books" "b2"]
           ["a1" "ol:name" "name1"]
           ["b2" "ol:id" "olb2"]
           ["b2" "ol:authors" "a1"]
           ["b3" "ol:id" "olb3"]
           ["b3" "ol:isbn_10" "isbn2"]]))

(def my-db (bfg-store my-triples))
(def ol-ids 
     (map :s (triples my-db nil "ol:id" nil)))

(def my-query 
     [[:b "ol:id" :uid]
      [:b "ol:authors" :a]
      [:a "ol:name" :an]
      [:b "ol:isbn_10" :isbn10]
      [:b "ol:isbn_13" :isbn13]])

(def result (run-query my-db
                       [:b "ol:id" :uid]
                       [:b "ol:authors" :a]
                       [:a "ol:name" :an]
                       [:b "ol:isbn_10" :isbn10]
                       [:b "ol:isbn_13" :isbn13]))



