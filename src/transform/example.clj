(ns transform.example
  (:use [transform]))

(def >isbn> "/book/book_edition/ISBN")

(defn url->webpage [] )
(defn titleparts->title [] )
(defn clean-ISBN [] )
(defn clean-pubdate [] )
(defn produce-pagination)

(def my-graph "/user/vishal/openlibrary/edition-triples")

(def my-transform 
     (transform (:relabel-nodes [#"/a/(.*)" "/authority/openlibrary/author/\\1"]
                                [#"/b/(.*)" "/authority/openlibrary/edition/\\1"])
                (:relabel-edges ["ol:publishers"         "ol:PUBLISHER"]
                                ["ol:authors"            "ol:AUTHOR"]
                                ["ol:isbn_10"            >isbn>]
                                ["ol:isbn_13"            >isbn>]
                                ["ol:lc_classifications" "/book/book_edition/lcc"]
                                ["ol:lccn"               "/book/book_edition/LCCN"]
                                ["ol:oclc_numbers"       "/book/book_edition/OCLC_number"]
                                ["ol:other_titles"       "/common/topic/alias"])
                (:rule (:match   [:s "ol:id" :o])
                       (:produce [:s "/type/object/type" "/book/book_edition"]
                                 [:s "/type/object/type" "/common/topic"]))
                (:rule (:match   [:s "ol:url" :url])
                       (:rewrite [:s "/common/topic/webpage" url->webpage]))
                (:rule (:match   [:s "ol:title" :title]
                                 [:s "ol:subtitle" :subtitle]
                                 [:s "ol:title_prefix" :title-prefix])
                       (:rewrite titleparts->title))
                (:match-rewrite [(p "/book/book_edition/isbn") clean-ISBN
                                 (p "ol:publish_date")         clean-pubdate])
                (:rule (:match   (p "ol:pagination")
                                 (p "ol:number_of_pages"))
                       (:rewrite produce-pagination))))

(apply-transform my-transform my-graph)