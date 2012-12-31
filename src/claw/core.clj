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
logging system being unconfigured to the console.

TODO: Make this less magical."
  [plugin-symbol]
  (println  " * Loading plugin"  (ansi/style plugin-symbol :cyan :bright)  "...")
  (if (find-ns 'claw.plugins.logging)
    (log/info  " * Loading plugin"  (ansi/style plugin-symbol :cyan :bright)  "...")))

(defn- log-plugin-exception!
  "Logs an exception that occurred loading a plugin according to the
  same semantics as log-plugin-load! (cf.)"
  [plugin-symbol exception]
  (println   (ansi/style (str "  -- Error loading plugin " plugin-symbol ": " (.getMessage exception)) :red :bright))
  (if (find-ns 'claw.plugins.logging)
    (log/error   (ansi/style (str "  -- Error loading plugin " plugin-symbol ": " (.getMessage exception)) :red :bright))))

(defn- start-plugin!
  "Loads the given plugin's namespace with require, and starts the
  single given namespaced plugin with claw.plugin/start-plugin!."
  [plugin-symbol]
  (let [nspace (symbol (namespace plugin-symbol))]
    (require nspace)
    (claw.plugin/start-plugin! (var-get (resolve plugin-symbol)))))

(defn- start-plugins!
  "Given a list of namespace-qualified symbols, requires all namespaces and loads the specified plugins.

TODO: add a \"die on exception\" option that halts and exits rather than continuing with startup.
"
  [plugins]
  (dorun (map
          (fn [plugin-symbol]
            (log-plugin-load! plugin-symbol)
            (try
              (start-plugin! plugin-symbol)
              (catch Throwable t
                (log-plugin-exception! plugin-symbol t))))
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
           "Claw is copyright (C) 2012 Paul Legato. Distributed under the Eclipse Public License.\n")
  
  (start-all-plugins!)
  
  (println
   "\nStartup complete!\n"
    (ansi/style (str "See logs/ring.log for further messages.") :yellow :bright)))

