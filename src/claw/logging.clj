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
  
  (log/info (ansi/style (str " ****** " (config/get :app-name)
                             " v " (versioneer/get-version  (config/get :maven-group-name)
                                                            (config/get :maven-artifact-name)
                                                            "<set :maven-artifact-name and :maven-group-name configs to autodetect version number>")
                             " (Claw v" (System/getProperty "claw.version") ") starting up...")
                        :bright :white))
  ;;(log/log-capture! (str *ns*)) ;; capture stdout / stderr to log to current namespace
  )
(def logging-plugin (plugin/new-plugin! (constantly true) (fn [_] (start-logger!)) (constantly true)  (constantly true) "logger"))
