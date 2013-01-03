(ns claw.plugins.dump-status
  "Dumps the current config and plugins state to the logfile."
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]
   [clansi.core :as ansi]
   [onelog.core :as log]))

(def dump-status-plugin (plugin/new-plugin! (constantly :ready)
                                            (fn [_]
                                              (log/info (ansi/style "* Current configuration: " :bright :magenta))
                                              (doall (map #(log/info (str "  " % " : " (config/get %) )) (keys @config/*config*)))
                                              :shutdown) ;; stateless one-off, short circuits back to :shutdown state
                                            (constantly :stopped)
                                            (constantly :shutdown)
                                            "logger"))
