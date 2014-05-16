(ns gpckwlf.wlf-client
  "Client interface"
  (:import [pckwlf.java PckClient RespType]))

(defn success?
  ([response]
     (= RespType/SUCCESS (.getType response))))

(defn failure?
  ([response]
     (= RespType/FAILURE (.getType response))))

(defn results?
  ([response]
     (= RespType/RESULTS (.getType response))))

(defn error?
  ([response]
     (= RespType/ERROR (.getType response))))

(defn make-client
  "Makes a client object"
  ([] (new PckClient)))

(defn login-account
  "Logs client in. Returns true if successful, else false."
  ([client username password]
     (try
       (success? (.login client username password))
       (catch Exception e
         false))))

(defn new-account
  "Creates new account"
  ([client username password]
     (.signUp client username password)))

(defn add-entry
  "Adds/updates an entry"
  ([client tag username password]
     (.add client tag username password)))

(defn remove-entry
  "Delete"
  ([client tag]
     (.remove client tag)))

(defn get-all
  "Gets all data from server"
  ([client]
     (let [response (.getAll client)]
       (.getResults response))))

(defn get-tags
  "Gets all tags in the results map."
  ([results] (.keySet results)))

(defn get-by-tag
  "Gets a map of password data given a results map and a tag"
  ([results tag]
     (let [entry (.get results tag)
           [username password] (clojure.string/split entry #",")]
       {:username username
        :password password})))
