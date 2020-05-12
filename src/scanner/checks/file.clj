(ns scanner.checks.file
  (:require [clojure.string  :as str]
            [clojure.java.io :as io]))

(def ^:private msg-includes "Cannot find string '%s'")

(def ^:private msg-excludes "String '%s' found")

(defn check
  "Returns check"
  [repo config]
  (let [rel-path     (:file config)
        path         (io/file repo rel-path)
        exists       (.exists (io/as-file path))
        content      (io/file path)
        not-includes (remove #(str/includes? content %) (:includes config))
        excludes     (filter #(str/includes? content %) (:excludes config))
        incl-msg     (map #(format msg-includes % rel-path) not-includes)
        excl-msg     (map #(format msg-excludes % rel-path) excludes)
        ret          {:file rel-path :exists exists}
        ret-incl     (if (empty? incl-msg) ret (assoc ret :includes incl-msg))
        ret-excl     (if (empty? excl-msg) ret-incl (assoc ret-incl :excludes excl-msg))]
    ret-excl))