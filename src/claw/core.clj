(ns claw.core
  (:require [claw.config :as config]
            [clansi.core :as ansi]
            [claw.plugin :as plugins]
            [onelog.core :as log]))



(defn start-all-plugins!
  "Starts all plugins in configuration order. (Intended for interactive development use at a REPL, and from (-main).)"
  []
  (plugins/start-plugins! (config/get :claw.config/internal-plugins))
  (plugins/start-plugins! (config/get :claw.config/plugins)))

(defn -main
  "Main framework entry point."
  [& args]

  (log/start! (config/get :claw.config/logfile) (keyword (config/get :claw.config/loglevel)))
  
  (log/info+ (ansi/style (str "Claw v" (System/getProperty "claw.version") " starting up...") :white :bright) "\n"
             "Claw is copyright (C) 2012 Paul Legato. Distributed under the Eclipse Public License.\n")

  (log/debug+ (ansi/style "Debug log enabled" :bright :magenta)) ;; Won't do anything if it's not
  
  (start-all-plugins!)

  (print "\n")
  (log/info+ "Startup complete!\n")
  (println (ansi/style (str "See " (config/get :claw.config/logfile) " for further messages.") :yellow :bright)))


