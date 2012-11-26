(ns claw.nrepl
  "Claw plugin which starts a nREPL."
  (:require
   [claw.config :as config]
   [claw.plugin :as plugin]
   [clojure.tools.nrepl.server :as nrepl]   
   [clojure.tools.logging :as log]
   [clansi.core :as ansi]))

(defonce nrepls (atom {}))

(defn start-nrepl-server!
  "Starts a nREPL server on the given port, on the configured port, or on port 7888 if none is configured.
   Does nothing if there is already an nREPL running on that port."
  ([] (start-nrepl-server! nil))
  ([port]
     (let [port (Integer. (or port (config/get :nrepl-port) "7888"))]
       (if (contains? @nrepls port)
         (do
           (log/warn (ansi/style (str "nREPL: Tried to start an nREPL on port " port ", but there's already an nREPL running there. Refusing to run.")))
           nil)
         (do
           (log/info (str "* Starting nREPL server on port " port "."))
           (swap! nrepls assoc port (nrepl/start-server :port port)))))))

(defn stop-nrepl-server!
  "Stops the nREPL server running on the given port, the configured port, or 7888.

TODO: This seems to be broken; the nrepl/stop-server call doesn't do anything."
  [port]
  (let [port (Integer. (or port (config/get :nrepl-port) "7888"))]
    (if-let [nrepl (get @nrepls port)]
      (do
        (log/info (str " * Shutting down nREPL server on port " port "."))
        (nrepl/stop-server nrepl)
        (swap! nrepls dissoc port))
      (do
        (log/warn (ansi/style (str "nREPL: asked to stop an nREPL on port " port ", but there does not seem to be any nREPL running there.")))
        nil))))

(def nrepl-plugin (plugin/new-plugin! (constantly true) start-nrepl-server! stop-nrepl-server! (constantly true) "nrepl"))
