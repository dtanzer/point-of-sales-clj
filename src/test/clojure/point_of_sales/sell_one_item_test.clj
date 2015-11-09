(ns point-of-sales.sell-one-item-test
  (:require
    [clojure.test :as test]))

(def display (atom {}))

(def ^:private scanned-empty-barcode-message-template "Scanning error: empty barcode")
(def ^:private product-not-found-message-template "Product not found for %s")
(def ^:private price-template "%s")
(def ^:private prices-by-barcode {"12345" "$7.95"
                                  "23456" "$12.50"})

(defn find-price [prices-by-barcode barcode]
  (get prices-by-barcode barcode))

(defn format-response-for-barcode [barcode]
  (if (empty? barcode)
    (format scanned-empty-barcode-message-template)
    (if-let [price (find-price prices-by-barcode barcode)]
      (format price-template price)
      (format product-not-found-message-template barcode))))

(defn on-barcode [barcode]
  (swap! display assoc :text (format-response-for-barcode barcode)))

(test/deftest sell-one-item-test
  (test/testing "product found"
    (on-barcode "12345")
    (test/is (= "$7.95" (:text @display))))

  (test/testing "another product found"
    (on-barcode "23456")
    (test/is (= "$12.50" (:text @display))))

  (test/testing "product not found"
    (on-barcode "99999")
    (test/is (= "Product not found for 99999"
                (:text @display))))

  (test/testing "empty barcode"
    (on-barcode "")
    (test/is (= "Scanning error: empty barcode"
                (:text @display)))))
