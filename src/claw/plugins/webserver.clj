(ns claw.plugins.webserver
  "Starts a webserver with Compojure routing."
  (:use
   compojure.core
   ring.middleware.http-basic-auth)
  
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]

   [clojure.tools.logging :as log]
   [clansi.core :as ansi]


   [compojure.route :as route]
   [compojure.handler :as handler]
   [compojure.response :as response]

   [hiccup.core]
   [hiccup.middleware]
   
   [ring.adapter.jetty :as jetty]
   ))


(config/add! {
              ::port "3000"
              ::global-middleware []
              ::middleware [] ;; Allows modes to add additional mode-specific middleware without overwriting the globals.
              })

(if (config/dev-mode?)
  (config/add! {:global-middleware ['ring.middleware.logger/wrap-with-logger
                                    'ring.middleware.jsonp/wrap-json-with-padding
                                    ]}))


(def default-routes
  "Route to the default Claw root landing page and 404 page.

TODO: Prettier landing page and 404 page.
"

  (defroutes app
    (GET "/" [] (hiccup.core/html [:h1 "Welcome to " [:a {:href "https://github.com/pjlegato/claw"} "Claw"] "!"]))
    (route/not-found "<h1>Page not found</h1>")))


(defn- default-middleware
  []
  (concat (config/get-unquoted ::global-middleware)
          (config/get-unquoted ::middleware)))

(def handler
  "Ring handler stack for the app."
  (-> ((apply comp (default-middleware)) (handler/site default-routes))
      (hiccup.middleware/wrap-base-url)))


(defn new-web-server
  "Returns a new webserver bound to the given port.

TODO: Log the handler stack at startup.
TODO: Facilities for wrapping different routes with different middleware.
"
  [port]
  (jetty/run-jetty #'handler {:port port :join? false}))


(defonce webserver (atom nil))

(defn start-server!
  "Starts a new webserver on the default port and saves it in the 'webserver' atom, unless one is already running.

TODO: Expand to allow secondary servers on other ports?
TODO: Option to automatically try different ports if this port is in use
"
  []
  (if-let [server @webserver]
    (if (.isRunning server)
      (log/warn (ansi/style " - start-server!: New default webserver was requested, but one is already running. Ignoring request." :bright :yellow))
      (.start server))
    (swap! webserver
           (fn [_]
             (let [port (Integer. (config/get ::port "3000"))]
               (println (ansi/style (str "   - Starting webserver on port " port) :bright :green))
               (log/info (ansi/style (str " - Starting webserver on port " port) :bright :green))
               (new-web-server port))))))


(defn stop-server!
  ([] (stop-server! @webserver))
  ([server]
     (if server
       (do
         (log/info (ansi/style " - Shutting down webserver..." :bright :cyan))
         (.stop server)
         (log/info " - Webserver stopped." ))
       (log/warn (ansi/style " - stop-server!: Asked to shut down default webserver, but no server is running." :bright :yellow)))))


(def webserver-plugin (plugin/new-plugin! (constantly true) (fn [_] (start-server!)) (fn [_] (stop-server!))  (constantly true) "webserver"))
