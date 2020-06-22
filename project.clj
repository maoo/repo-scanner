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

(defproject org.finos/repo-scanner "0.1.0-SNAPSHOT"
  :description          "A GitHub Action to scan a GitHub repository."
  :url                  "https://github.com/maoo/repo-scanner"
  :license              {:name "Apache License, Version 2.0"
                         :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :min-lein-version     "2.7.1"
  :repositories         [["sonatype-snapshots" {:url "https://oss.sonatype.org/content/groups/public" :snapshots true}]
                         ["jitpack"            {:url "https://jitpack.io"
                                                :snapshots true}]]
  :plugins              [[lein-licenses "0.2.2"]
                         [lein-ancient "0.6.15"]]
  :dependencies         [[org.clojure/clojure                   "1.10.1"]
                         [mount                                 "0.1.15"]
                         [org.clojure/tools.cli                 "1.0.194"]
                         [org.clojure/tools.logging             "1.1.0"]
                         [ch.qos.logback/logback-classic        "1.2.3"]
                         [org.slf4j/jcl-over-slf4j              "1.7.30"]
                         [org.slf4j/log4j-over-slf4j            "1.7.30"]
                         [org.slf4j/jul-to-slf4j                "1.7.30"]
                         [clj-commons/clj-yaml                  "0.7.1"]
                         [lambdaisland/uri                      "1.4.54"]
                         [irresponsible/tentacles               "0.6.6"]
                         [clj-jgit                              "1.0.0-beta3"]]
  :managed-dependencies [[joda-time/joda-time "2.10.6"]
                         [clj-http            "3.10.1"]]
  :profiles             {:dev     {:dependencies [[midje      "1.9.9"]]
                                   :plugins      [[lein-midje "3.2.1"]]}
                         :uberjar {:aot :all}}
  :main                 ^:skip-aot gh-org-meta.main)
