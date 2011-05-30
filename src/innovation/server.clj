(ns innovation.server
  (:use [clojure.contrib.server-socket :only (create-server)]
        [clojure.contrib.duck-streams :only (reader read-lines)]))

(create-server
  9011
  (fn [in-stream out-stream]
    (with-open [in (reader in-stream)]
      (doseq [line (read-lines in)] (print line)))))
