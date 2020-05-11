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
(ns scanner.main
  (:require [clojure.string          :as str]
            [clojure.tools.cli       :as cli]
            [clojure.tools.logging   :as log]
            [scanner.tools.exit-code :as ec]
            [scanner.core            :as c])
  (:gen-class))

(def ^:private cli-opts
  [["-r" "--repo user/repo" "GitHub repo coordinates"]
   ["-c" "--config FILE" "Repo Scanner config yaml file"]
   ["-h" "--help"]])

(defn- usage
  [options-summary]
  (str/join
   \newline
   ["Repo Scanner."
    ""
    "Usage: repo-scanner [options] command"
    ""
    "Options:"
    options-summary
    ""
    (str "Available tools:\n\t" (str/join "\n\t" c/tool-names))]))

(defn- error-message
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn- exit
  ([exit-code] (exit exit-code nil))
  ([exit-code message]
   (ec/set-exit-code exit-code)
   (when-not (str/blank? message)
     (println message))
   (flush)
   (System/exit (ec/get-exit-code))))

(defn -main
  [& args]
  (log/info "repo-scanner started")
  (try
    (let [{:keys [options errors summary]} (cli/parse-opts args cli-opts)]
      (cond
        (:help options) (exit 0 (usage summary))
        errors          (exit 1 (error-message errors)))
      (c/run-tool options))
    (catch Exception e
      (log/error "repo-scanner finished unsuccessfully" e)
      (exit 2)))
  (log/info "repo-scanner finished successfully")
  (exit 0))
