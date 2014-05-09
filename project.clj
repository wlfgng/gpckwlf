(defproject gpckwlf "0.1.0"
  :description "Graphical frontend to pckwlf."
  :url "https://github.com/wlfgng/gpckwlf"
  :license {:name "The Unlicense"
            :url "http://unlicense.org/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.4"]]
  :java-source-paths ["src/util/java"]
  :jar-name "gpckwlf.jar"
  :uberjar-name "gpckwlf-standalone.jar"
  :main gpckwlf.core)
