(ns scanner.checks.file
  (:require [clojure.string :as str]
            [clojure.io     :as io]))

(def msg-includes "Cannot find string '%s' on file '%s'")

(def msg-excludes "String '%s' found on file '%s'")

(defn- check
  "Returns check"
  [config]
  (let [path         (:file config)
        exists       (io/exists? path)
        content      (io/resource path)
        not-includes (map #(not (str/includes? content %)) (:includes config))
        excludes     (map #(str/includes? content %) (:excludes config))
        incl-msg     (map #(str/format msg-includes % path) not-includes)
        excl-msg     (map #(str/format msg-excludes % path) excludes)]
    {:exists     exists
     :includes incl-msg
     :excludes excl-msg}))