(defproject gpckwlf "0.1.0-SNAPSHOT"
  :description "Graphical frontend to pckwlf."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.4"]]
  :java-source-paths ["src/util/java"]
  :jar-name "gpckwlf.jar"
  :uberjar-name "gpckwlf-standalone.jar"
  :main gpckwlf.core)
