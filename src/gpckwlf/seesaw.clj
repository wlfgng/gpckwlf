(ns gpckwlf.seesaw
  "Extensions to seesaw"
  (:require [seesaw.core :refer [abstract-panel]])
  (:import [util.java WrapLayout]))

(defn wrap-panel
  ""
  ([& opts]
     (abstract-panel (WrapLayout.) opts)))

(defmacro remove-borders
  "Removes all borders from button"
  ([button]
     `(doto ~button
        (.setBorder (javax.swing.BorderFactory/createEmptyBorder))
        (.setContentAreaFilled false))))
