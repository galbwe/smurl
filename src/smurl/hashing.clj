(ns smurl.hashing
  (:require [clojure.string :refer [join, upper-case]]))

(def DIGITS (->> (range 10) (map str)))
(def CHARS-LOWER "abcdefghijklmnopqrstuvwxyz")
(def CHARS-UPPER (map upper-case CHARS-LOWER))
(def CHARS (concat DIGITS CHARS-LOWER CHARS-UPPER))

(defn- euclidean-algo-step [[q _]] [(quot q 62) (rem q 62)])

(defn base-62
  "Converts a base 10 integer into a base 62 string representation"
  [x]
  (if (int? x) (if (= x 0)  "0"

                   (if (< x 0) (str "-" (base-62 (* -1 x))) (let [b62-digits (->> [x 0]
                                                                                  (iterate euclidean-algo-step)
                                                                                  (take-while (fn [[q r]] (or (> q 0) (> r 0))))
                                                                                  (map (fn [[_ r]] r))
                                                                                  (drop 1)
                                                                                  reverse
                                                                                  (map (fn [i] (nth CHARS i))))]
                                                              (join b62-digits))))
      (throw (ex-info "Invalid input: base-62 accepts an int argument." {:method "base-62" :input x}))))

