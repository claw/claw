(ns claw.core
  (:require [claw.config :as config]
            [clansi.core :as ansi]
            [claw.plugin]
            [clojure.tools.logging :as log]))

(defn- log-plugin-load!
  "Logs an info message noting that the specified plugin has been
loaded, and prints a similar message to the console, to aid debugging
in cases where the logging plugin itself hasn't loaded yet.

 Will not try to log anything unless the claw.logging namespace has
been loaded, to avoid causing Log4J to print warning spam about the
logging system being unconfigured to the console."
  [plugin-symbol]
  (println  " * Loading plugin"  (ansi/style plugin-symbol :cyan :bright)  "...")
  (if (find-ns 'claw.logging)
    (log/info  " * Loading plugin"  (ansi/style plugin-symbol :cyan :bright)  "...")))

(defn- start-plugin!
  "Loads the given plugin's namespace with require, and starts the
  single given namespaced plugin with claw.plugin/start-plugin!."
  [plugin-symbol]
  (let [nspace (symbol (namespace plugin-symbol))]
    (require nspace)
    (claw.plugin/start-plugin! (var-get (resolve plugin-symbol)))))

(defn- start-plugins!
  "Given a list of namespace-qualified symbols, requires all namespaces and loads the specified plugins."
  [plugins]
  (dorun (map
          (fn [plugin-symbol]
            (log-plugin-load! plugin-symbol)
            (start-plugin! plugin-symbol))
          plugins)))

(defn start-all-plugins!
  "Starts all plugins in configuration order. (Intended for interactive development use at a REPL, and from (-main).)"
  []
  (start-plugins! (config/get :claw-internal-plugins))
  (start-plugins! (config/get :claw-plugins)))

(defn -main
  "Main framework entry point."
  [& args]

  (println (ansi/style (str "Claw v" (System/getProperty "claw.version") " starting up...") :white :bright) "\n"
           "Copyright (C) 2012 Paul Legato. Distributed under the Eclipse Public License.\n")
  
  (start-all-plugins!)
  
  (println
   "\nStartup complete!\n"
    (ansi/style (str "See logs/ring.log for further messages.") :yellow :bright)))

