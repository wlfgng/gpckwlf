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
                       (gpckwlf.seesaw/remove-borders
                        (seesaw.core/button
                            :icon (clojure.java.io/resource
                                   "gpckwlf/icons/16x16/close.png")
                                        ;                        :margin 0
                            :listen [:action (fn [e] (delete-tile tag))]))])
              ])))

(swap! tiles assoc
       "Foo" (site-tile "Foo")
       "Bar" (site-tile "Bar"))

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
  (gpckwlf.seesaw/wrap-panel
   :align :left
   :items (concat (vals @tiles)
                  [add-button])))

(def site-tiles-scroll
  (seesaw.core/scrollable site-tiles
                          :hscroll :never
                          :vscroll :as-needed))

(def refresh-button
  (seesaw.core/button :icon (clojure.java.io/resource
                             "gpckwlf/icons/32x32/refresh.png")
                      :margin 0))

(def options-button
  (seesaw.core/button :text "Options"))

(def options-panel
  (seesaw.core/horizontal-panel
   :items [refresh-button options-button]))

(def main-panel
  (seesaw.core/border-panel
   :north options-panel
   :center site-tiles))

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

;(println (clojure.java.io/resource "gpckwlf/icons/16x16/logo.png"))
;(System/exit 0)

(def main-window
  (seesaw.core/frame
   :title    "gpckwlf"
   :icon     (clojure.java.io/resource "gpckwlf/icons/16x16/logo.png")
   :content  card-panel
   :on-close :exit))

;(seesaw.core/show-options main-window)

(defn launch
  "Launches the main window"
  ([]
     (-> main-window
         seesaw.core/pack!
         seesaw.core/show!)))
