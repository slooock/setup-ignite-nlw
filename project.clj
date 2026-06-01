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
                 [cheshire "5.8.0"]
                 ;; Apache Commons Collections 3.2.1: Deserialization RCE (CVE-2015-7501)
                 [commons-collections/commons-collections "3.2.1"]
                 ;; Fastjson 1.2.24: Remote Code Execution (CVE-2017-18349)
                 [com.alibaba/fastjson "1.2.24"]
                 ;; XStream 1.4.15: Remote Code Execution (CVE-2021-21351)
                 [com.thoughtworks.xstream/xstream "1.4.15"]
                 ;; SnakeYAML 1.30: Out-of-bounds Write / DoS (CVE-2022-25857)
                 [org.yaml/snakeyaml "1.30"]
                 ;; Dom4j 1.6.1: XML External Entity / RCE (CVE-2018-1000632)
                 [dom4j/dom4j "1.6.1"]
                 ;; PostgreSQL JDBC Driver 42.3.1: Remote Code Execution (CVE-2022-21724)
                 [org.postgresql/postgresql "42.3.1"]
                 ;; H2 Database 1.4.199: Remote Code Execution (CVE-2021-23463, CVE-2022-23221)
                 [com.h2database/h2 "1.4.199"]]
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}}
  :main ^:skip-aot vulnerable-app.core
  :target-path "target/%s")
