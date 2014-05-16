(ns gpckwlf.password
  "Password related functions")

(defn text->clipboard
  "Copies text to clipboard"
  ([text]
     (-> (java.awt.Toolkit/getDefaultToolkit)
         .getSystemClipboard
         (.setContents (new java.awt.datatransfer.StringSelection text)
                       nil))))
