(ns smurl.hashing-tests
  (:require [clojure.test :as t] [smurl.hashing :as hash]))

(t/deftest test-base-62
  (t/testing "Converts nonnegative base 10 integers into base 62"
    (t/is (= (hash/base-62 0) "0"))
    (t/is (= (hash/base-62 10) "a"))
    (t/is (= (hash/base-62 11) "b"))
    (t/is (= (hash/base-62 35) "z"))
    (t/is (= (hash/base-62 36) "A"))
    (t/is (= (hash/base-62 37) "B"))
    (t/is (= (hash/base-62 61) "Z"))
    (t/is (= (hash/base-62 62) "10"))
    (t/is (= (hash/base-62 (+ (* 61 62 62 62) (* 11 62) 36)) "Z0bA")))
  (t/testing "Converts negative base 10 integers into base 62"
    (t/is (= (hash/base-62 -10) "-a"))
    (t/is (= (hash/base-62 -11) "-b"))
    (t/is (= (hash/base-62 -35) "-z"))
    (t/is (= (hash/base-62 -36) "-A"))
    (t/is (= (hash/base-62 -37) "-B"))
    (t/is (= (hash/base-62 -61) "-Z"))
    (t/is (= (hash/base-62 -62) "-10"))
    (t/is (= (hash/base-62 (* -1 (+ (* 61 62 62 62) (* 11 62) 36))) "-Z0bA")))
  (t/testing "Non-numeric input throws an error"
    (t/is (thrown-with-msg? clojure.lang.ExceptionInfo #"Invalid input:" (hash/base-62 "foo")))))