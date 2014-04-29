(ns gpckwlf.app
  "Main application."
  (:require [seesaw core forms]))

(def login-username
  (seesaw.core/text))

(def login-password
  (seesaw.core/password :echo-char \*))

(def login-button
  (seesaw.core/button :text "Login"))

(def login-panel
  (seesaw.core/grid-panel
   :columns 2
   :items ["Username"     ""
           login-username ""
           "Password"     ""
           login-password login-button]))

(comment
  (def login-panel
    (seesaw.forms/forms-panel
     :items ["Username" login-username
             "Password" login-password])))

(def site-tiles
  (seesaw.core/grid-panel
   :columns 3
   :items ["These" "will" "be" "tiles"]))

(def options-button
  (seesaw.core/button :text "Options"))

(def main-panel
  (seesaw.core/horizontal-panel
   :items [site-tiles options-button]))

(def card-panel
  (seesaw.core/card-panel
   :items [[login-panel :login-panel]
           [main-panel  :main-panel]]))

(seesaw.core/listen login-button
                    :action (fn [e] (seesaw.core/show-card! card-panel
                                                           :main-panel)))

(def main-window
  (seesaw.core/frame
   :title "gpckwlf"
   :content card-panel
   :on-close :exit))


(defn launch
  "Launches the main window"
  ([]
     (-> main-window
         seesaw.core/pack!
         seesaw.core/show!)))
