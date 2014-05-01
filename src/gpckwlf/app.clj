(ns gpckwlf.app
  "Main application."
  (:require [seesaw bind core]
            [gpckwlf seesaw]))

(declare add-tile delete-tile site-tile)

(def tile-width 100)
(def tile-height 100)

(def tiles (atom (sorted-map)))

(defn add-tile
  ([tag]
     (swap! tiles assoc tag (site-tile tag))))

(defn delete-tile
  ([tag]
     (swap! tiles dissoc tag)))

(defn add-form
  ([]
     (seesaw.core/grid-panel
      :columns 2
      :items ["Tag" (seesaw.core/text :id :tag)
              "Username" (seesaw.core/text :id :username)
              "Password" (seesaw.core/password :echo-char \*
                                               :id :password)])))

(defn add-dialog
  ([]
     (let [form (add-form)]
       (seesaw.core/dialog
        :content form
        :option-type :ok-cancel
        :type :question
        :success-fn (fn [e] (add-tile (seesaw.core/select form [:#items
                                                               :#tag])))))))

(defn add-window
  ([]
     (seesaw.core/frame
      :title "Add site"
      :content (add-dialog)
      :resizable? false
      :on-close :dispose)))

(defn add-popup
  ([e]
     (-> (add-window)
         seesaw.core/pack!
         seesaw.core/show!)))

(def add-button
  (seesaw.core/button :text "+"
                      :listen [:action add-popup]))

(defn site-tile
  "Returns a new site tile with the given tag."
  ([tag] {:pre (string? tag)}
     (seesaw.core/vertical-panel
      :items [(seesaw.core/horizontal-panel
               :items [tag
                       (seesaw.core/button
                        :text "X"
                        :listen [:action (fn [e] (delete-tile tag))])])]
      :height tile-height
      :width  tile-width)))

(def login-username
  (seesaw.core/text :id :username))

(def login-password
  (seesaw.core/password :echo-char \*
                        :id :password))

(def login-button
  (seesaw.core/button :text "Login"))

(def exit-button
  (seesaw.core/button :text "Exit"))

(def login-panel
  (seesaw.core/grid-panel
   :columns 2
   :items ["Username"   login-username
           "Password"   login-password
           login-button exit-button]))

(comment
  (def login-panel
    (seesaw.forms/forms-panel
     :items ["Username" login-username
             "Password" login-password])))

; use a border-panel instead
(def site-tiles
  (seesaw.core/scrollable
   (gpckwlf.seesaw/wrap-panel
    :align :left
    :items (concat (vals @tiles)
                   [add-button])
;    :items
;    ["These" "will" "be" "tiles." "Look" "how" "they" "rearrange"
;     "themselves." "Isn't" "that" "cool?"]
    )
   :hscroll :never
   :vscroll :as-needed))

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

(seesaw.core/listen exit-button
                    :action (fn [e] (System/exit 0)))

(seesaw.bind/bind tiles
                  (seesaw.bind/transform #(-> %
                                              vals
                                              (concat [add-button])))
                  (seesaw.bind/property site-tiles :items))

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
