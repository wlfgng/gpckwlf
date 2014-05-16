(ns gpckwlf.core
  "Launches gpckwlf"
  (:require [seesaw.core :refer [invoke-later]]
            [gpckwlf.app :refer [launch]])
  (:gen-class))

(defn -main
  "Launch gpckwlf"
  ([& args] (invoke-later (launch))))
