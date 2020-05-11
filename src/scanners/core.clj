;
; Copyright 2017 Fintech Open Source Foundation
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
  (:require [clojure.string :as str]
            [clojure.io     :as io]
            [clj-yaml.core  :as yaml]))

(defn- run-scanner
  "Runs the given scanner"
  [config]
  (let [cmd    (str (:fn config) "/check")
        cmd-fn (resolve (symbol (str/lower-case cmd)))]
    (cmd-fn config)))

(defn run-scanners
  "Runs the repo-scanner tool."
  [options]
  ; TODO - checkout GitHub repo via --repo param (see main.clj)
  ; TODO - read YAML config file from repo
  (let [yaml (:config options)
        file (io/resource yaml)
        config (yaml/parse-string file)]
    (map #(run-scanner %) config)))