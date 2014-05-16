(ns gpckwlf.client
  "Client interface"
  (import [java.pckwlf PckClient]))

(defn make-client
  "Makes a client object"
  ([] (new PckClient)))

(defn login-account
  "Logs client in"
  ([client username password]
     (.login client username password)))

(defn new-account
  "Creates new account"
  ([client username password]
     (.signUp client username password)))
