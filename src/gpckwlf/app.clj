(ns gpckwlf.app
  "Main application."
  (:require [seesaw bind core]
            [gpckwlf password seesaw]))

(declare add-tile delete-tile edit-window logout
         show-panel show-window site-tile)

(def tile-width 100)
(def tile-height 100)

(def tiles (atom (sorted-map)))

(defn add-tile
  ([tag]
     (swap! tiles assoc tag (site-tile tag))))

(defn delete-tile
  ([tag]
     (swap! tiles dissoc tag)))

(defn copy-password
  ([tag]
     (gpckwlf.password/text->clipboard tag)))

(defn show-password
  ([tag]
     (gpckwlf.seesaw/display-frame (show-window tag))))

(defn edit-password
  ([tag]
     (gpckwlf.seesaw/display-frame (edit-window tag))))



(defn add-form
  ([]
     (seesaw.core/grid-panel
      :columns 2
      :items ["Tag" (seesaw.core/text :id :tag)
              "Username" (seesaw.core/text :id :username)
              "Password" (seesaw.core/password :echo-char \*
                                               :id :password)])))

(defn add-window
  ([]
     (let [form (add-form)]
       (seesaw.core/dialog
        :resource ::add-window
        :content form
        :option-type :ok-cancel
        :type :question
        :success-fn (fn [e] (-> form
                               (seesaw.core/select [:#tag])
                               seesaw.core/text
                               add-tile))))))

(defn show-hide-password
  "Show/hide password field."
  ([password]
     (seesaw.core/config! password :echo-char
                          (if (= \* (seesaw.core/config password
                                                        :echo-char))
                            (char 0)
                            \*))))

(defn edit-panel
  "Panel to edit site's tag, username, and password."
  ([tag]
     (let [password (seesaw.core/password :text tag
                                          :echo-char \*
                                          :id :password)]
       (seesaw.core/grid-panel
        :columns 3
        :items ["Tag"
                (seesaw.core/text :text tag
                                  :id :tag)
                "",
                
                "Username"
                (seesaw.core/text :text "Username"
                                  :id :username)
                "",

                "Password"
                password
                (seesaw.core/button
                 :resource ::show
                 :margin 0
                 :listen [:action (fn [e] (show-hide-password password))])]))))

(defn edit-window
  ""
  ([tag]
     (let [edit-panel (edit-panel tag)
           update-tag (fn [new-tag]
                        (delete-tile tag)
                        (add-tile new-tag))]
       (seesaw.core/dialog
        :resource ::edit-window
        :option-type :ok-cancel
        :type :question
        :content edit-panel
        
        :success-fn (fn [e] (-> edit-panel
                               (seesaw.core/config :items)
                               second
                               seesaw.core/text
                               update-tag))))))

(defn show-panel
  "Panel to show site's username and password."
  ([tag]
     (let [password (seesaw.core/password :text tag
                                          :echo-char \*
                                          :editable? false
                                          :id :password)]
       (seesaw.core/grid-panel
        :columns 3
        :items ["Username" tag ""

                "Password" password
                (seesaw.core/button
                 :resource ::show
                 :margin 0
                 :listen [:action (fn [e] (show-hide-password password))])]))))

(defn show-window
  ([tag]
     (println "BUT SOMEHOW THIS IS OK?!?!:" (type (show-panel tag)))
     (seesaw.core/dialog
      :resource ::show-window
      :content (show-panel tag))))

(defn add-popup
  ([e]
     (gpckwlf.seesaw/display-frame (add-window))))

(def add-button
  (seesaw.core/button :resource ::add
                      :listen [:action add-popup]))

(defn delete-button
  ([tag]
     (gpckwlf.seesaw/remove-borders
      (seesaw.core/button
       :resource ::delete
       :listen [:action (fn [e] (delete-tile tag))]))))

(defn site-tile-copy-button
  ""
  ([tag]
     (seesaw.core/button
      :resource ::copy
      :margin 0
      :listen [:action (fn [e] (copy-password tag))])))

(defn site-tile-show-button
  ""
  ([tag]
     (seesaw.core/button
      :resource ::show
      :margin 0
      :listen [:action (fn [e] (show-password tag))])))

(defn site-tile-edit-button
  ""
  ([tag]
     (seesaw.core/button
      :resource ::edit
      :margin 0
      :listen [:action (fn [e] (edit-password tag))])))

(defn site-tile-buttons
  "Returns the horizontal panel containing the buttons for a site tile with
  the given tag."
  ([tag] {:pre (string? tag)}
     (seesaw.core/horizontal-panel
      :items [(site-tile-copy-button tag)
              (site-tile-show-button tag)
              (site-tile-edit-button tag)])))

(defn site-tile
  "Returns a new site tile with the given tag."
  ([tag] {:pre (string? tag)}
     (seesaw.core/vertical-panel
      :items [(seesaw.core/border-panel :west tag
                                        :east (delete-button tag))
              (site-tile-buttons tag)])))

(swap! tiles assoc
       "Foo" (site-tile "Foo")
       "Bar" (site-tile "Bar")
       "Baz" (site-tile "Baz"))

(def login-username
  (seesaw.core/text :id :username
                    :columns 10))

(def login-password
  (seesaw.core/password :echo-char \*
                        :id :password
                        :columns 10))

(def login-button
  (seesaw.core/button :resource ::login))

(def exit-button
  (seesaw.core/button :resource ::exit))

(def login-panel
  (seesaw.core/grid-panel
   :columns 2
   :items ["Username"   login-username
           "Password"   login-password
           login-button exit-button]))

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
  (seesaw.core/button :resource ::refresh
                      ;; :icon
                      ;; (clojure.java.io/resource
                      ;;  "gpckwlf/icons/32x32/refresh.png")
                      :margin 0
                      :listen [:action (fn [e] (println "refreshing"))]))

(def settings-button
  (seesaw.core/button :resource ::settings
                      :margin 0
                      :listen [:action (fn [e] (println "settings!!"))]))


(def logout-button
  (seesaw.core/button :resource ::logout
                      :margin 0
                      :listen [:action (fn [e] (logout))]))

(def settings-panel
  (seesaw.core/horizontal-panel
;   :align :right
   :items [refresh-button settings-button logout-button]))

(def main-panel
  (seesaw.core/border-panel
   :north settings-panel
   :center site-tiles))

(def card-panel
  (seesaw.core/card-panel
   :items [[login-panel :login-panel]
           [main-panel  :main-panel]]))

(defn logout
  ([]
     (seesaw.core/show-card! card-panel
                             :login-panel)))

; this is not how you do it lol
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
     (gpckwlf.seesaw/display-frame main-window)))
