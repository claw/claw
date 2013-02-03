(defproject org.claw/claw "0.4.0"
  :description "Claw is a full stack, monolithic web framework for Clojure."
  :url "http://github.com/claw/claw"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]

                 ;; TODO: Break out plugins into subprojects, so that
                 ;; you don't need to have dependencies around that
                 ;; are only needed for plugins you aren't actually
                 ;; using.
                 
                 ;; Ring extensions
                 [ring.middleware.logger "0.4.0"]
                 [org.clojars.pepijndevos/ring-http-basic-auth "0.1.1"]
                 [ring.middleware.jsonp "0.1.1"] ;; auto-generate JSONP from any JSON response
                 [com.cemerick/friend "0.1.2"] ;; Authentication
                 
                 ;; Database
                 [korma "0.3.0-beta9"]
                 [postgresql "9.1-901.jdbc4"]

                 ;; Web
                 [ring "1.1.6"]
                 [compojure "1.1.3"]  ;; Routes
                 [ring-serve "0.1.2"] ;; Easily start and stop the webserver
                 [hiccup "1.0.2"]     ;; Render HTML from Clojure


                 ;; General purpose utilities
                 [onelog "0.4.2"]
                 [clj-time "0.4.2"]
                 [org.clojure/core.memoize "0.5.1"] ;; Advanced memoization
                 [environ "0.3.0"] ;; read config from environment
                 [org.apache.commons/commons-compress "1.4"] ;; Read/write compressed files
                 [fs "1.0.0"] ;; Filesystem utils
                 [slingshot "0.10.2"] ;; Enhanced throw/catch
                 [org.clojure/tools.nrepl "0.2.0-beta8"] ;; Network REPL
                 [trptcolin/versioneer "0.1.0"] ;; Reads versions from Maven / system properties                 
                 [cheshire "5.0.0"] ;; JSON
                 ]
  :main claw.core
  )
