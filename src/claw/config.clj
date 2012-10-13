(ns claw.config
  (:use [environ.core]
        ))

;; Set overrides in your ~/.lein/profiles.clj for development mode.

(def defaults
  { :dev
   {
    :mode "dev"
    :web-port "3000"
    
    :database-host "localhost"
    :database-port "5432"
    
    :database-name "claw-development"
    :database-user "claw-development"
    :database-password ""
    
    }

   :production
   {
    :mode "production"
    :web-port "8000"
    
    :database-host "localhost"
    :database-port "5432"
    
    :database-name "claw-production"   
    :database-user "claw-production"
    :database-password ""
    
    }
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

(defn config
  "Returns the current configuration value for the given key.

If an environment variable is set named key, config returns that value.

Config values can also be set in `~/.lein/profiles.clj` (see https://github.com/weavejester/environ for examples.)

If neither of the above locations contains a value, the default config map for the current operating mode is consulted.
"
  [key] (or (env key) ((mode defaults) key)))