(ns claw.config
  "Global Claw app config.

Note that configured references to other namespaces must be quoted so
that the namespace don't have to be already loaded when config.clj loads.

You can set overrides in your ~/.lein/profiles.clj for development mode.

You can override any config values with environment variables. The
environment variable should be named as THE_NAMESPACE__FOO, where
THE_NAMESPACE corresponds to a Clojure namespace called the.namespace,
and FOO corresponds to the config key ::foo in that namespace. For
example, to override the default nREPL port, you could set the
environment variable CLAW_PLUGINS_NREPL__PORT=7898. (See the docstring
on env-munge-name for a longer explanation of how to map config keys
to env variables.)

TODO: Add docstring support for config items. Config keys are
currently keywords, which do not accept metadata. Perhaps there can be
convenience functions for adding default config values to *config* and
maintaining a parallel data structure that provides documentation for
each item.

"
  (:refer-clojure :exclude [get])
  (:require [cheshire.core :as json]
            [onelog.core :as log]
            )
  (:use [environ.core]))


;; The current live configuration, without any env variable derived
;; values.  Note that this means you CANNOT inspect this directly. Use
;; (get) instead, so that env vars are taken into account.
;;
;; TODO: Demunge and merge env vars into this at startup, so this CAN be inspected directlya
;;
(defonce ^:dynamic *config* (atom {}))

(defn add!
  "Adds the given data, typically a map of configuration values, to
  the current live *config*.

  Keys should typically be ::keywords -- i.e. double-colon
  auto-namespaced keywords. In this way, different plugins (and other
  code) can use the same names for their configuration values. For
  example, the nREPL and webserver plugins both have a value
  called ::port, which is auto-resolved to the appropriate namespaces.
"
  [config-data]
     (swap! *config* merge config-data))

(defn- env-munge-name
  "Takes a configuration keyword and munges it into an env variable name
  keyword suitable for use with Environ according to the following rules:

    1) All periods are converted to single dashes.
    2) All slashes are converted to double dashes.
    3) Colons are removed.

 For example, :claw.config/foo becomes :claw-config--foo.

Note that environ (the library) auto-uppercases names of env vars and
translates dashes to underscores, so your overriding var in the actual
environment will have to be named CLAW_CONFIG__FOO in this case.

"
  [config-key]
  (keyword (-> 
            (clojure.string/replace (str config-key) "." "-")
            (clojure.string/replace ":" "")
            (clojure.string/replace "/" "--"))))


(defn get
  "Returns the current configuration value for the given key. Key should generally be a lowercase symbol.

*config* holds the runtime configuration in an atom which contains a
maps. Keys should typically be double-colon namespace qualified ::keywords.

Environment variables can be used to override the internal default
config maps for runtime adjustment of all config values. (Env
variables are better than external config files because you can deploy
exactly the same codebase or jar in production, development, testing,
etc. and drive its behavior in the startup scripts, rather than
managing a zoo of overlapping config files in version control.)

The env variable naming syntax is SOME_NAMESPACE__KEY_NAME. That is, all
letters are uppercased, all dashes and periods are translated to
underscores, and the namespace and config key are seperated by a double underscore.

For example, to set the nREPL port to 12345, which is defined in
the :port config key within the :claw.plugin.nrepl configspace, you
can start Claw with

     $ CLAW_PLUGINS_NREPL__PORT=12345 lein run

Config tries to parse the env variable value as JSON and returns that
to the Clojure code asking about that config key. If JSON parsing
fails, a raw string is returned.

Development config values can also be set in `~/.lein/profiles.clj`
and `.lein-env` in the project directory (see
https://github.com/weavejester/environ for examples.)

If none of the above locations contains a value, the default config
map for the current operating mode is consulted.

If all of the above fail, the default provided is used.

TODO: Facility for preloading a config file
"
  ([config-key] (get config-key nil))
  ([config-key default]
     ;; First try the env var.
     (or (if-let [val (env (env-munge-name config-key))]
               (try (json/parse-string val)
                    (catch com.fasterxml.jackson.core.JsonParseException e val)))
         ;; Nothing in the env, so try *config*
         (let [config @*config*]
           (if (contains? config config-key)
             (config-key config)
             ;; Nothing there either, so log an error and return the given default.
             ;; TODO: log the calling line number
             (do
               (log/error (str " * Error: no value found for requested config value " config-key ))
               default))))))

(defn get-unquoted
  "Uses config/get to get a sequence of symbolic name and unquotes them,
  returning the sequence of concretized vars that results. It tries to
  load any namespaces referenced.

TODO: Throw better errors than the default here, to catch typos in the
config. Give users a friendly error message suggesting that they check
their config for typos, and make sure that all required files have been
loaded.

"
  ([config-key]
     (map (fn [sym]
            (require (symbol (namespace sym)))
            (var-get (find-var sym))) (claw.config/get config-key))))

(defn dev-mode?
  "Convenience method; returns true if in dev mode."
  []
  (get ::mode :claw.config))


(def default-claw-configuration
  "This is the base configuration for Claw itself."
  {
   ::mode "dev" ;; "dev" or "production"
   
   ::app-human-name "<Unnamed Claw App>" ;; Human-readable name of the application

   ;; Maven-*-name are used for auto-finding the app's version number (n.b. not Claw's version number, the app using Claw's version number.)
   ;; These are auto-set in the app-level config files generated by claw-template.
   ::maven-artifact-name nil
   ::maven-group-name nil

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
   ::internal-plugins ['claw.plugins.logging/logging-plugin ;; logging-plugin should always be loaded first, so we can get failure logs for other plugins
                       'claw.plugins.nrepl/nrepl-plugin
                       'claw.plugins.webserver/webserver-plugin
                       'claw.plugins.dump-status/dump-status-plugin]
   
   ;;
   ;; App-level user-provided plugins should be added to ::plugins.
   ;;
   ::plugins '[] 

   ::logfile "logs/claw.log"
   ::loglevel "info"
   })

(add! default-claw-configuration)
