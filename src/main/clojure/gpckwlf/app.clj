(ns gpckwlf.app
  "Main application."
  (:require [seesaw bind core]
            [gpckwlf password seesaw str wlf-client])
  (:import [pckwlf.java RespType]))

(declare add-tile delete-tile edit-window login-window logout main-window
         refresh settings-window show-panel show-window site-tile)

(def tile-width 100)
(def tile-height 100)
(def ^:dynamic *tag-length* 32)

(def tiles (atom (sorted-map)))
(def inactivity-timeout (atom 30))

(def client
  (atom nil))
(def results
  (atom #{}))

(defn get-data
  "Get username/password associated with tag"
  ([tag] (gpckwlf.wlf-client/get-by-tag @results tag)))

(defn tag-text
  ([tag] (gpckwlf.str/pad tag *tag-length*)))

(defn add-tile
  ([tag]
     (swap! tiles assoc tag (site-tile tag))))

(defn delete-tile
  ([tag]
     (let [response (gpckwlf.wlf-client/remove-entry @client tag)]
       (if (gpckwlf.wlf-client/success? response)
         (refresh)
         (seesaw.core/alert "Failed to delete entry.")))))

(defn copy-password
  ([tag]
     (gpckwlf.password/text->clipboard (:password (gpckwlf.wlf-client/get-by-tag
                                                   @results tag)))))

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
        :success-fn (fn [e] (let [tag (-> form
                                         (seesaw.core/select [:#tag])
                                         seesaw.core/text)
                                 username (-> form
                                              (seesaw.core/select [:#username])
                                              seesaw.core/text)
                                 password (-> form
                                              (seesaw.core/select [:#password])
                                              seesaw.core/text)
                                 response (gpckwlf.wlf-client/add-entry
                                           @client tag username password)]
                             (if (gpckwlf.wlf-client/success? response)
                               (refresh)
                               (seesaw.core/alert "Failed to add entry"))))))))

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
        :items ["Username"
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
     (let [edit-panel (edit-panel tag)]
       (seesaw.core/dialog
        :resource ::edit-window
        :option-type :ok-cancel
        :type :question
        :content edit-panel
        :resizable? false
        :success-fn (fn [e] (let [username (-> edit-panel
                                              (seesaw.core/select
                                               [:#username])
                                              seesaw.core/text)
                                 password (-> edit-panel
                                              (seesaw.core/select
                                               [:#password])
                                              seesaw.core/text)
                                 response (gpckwlf.wlf-client/add-entry
                                           @client tag username password)]
                             (if (gpckwlf.wlf-client/success? response)
                               (refresh)
                               (seesaw.core/alert "Failed to edit entry"))))))))

(defn show-panel
  "Panel to show site's username and password."
  ([tag]
     (let [{:keys [username password]} (get-data tag)
           pass-field (seesaw.core/password :text password
                                            :echo-char \*
                                            :editable? false
                                            :id :password)]
       (seesaw.core/grid-panel
        :columns 3
        :items ["Username"
                username
                "",

                "Password"
                pass-field
                (seesaw.core/button
                 :resource ::show
                 :margin 0
                 :listen [:action (fn [e] (show-hide-password pass-field))])]))))

(defn show-window
  ([tag]
     (seesaw.core/dialog
      :resource ::show-window
      :resizable? false
      :content (show-panel tag))))

(defn add-popup
  ([e]
     (gpckwlf.seesaw/display-frame (add-window))))

(def add-button
  (seesaw.core/button :resource ::add
                      :margin 0
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
      :items [(seesaw.core/border-panel :west (seesaw.core/label
                                               :text (tag-text tag)
                                               :resource ::tag)
                                        :east (delete-button tag))
              (site-tile-buttons tag)])))



(defn make-tiles
  "Makes a map of tag->tile from response map."
  ([results]
     (let [tags (gpckwlf.wlf-client/get-tags results)]
       (into (sorted-map)
             (map (fn [tag] [tag (site-tile tag)])
                  tags)))))

(defn refresh-tiles
  "Refreshes tiles"
  ([]
     (reset! tiles
             (make-tiles @results))))

(defn refresh
  "Refreshes passwords and tiles"
  ([] (when-let [re (gpckwlf.wlf-client/get-all @client)]
        (reset! results re)
        (refresh-tiles))))

(comment
  (swap! tiles assoc
         "My GMail" (site-tile "My GMail")
         "Work email" (site-tile "Work email")
         "Twitter" (site-tile "Twitter")
         "Facebook" (site-tile "Facebook")
         "Github" (site-tile "Github")))

(def login-username
  (seesaw.core/text :id :username
                    :columns 10))

(def login-password
  (seesaw.core/password :echo-char \*
                        :id :password
                        :columns 10))

(defn login-action
  ([e]
     (reset! client (gpckwlf.wlf-client/make-client))
     (let [success? (gpckwlf.wlf-client/login-account @client
                                                      (seesaw.core/text
                                                       login-username)
                                                      (seesaw.core/text
                                                       login-password))]
       (if success?
         (do (seesaw.core/show! main-window)
             (seesaw.core/hide! login-window)
             (refresh))
         (do (reset! client nil)
             (seesaw.core/alert "Failure to log in"))))))

(def login-button
  (seesaw.core/button :resource ::login
                      :listen [:action login-action]))

(def exit-button
  (seesaw.core/button :resource ::exit
                      :listen [:action (fn [e] (System/exit 0))]))

(def login-panel
  (seesaw.core/grid-panel
   :columns 2
   :items ["Username"   login-username
           "Password"   login-password
           login-button exit-button]))

(def login-window
  (seesaw.core/dialog
   :resource ::login-window
   :content login-panel
   :resizable? false
   :options [login-button
             exit-button]))

(def settings-panel
  (seesaw.core/vertical-panel
   :items ["Inactivity Timeout (in minutes)"
           (seesaw.core/text :text @inactivity-timeout),

           "Old Password"
           (seesaw.core/password :echo-char \*
                                 :id :old-password)

           "New Password"
           (seesaw.core/password :echo-char \*
                                 :id :new-password)
           "Confirm New Password"
           (seesaw.core/password :echo-char \*
                                 :id :new-password-confirm)]))

(def settings-window
  (seesaw.core/dialog
   :option-type :ok-cancel
   :resizable? false
   :content settings-panel))

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
                      :listen [:action (fn [e] (refresh))]))

(def settings-button
  (seesaw.core/button :resource ::settings
                      :margin 0
                      :listen [:action (fn [e]
                                         (gpckwlf.seesaw/display-frame
                                          settings-window))]))

(def logout-button
  (seesaw.core/button :resource ::logout
                      :margin 0
                      :listen [:action (fn [e] (logout))]))

(def control-panel
  (seesaw.core/horizontal-panel
;   :align :right
   :items [refresh-button settings-button logout-button]))



(def main-panel
  (seesaw.core/border-panel
   :north control-panel
   :center site-tiles-scroll))

(defn logout
  ([]
     (reset! client nil)
     (reset! results nil)
     (seesaw.core/hide! main-window)
     (seesaw.core/show! login-window)))


(seesaw.bind/bind tiles
                  (seesaw.bind/transform #(-> %
                                              vals
                                              (concat [add-button])))
                  (seesaw.bind/property site-tiles :items))

(def main-window
  (seesaw.core/frame
   :title    "gpckwlf"
   :icon     (clojure.java.io/resource "gpckwlf/icons/16x16/logo.png")
   :content  main-panel

   :width  600
   :height 400

   :minimum-size [300 :by 150]

   :on-close :exit))

(defn launch
  "Launches the main window"
  ([]
     (gpckwlf.seesaw/display-frame login-window)))
