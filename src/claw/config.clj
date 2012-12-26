(ns claw.config
  "Global Claw app config.

Note that configured references to other namespaces must be quoted so
that they don't have to be already loaded when config.clj loads.
"
  (:refer-clojure :exclude [get])
  (:require [cheshire.core :as json])
  (:use [environ.core]))

;; Set overrides in your ~/.lein/profiles.clj for development mode.

(def default-configuration
  "This is the base configuration from which mode-specific configuration maps are derived.
You may be more interested in mode-defaults, where many of these
settings are overridden for e.g. dev mode.
"
  {
   ;;;;;;;;;;;;;;;;;;;;;;;
   ;;
   ;; General settings
   ;;
   ;;;;;;;;;;;;;;;;;;;;;;;
   :app-human-name "<Unnamed Claw App>" ;; Human-readable name of the application

   ;; Maven-*-name are used for auto-finding the app's version number (n.b. not Claw's version number, the app using Claw's version number.)
   ;; These are auto-set in the app-level config files generated by claw-template.
   :maven-artifact-name nil
   :maven-group-name nil

   :nrepl-port "9999"

   ;;;;;;;;;;;;;;;;;;;;;;;
   ;;
   ;; Database configuration
   ;;
   ;;;;;;;;;;;;;;;;;;;;;;;
   :database-host "localhost"
   :database-port "5432"

   ;;;;;;;;;;;;;;;;;;;;;;;
   ;;
   ;; Web configuration
   ;;
   ;;;;;;;;;;;;;;;;;;;;;;;
   :web-port "3000"
   :global-middleware ['ring.middleware.logger/wrap-with-logger
                       'ring.middleware.jsonp/wrap-json-with-padding
                       ]
   :middleware [] ;; Allows modes to add additional mode-specific middleware without overwriting the globals.

   ;;;;;;;;;;;;;;;;;;;;;;;
   ;;
   ;; Plugins to be started automatically at boot time
   ;;
   ;;;;;;;;;;;;;;;;;;;;;;;

   ;;
   ;; Plugins used internally by Claw itself. The default plugin set
   ;; gives you a reasonable full-stack web framework.  If you want to
   ;; make a leaner app, just override this in your config, and only
   ;; include the plugins you need.
   ;;
   :claw-internal-plugins ['claw.logging/logging-plugin ;; logging-plugin should always be loaded first, so we can get failure logs for other plugins
                           'claw.nrepl/nrepl-plugin
                           'claw.plugins.compojure/compojure-plugin]
   
   ;;
   ;; App-level user-provided plugins should be added to :claw-plugins.
   ;;
   :claw-plugins '[] 

   }
  )

(def mode-defaults
  "Application default configurations by mode."
  {

   :dev (merge default-configuration
               {
                :mode "dev"
                
                :database-name "claw-development"
                :database-user "claw-development"
                :database-password ""
                
                :middleware ['ring.middleware.stacktrace/wrap-stacktrace-web
                             'ring.middleware.reload/wrap-reload]
                
                })
   
   :production (merge default-configuration
                      {
                       :mode "production"
                       :web-port "8080"
                       
                       :database-name "claw-production"   
                       :database-user "claw-production"
                       :database-password ""
                       
                       })
   })


(def mode
  "The application's run mode, which selects the default settings used.

Valid values are \"production\" or \"dev\".
Any unknown value is ignored and puts the app into dev mode.
"
  (case (env :mode)
        "production" :production
        "dev" :dev
        :dev)) ;; TODO: log an error on unknown values

(defn get
  "Returns the current configuration value for the given key. Key should generally be a lowercase symbol.

If an environment variable is set with the same name as the key,
capitalized, config tries to parse it as JSON and returns that. If
JSON parsing fails, a raw string is returned.

Development config values can also be set in `~/.lein/profiles.clj`
and `.lein-env` in the project directory (see
https://github.com/weavejester/environ for examples.)

If neither of the above locations contains a value, the default config map for the current operating mode is consulted.

If all of the above fail, the default provided is used.
"
  ([key default] (or (get key) default))
  ([key]
     (or (if-let [val (env key)]
           (try (json/parse-string val)
                (catch com.fasterxml.jackson.core.JsonParseException e val)))
         ((mode mode-defaults) key))))

(defn get-unquoted
  "Uses config/get to get a sequence of symbolic name and unquotes them,
  returning the sequence of concretized vars that results. It tries to
  load any namespaces referenced.

TODO: Throw better errors than the default here, to catch typos in the
config. Give users a friendly error message suggesting that they check
their config for typos, and make sure that all required files have been
loaded.

"
  [key]
  (map (fn [sym]
         (require (symbol (namespace sym)))
         (var-get (find-var sym))) (claw.config/get key)))