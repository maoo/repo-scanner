(ns scanner.main-test
  (:require [clojure.test :refer :all]
            [scanner.main :refer :all]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version"))

; Tune down logging noise from Apache HTTP client - it's rather noisy by default
(.setLevel (java.util.logging.Logger/getLogger "org.apache.http") java.util.logging.Level/SEVERE)

(deftest main-test
  ; (testing "Invalid args"
    ; (is (= nil (-main nil)))
    ; (is (= nil (-main ["-p" "whatever"])))
    ; (is (= nil (-main "run" "-c" "config.yml" "-r" "maoo/repo-scanner"))))
  (testing "Valid args"
    (is (= nil (-main "-c" "./test/finos-compliance.yml" "-r" "maoo/repo-scanner")))))