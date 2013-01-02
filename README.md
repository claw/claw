# Claw: Clojure All-in-one Webstack

## Work-in-progress. Please get in touch if you'd like to help!

Claw is a full stack, monolithic web framework for Clojure. Out of the box, it provides everything you need to build a web application, from durable storage to JavaScript.

Sometimes, you want to assemble a custom stack from components. Other times, you just want to get something up and running with reasonable defaults. Claw is for those times.


## Usage


## Features

Currently implemented:

* Compojure, Hiccup, Enlive
* Logging
* nREPL
* Generic configuration framework with sane defaults provided; everything works out of the box with zero configuration (but you can override all settings if you like)
* Basic plugin lifecycle management framework; everything is a modular
  plugin
  
## TODO / Wishlist:
* Automated tests
* Add libnoir
* Korma integration
* ClojureQL integration
* Database migrations
* Default error messages
* Better default welcome page
* Separate webserver plugin into "webserver" and "Compojure" plugins;
  add Moustache plugin
* Auto-color all warnings yellow and all errors red in logs
* Full cycle reactive AJAX auto-form validation, including Clojurescript
  feedback for wrong form fields
* Easy-add HTTP Basic auth
* SSL
* Generate / manage robots.txt
* Generate a sitemap
* Hold a route / middleware interaction graph in memory, allow it to
  be dynamically updated, auto-change / set up everything when it
  is. Plugins can specify where in the graph they'd like to be.
* Graceful, logged shutdown on SIGTERM
* Authentication with Friend, role-based access control primitives
* Canned user login / account management CRUD, skinnable via CSS
* Resource compilation / asset pipelining with https://github.com/edgecase/dieter
* Canned admin console (skinnable via CSS, of course)
* REST: The SQL equivalent of Ringfinger? Something else?
* Invitations, password reset via e-mail
* e-mail validation
* Easy ClojureScript reactive programming (e.g. along the lines of
  http://dev.clojure.org/display/design/Reactive+Programming and
  https://groups.google.com/forum/#!topic/clojure-dev/LzVu4dIvOrg)
* Config in middleware?
  http://brehaut.net/blog/2012/configuration_middleware
* A clean way for plugins to supply config defaults and have them
  merged into the app-wide config automatically
* Robert Hooke based hooks (https://github.com/technomancy/robert-hooke/)
* Lots of other stuff.. 

What do you want to see Claw do?

## License

Copyright © 2012 Paul Legato. All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
