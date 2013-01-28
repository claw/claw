(ns claw.plugins.logging
  "Plugin to initialize the logging system."
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]
   [onelog.core :as log]
   [clansi.core :as ansi]
   [trptcolin.versioneer.core :as versioneer]
   [ring.middleware.logger :as logger]))

(defn- log-uncaught-exceptions!
  "Causes the logger to catch all uncaught exceptions and log them to
  the default logfile."
  []
  (Thread/setDefaultUncaughtExceptionHandler
   (reify Thread$UncaughtExceptionHandler
     (uncaughtException [this thread throwable]
       (log/error (ansi/style "Uncaught Exception!" :bright :red))
       (log/error throwable)))))

(defn start-logger!
  "Sets up default app-wide logging."
  []
  (log/start! (config/get :claw.config/logfile) (keyword (config/get :claw.config/loglevel)))

  (log-uncaught-exceptions!)
  ;;(log/log-capture! (str *ns*)) ;; capture stdout / stderr to log to current namespace

  ;; Note: 'app' here refers to the user-level app using Claw, not to claw itself.
  (let [app-name (config/get :app-human-name)
        app-version (versioneer/get-version  (config/get :maven-group-name)
                                             (config/get :maven-artifact-name)
                                             nil)]
    (if app-version
      (log/info (ansi/style
                 (str " ****** " app-name
                      " v " app-version
                      " (using Claw v" (System/getProperty "claw.version") ") starting up...")
                 :bright :green))
      (do
        (log/info (ansi/style
                   (str " ***** " app-name " (using Claw v" (System/getProperty "claw.version") ") starting up...")
                   :bright :green))
        (log/info (ansi/style " -- Set :app-name, :maven-group-name, and :maven-artifact-name in your config to have your app name and version number autologged here." :yellow))))))

  
(def logging-plugin (plugin/new-plugin! (constantly :ready)
                                        (fn [_] (start-logger!) :started)
                                        (constantly :stopped)
                                        (constantly :shutdown)
                                        "logger"))
