(defproject gpckwlf "0.2.0-SNAPSHOT"
  :description "pckwlf user interface."
  :url "https://github.com/wlfgng/gpckwlf"
  :license {:name "The Unlicense"
            :url "http://unlicense.org/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.4"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"
                      "src/util/java"]
  :test-paths ["src/test/clojure"]
  :jar-name "gpckwlf.jar"
  :uberjar-name "gpckwlf-standalone.jar"
  :main gpckwlf.core)
