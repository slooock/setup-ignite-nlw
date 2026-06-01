(defproject setup-ignite-nlw "1.0.0"
  :description "Clojure project with intentional vulnerabilities for SAST and SCA scanning tests"
  :url "https://github.com/slooock/setup-ignite-nlw"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 ;; Log4j 2.14.1: Log4Shell (CVE-2021-44228)
                 [org.apache.logging.log4j/log4j-core "2.14.1"]
                 ;; Google Guava 24.1-jre: Uncontrolled Resource Consumption (CVE-2018-10237)
                 [com.google.guava/guava "24.1-jre"]
                 ;; Jackson Databind 2.9.8: Deserialization RCE (CVE-2019-12384)
                 [com.fasterxml.jackson.core/jackson-databind "2.9.8"]
                 ;; Cheshire 5.8.0: Old JSON parsing library version with dependencies having known vulnerabilities
                 [cheshire "5.8.0"]]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}}
  :main ^:skip-aot vulnerable-app.core
  :target-path "target/%s")
