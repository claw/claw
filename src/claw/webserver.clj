(ns claw.webserver
  (:use

   ;; TODO: for noir-async:
   ;; aleph.http 
   ;; lamina.core
   
   noir.core
   noir.fetch.remotes
   claw.config
   ring.middleware.http-basic-auth)
  
  (:require
   
   [noir.response :as response]
   [noir.server :as nr-server]

   [clojure.tools.logging :as log]
   [clansi.core :as ansi]
   
   [ring.middleware.logger :as logger]
   [ring.middleware.stacktrace :as stacktrace]
   [ring.middleware.jsonp :as jsonp]
   ))



(defn- pre-noir-functions  []
  "Tasks that should be performed exactly once at app boot time,
before Noir starts."


  ;; Logs all requests:
  (nr-server/add-middleware logger/wrap-with-logger)

  (logger/set-default-root-logger!) ;; Set the entire app to log to the same Ring middleware log
  
  (log/info (ansi/style (str " ****** " (config :app-name)
                             " v " (trptcolin.versioneer.core/get-version  (config :maven-group-name)
                                                                           (config :maven-artifact-name)
                                                                           "<set :maven-artifact-name and :maven-group-name configs to autodetect version number>")
                             " (Claw v" (System/getProperty "claw.version") ") starting up...")
                        :bright :white))
  ;;(log/log-capture! (str *ns*)) ;; capture stdout / stderr to log to current namespace

  (if (config :show-web-stacktraces)
    ;; Sends helpful HTML stacktraces on exceptions, rather than
    ;; crashing with a silent 500 response.
    (nr-server/add-middleware stacktrace/wrap-stacktrace-web))
  
  ;; Allow users to request JSONP from any JSON-generating response
  (nr-server/add-middleware jsonp/wrap-json-with-padding)

  (if (config :auto-reload)
       (nr-server/add-middleware ring.middleware.reload/wrap-reload)))

(defonce pre-noir-setup (pre-noir-functions))

(nr-server/load-views "src/ablaze/web/views/")
(defpage "/" [] (noir.response/redirect "/names"))


(defn start-noir-server
  ([mode port]
        
        ;; For regular Noir:
        (nr-server/start port)
        
        ;; for noir-async:
        ;; (start-http-server
        ;;  (wrap-ring-handler noir-handler)
        ;;  {:port port :websocket true}))
        

     (start-noir-server )
     )
  ([]
      (let [mode (keyword (or (config :mode) (or (first mode) :dev)))
            port (Integer. (or (config :web-port) "3000"))
            ]
        
        )))

;; TODO: rm for standalone mode
;(defonce webserver (log/with-logs (str *ns*) (-main)))
;;(defonce webserver (start-noir-server))


(defn stop-server [server]
  (nr-server/stop server))

(defn start-nrepl-server []
  (let [port (Integer. (or (config :nrepl-port) "7888"))]
    (log/info (str "* Starting nREPL server on port " port "."))
    (nrepl/start-server :port port)))

(defn -main [& mode]
  (defonce nrepl-server (start-nrepl-server))
  (defonce webserver (start-noir-server mode)))

(defn manual-start-server
  [] (def webserver (start-server :dev)))



(defn- post-noir-functions []
  "Tasks that should be performed exactly once at server startup,
after Noir starts."
  )

(defonce post-noir-setup (post-noir-functions))
