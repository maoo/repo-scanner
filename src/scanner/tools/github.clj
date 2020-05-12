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
(ns scanner.tools.github
  (:require [clojure.string        :as str]
            [clojure.java.io       :as io]
            [clojure.tools.logging :as log]
            [mount.core            :as mnt :refer [defstate]]
            [lambdaisland.uri      :as uri]
            [clj-http.client       :as http]
            [tentacles.repos       :as tr]
            [tentacles.issues      :as ti]
            [tentacles.search      :as ts]
            [tentacles.orgs        :as to]
            [tentacles.users       :as tu]
            [scanner.config        :as cfg]))

(defn- rm-rf
  [^java.io.File file]
  (when (.isDirectory file)
    (doseq [child-file (.listFiles file)]
      (rm-rf child-file)))
  (io/delete-file file true))

(defstate auth
  :start (str "finos:" (System/getenv "GITHUB_TOKEN")))

(defstate opts
  :start {:throw-exceptions true :all-pages true :per-page 100 :auth auth :user-agent "Repo Scanner"})

(defstate github-revision
  :start (:github-revision cfg/config))

(defn- parse-github-url-path
  "Parses the path elements of a GitHub URL - useful for retrieving org name (first position) and repo name (optional second position)."
  [url]
  (if-not (str/blank? url)
    (remove str/blank? (str/split (:path (uri/uri url)) #"/"))))

(defmacro ^:private call-gh
  [& body]
  `(try
     ~@body
     (catch clojure.lang.ExceptionInfo ei#
       (if-not (= 404 (:status (ex-data ei#)))
         (throw ei#)))))

(defn invite-member
  "Invites a github user to a given org"
  [org user]
    ; TODO - enable it only after notifying the community
    ; (call-gh (:body (http/put
    ;  (str "https://api.github.com/orgs/" org "/memberships/" user)))))
    (println "Invited user " user " to github " org " org"))

; (defn collaborator-logins
;   "Returns a list containing the logins of all collaborators in the given repo, or for all repos if none is provided."
;   [repo-url]
;   (map :login (collaborators repo-url)))

; (defn repo-logins
;   [repo-url]
;   (let [logins (collaborator-logins repo-url)]
;       (map #(assoc {}
;                    :login %
;                    :repo repo-url) logins)))

; (defn- admins
;   "List the admins of the given repository."
;   [repo-url]
;   (filter #(:admin (:permissions %)) (collaborators repo-url)))

; (defn admin-logins
;   "List the logins of the admins of the given repository."
;   [repo-url]
;   (map :login (admins repo-url)))

; (defn- committers
;   "List the committers (non-admin collaborators) of the given repository."
;   [repo-url]
;   (remove #(:admin (:permissions %))) (collaborators repo-url))

; (defn committer-logins
;   "List the logins of the committers of the given repository."
;   [repo-url]
;   (map :login (committers repo-url)))

; Note: functions that call GitHub APIs are memoized, so that when tools are "stacked" they benefit from cached GitHub API calls

(defn content-fn
  "Returns the contents of file"
  [org repo path]
  (call-gh
    (:body (http/get
     (str "https://raw.githubusercontent.com/" org "/" repo "/master/" path)))))
(def content (memoize content-fn))

(defn folder-fn
  "Returns the metadata of a folder in GitHub"
  [org repo path]
  (call-gh
    (:body (http/get
     (str "https://api.github.com/repos/" org "/" repo "/contents/" path)))))
(def folder (memoize folder-fn))

(defn pending-invitations-fn
  "Returns the list of pending invitations for a given org"
  [org-name]
  (call-gh
    (:body (http/get
     (str "https://api.github.com/orgs/" org-name "/invitations")))))
(def pending-invitations (memoize pending-invitations-fn))

(defn- collaborators-fn
  "Returns the collaborators for the given repo, or nil if the URL is invalid."
  [repo-url & [affiliation]]
  (log/debug "Requesting repository collaborators for" repo-url)
  (if-not (str/blank? repo-url)
    (let [[org repo] (parse-github-url-path repo-url)
          collab-opts (assoc opts :affiliation affiliation)]
      (if (and (not (str/blank? org))
               (not (str/blank? repo)))
        (call-gh (tr/collaborators org repo collab-opts))))))
(def collaborators (memoize collaborators-fn))

(defn- teams-fn
  "Returns the teams for the given repo, or nil if the URL is invalid."
  [repo-url]
  (log/debug "Requesting repository teams for" repo-url)
  (if-not (str/blank? repo-url)
    (let [[org repo] (parse-github-url-path repo-url)]
          (call-gh (tr/teams org repo opts)))))
(def teams (memoize teams-fn))

(defn- org-members-fn
  "Returns the members for the given org, or nil if the URL is invalid."
  [org-url]
  (log/debug "Requesting org members for" org-url)
  (if-not (str/blank? org-url)
    (let [[org-name] (parse-github-url-path org-url)]
          (call-gh (to/members org-name opts)))))
(def org-members (memoize org-members-fn))

(defn org-issues-fn
  "Returns first 100 open issues, with a given label, across all repos of a given org"
  [org-name label]
  (let [issues (:items (ts/search-issues ""
                                 {:label (str "\"" label "\"")
                                  :state "open"
                                  :org org-name} opts))]
    issues))
(def org-issues (memoize org-issues-fn))

(defn issues-fn
  "Returns first 100 open issues, with a given label, on a given repo"
  [org repo labels]
  (let [issues (ti/issues org
                          repo
                          (assoc opts
                                 :label (str/join "," labels)
                                 :state "open"))]
    issues))
(def issues (memoize issues-fn))

(defn- org-fn
  "Returns information on the given org, or nil if it doesn't exist."
  [org-url]
  (log/debug "Requesting org information for" org-url)
  (if-not (str/blank? org-url)
    (let [[org-name] (parse-github-url-path org-url)]
      (if-not (str/blank? org-name)
        (call-gh (to/specific-org org-name opts))))))
(def org (memoize org-fn))

(defn- repos-fn
  "Returns all public repos in the given org, or nil if there aren't any."
  [org-url]
  (log/debug "Requesting repositories for" org-url)
  (if-not (str/blank? org-url)
    (let [[org-name] (parse-github-url-path org-url)]
      (if-not (str/blank? org-name)
        (remove :private (call-gh (tr/org-repos org-name opts)))))))
(def repos (memoize repos-fn))

(defn- filter-repo
  [repo filters]
  (if (empty? filters)
    true
    (let [filter (seq (first filters))
          curr   (= (get repo (first filter))
                    (second filter))
          tail (filter-repo repo (rest filters))]
      (and curr tail))))

(defn repo-urls
  "Returns the URLs of all repos in the given org."
  [org-url & [filters]]
  (if org-url
    (let [repos (repos org-url)]
      (map :html_url
           (filter #(filter-repo % filters) 
                   repos)))))

(defn filtered-repo-urls
  "Returns the URLs of all repos in the given org."
  [org-url]
  (gh/repos-urls org-url {:fork false :archived false}))

(defn- repo-fn
  "Retrieve the data for a specific public repo, or nil if it's private or invalid."
  [repo-url]
  (log/debug "Requesting repository details for" repo-url)
  (if repo-url
    (let [[org repo] (parse-github-url-path repo-url)]
      (if (and (not (str/blank? org))
               (not (str/blank? repo)))
        (let [result (call-gh (tr/specific-repo org repo opts))]
          (if-not (:private result)
            result))))))
(def repo (memoize repo-fn))

(defn- languages-fn
  "Retrieve the languages data for a specific repo."
  [repo-url]
  (log/debug "Requesting repository languages for" repo-url)
  (if repo-url
    (let [[org repo] (parse-github-url-path repo-url)]
      (if (and (not (str/blank? org))
               (not (str/blank? repo)))
        (call-gh (tr/languages org repo opts))))))
(def languages (memoize languages-fn))

(defn- user-fn
  "Retrieve the data for a specific GitHub username, or nil if it's invalid."
  [username]
  (if username
    (call-gh (tu/user username opts))))
(def user (memoize user-fn))
