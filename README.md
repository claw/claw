# Claw: Clojure All-in-one Webstack

## Work-in-progress. Please get in touch if you'd like to help!

Claw is a full stack, monolithic web framework for Clojure. Out of the box, it provides everything you need to build a web application, from durable storage to JavaScript.

Sometimes, you want to assemble a custom stack from components. Other times, you just want to get something up and running with reasonable defaults. Claw is for those times.


## Usage


## Features

Currently implemented:

* Noir, Hiccup, Enlive
* Logging
* nREPL
* Generic configuration framework with sane defaults provided; everything works out of the box with zero configuration (but you can override all settings if you like)
* Basic plugin lifecycle management framework; everything is a modular
  plugin
  
## TODO:
* Automated tests
* Change Noir to libnoir
* Korma integration
* ClojureQL integration
* Database migrations
* Default error messages
* Better default welcome page
* Auto-color all warnings yellow and all errors red in logs
* Easy-add HTTP Basic auth
* SSL
* Graceful shutdown on SIGTERM
* Authentication with Friend
* Admin console
* OAuth
* Easy ClojureScript reactive programming (e.g. along the lines of
  http://dev.clojure.org/display/design/Reactive+Programming and https://groups.google.com/forum/#!topic/clojure-dev/LzVu4dIvOrg)
* Lots of other stuff

## License

Copyright © 2012 Paul Legato. All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
