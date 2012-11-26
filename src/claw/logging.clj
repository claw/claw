(ns claw.logging
  "Plugin to initialize the logging system."
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]
   [clojure.tools.logging :as log]
   [clansi.core :as ansi]
   [trptcolin.versioneer.core :as versioneer]
   [ring.middleware.logger :as logger]))

(defn start-logger!
  "Sets up default app-wide logging."
  []
  (logger/set-default-logger!)
  (logger/set-default-root-logger!) ;; Set the entire app to log to the same Ring middleware log

  ;;(log/log-capture! (str *ns*)) ;; capture stdout / stderr to log to current namespace

  ;; Note: 'app' here refers to the user-level app using Claw, not to claw itself.
  (let [app-name (config/get :app-name "<Unknown Claw app; set the :app-name config string to name your app>")
        app-version (versioneer/get-version  (config/get :maven-group-name)
                                             (config/get :maven-artifact-name)
                                             nil)]
    (if (and app-name app-version)
      (log/info (ansi/style
                 (str " ****** " app-name
                      " v " app-version
                      " (using Claw v" (System/getProperty "claw.version") ") starting up...")
                 :bright :green))
      (do
        (log/info (ansi/style
                   (str " ***** Unnamed Claw App (using Claw v" (System/getProperty "claw.version") ") starting up...")
                   :bright :green))
        (log/info (ansi/style " -- Set :app-name, :maven-group-name, and :maven-artifact-name in your config to have your app name and version number autologged here." :yellow))))))

  
(def logging-plugin (plugin/new-plugin! (constantly true) (fn [_] (start-logger!)) (constantly true)  (constantly true) "logger"))
