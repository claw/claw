(ns claw.core
  (:require [claw.config :as config]
            [clansi.core :as ansi]
            [claw.plugin]
            [onelog.core :as log]))

(defn- start-plugin!
  "Loads the given plugin's namespace with require, and starts the
  single given namespaced plugin with claw.plugin/start-plugin!."
  [plugin-symbol]
)

(defn- start-plugins!
  "Given a list of namespace-qualified symbols, requires all namespaces and loads the specified plugins.

TODO: add a \"die on exception\" option that halts and exits rather than continuing with startup.
"
  [plugins]
  (dorun (map
          (fn [plugin-symbol] (claw.plugin/start-plugin-by-symbol! plugin-symbol))
          plugins)))

(defn start-all-plugins!
  "Starts all plugins in configuration order. (Intended for interactive development use at a REPL, and from (-main).)"
  []
  (start-plugins! (config/get :claw.config/internal-plugins))
  (start-plugins! (config/get :claw.config/plugins)))

(defn -main
  "Main framework entry point."
  [& args]

  (log/set-default-logger! (config/get :claw.config/logfile) (keyword (config/get :claw.config/loglevel)))
  
  (log/with-console-log
    (log/info (ansi/style (str "Claw v" (System/getProperty "claw.version") " starting up...") :white :bright) "\n"
              "Claw is copyright (C) 2012 Paul Legato. Distributed under the Eclipse Public License.\n"))

  (log/debug (ansi/style "Debug log enabled" :bright :magenta)) ;; Won't do anything if it's not
  
  (start-all-plugins!)

  (print "\n")
  (log/with-console-log
    (log/info
     "Startup complete!\n"))
  (println (ansi/style (str "See " (config/get :claw.config/logfile) " for further messages.") :yellow :bright)))


