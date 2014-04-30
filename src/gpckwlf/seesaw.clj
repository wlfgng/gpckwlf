(ns gpckwlf.seesaw
  "Extensions to seesaw"
  (:require [seesaw.core :refer [abstract-panel]])
  (:import [util.java WrapLayout]))

(defn wrap-panel
  ""
  ([& opts]
     (abstract-panel (WrapLayout.) opts)))
