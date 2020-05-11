;
; Copyright 2020 Fintech Open Source Foundation
; SPDX-License-Identifier: Apache-2.0
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
(ns scanner.core
  (:require [clojure.string          :as str]
            [clojure.java.io         :as io]
            [clj-jgit.porcelain      :as jgit]
            [scanner.tools.cli-utils :as cu]
            [clj-yaml.core           :as yaml]))

(def ^:private no-config-error "Couldn't find a config file. Make sure it's either passed with --config or .github/repo-scanner.yml is present in your repo")

(defn- run-scanner
  "Runs the given scanner"
  [repo-dir config]
  (let [cmd    (str (:fn config) "/check")
        cmd-fn (resolve (symbol (str/lower-case cmd)))]
    (cmd-fn repo-dir config)))

(defn run-scanners
  "Runs the repo-scanner tool."
  [repo-coords options]
  (let [base-dir (System/getProperty "java.io.tmpdir")
        repo-url (str "https://github.com/" repo-coords ".git")
        repo-dir (str base-dir repo-coords)
        repo-yml (str repo-dir ".github/repo-scanner.yml")
        cfg-yml  (:config options)]
    ; clone the repo
    (jgit/git-clone repo-url :dir repo-dir)
    ; load config from config first, then from the repo, else fail
    (let [yaml-file (cond
                      (.exists (io/as-file cfg-yml)) (io/resource cfg-yml)
                      (.exists (io/as-file repo-yml)) (io/resource repo-yml)
                      :else    (cu/exit 1 (cu/error-message [no-config-error])))
          config (yaml/parse-string yaml-file)]
      (map #(run-scanner (io/file repo-dir) %) config))))