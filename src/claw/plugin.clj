(ns claw.plugin)

;; TODO: change state management to a proper finite state machine.
;; For now, there are the following states:
;;
;;  - :shutdown - Pre-function has not been run yet, or plugin has been run, stopped, and post-function has been run to reset to original state.
;;  - :ready - pre-function has been run, but not started yet.
;;  - :running - start-function has been run; the plugin is fully operational.
;;  - :stopped - stop-function has been run, the plugin is not operational,
;;                but post-function has not been run. The plugin may be restarted
;;                without re-running pre-function.
;;
;; TODO: Maybe keep track of what protocols each plugin requires and
;; provides... automatic dependency resolution? Or is this just
;; reinventing the Maven / Leiningen wheel?
;;
;; TODO: Support for plugins that can be started more than once,
;; e.g. a Postgres plugin that allows simultaneous connection to
;; multiple databases.
;;

(defprotocol Plugin
  "Methods to manage the lifecycle of a plugin."
  (state [plugin] "Returns the plugin's current state.")
  (to-state! [plugin] "Sets the given state.")

  (start! [plugin] "Starts the plugin, running any necessary pre-function exactly once (no matter how many times the plugin is started.) Should update state as appropriate.")
  (stop! [plugin] "Stops the plugin, running any necessary post-function exactly once (no matter how many times the plugin is stopped.) Should update state as appropriate.")
  (shutdown! [plugin] "Returns to :shutdown state, stopping the plugin and running any post-function (once) if necessary. Should update state as appropriate.")
  )

(defonce plugins (agent []))

(defrecord InternalPlugin [name pre-function start-function stop-function post-function state]
  Plugin
  (state [plugin] state)
  (to-state!)
  (start! [plugin]
     (pre-function))
  (stop! [plugin] "Stops the plugin, running any necessary post-function exactly once (no matter how many times the plugin is stopped.)"))


