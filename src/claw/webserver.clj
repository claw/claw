(ns claw.webserver
  (:use

   ;; TODO: for noir-async:
   ;; aleph.http 
   ;; lamina.core
   
   noir.core
   ring.middleware.http-basic-auth)
  
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]

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

  (if (config/get :show-web-stacktraces)
    ;; Sends helpful HTML stacktraces on exceptions, rather than
    ;; crashing with a silent 500 response.
    (nr-server/add-middleware stacktrace/wrap-stacktrace-web))
  
  ;; Allow users to request JSONP from any JSON-generating response
  (nr-server/add-middleware jsonp/wrap-json-with-padding)

  (if (config/get :auto-reload)
    (nr-server/add-middleware ring.middleware.reload/wrap-reload))

  (nr-server/load-views "src/claw/web/views/")
  (defpage "/" [] (noir.response/redirect "/names")))




(defn new-noir-server
  "Creates a new webserver bound to the given port."
  [port]
  ;; for noir-async:
  ;; (start-http-server
  ;;  (wrap-ring-handler noir-handler)
  ;;  {:port port :websocket true}))
  
  ;; For regular Noir:
  (nr-server/start port))


(defonce webserver (atom nil))

(defn start-server!
  "Starts a new webserver on the default port and saves it in the 'webserver' atom, unless one is already running.

TODO: Expand to allow secondary servers on other ports?"
  []
  (if-not @webserver
    (swap! webserver
           (fn [_]
               (let [port (Integer. (config/get :web-port "3000"))]
                 (log/info (ansi/style (str " - Starting webserver on port " port) :bright :cyan))
                 (new-noir-server port))))
    (log/warn (ansi/style " - start-server!: New default webserver was requested, but one is already running. Ignoring request." :bright :yellow))))


(defn stop-server! [server]
  (if-let [server @webserver]
    (do
      (log/info (ansi/style " - Shutting down webserver..." :bright :cyan))
      (nr-server/stop server))
    (log/warn (ansi/style " - stop-server!: Asked to shut down default webserver, but no server is running." :bright :yellow))))


(def webserver-plugin (plugin/new-plugin! (fn [_] (pre-noir-functions)) (fn [_] (start-server!)) (fn [_] (stop-server!))  (constantly true) "webserver"))
