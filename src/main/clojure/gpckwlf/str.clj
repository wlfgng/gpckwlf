(ns gpckwlf.str
  "String functions.")

(defn pad
  "Pads a string with spaces until it is of the given size."
  ([string size]
     (format (str "%-" size "s") string)))
