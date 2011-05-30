(ns innovation.server
  (:use [clojure.contrib.server-socket :only (create-server)]
        [clojure.contrib.duck-streams :only (writer reader read-lines)]))

(def players (ref {}))

(defn respond [out text]
  (do
    (.println out text)
    (.flush out)))

(defn ERROR [out text]
  (respond out (format "%s,%s" "ERROR" text)))

(defn LOGIN [line in out]
  (let [tokens (.split line ",")
        username (nth tokens 1)
        password (nth tokens 2)]
    (respond out (format "username:%s password:%s" username password))))

(defn ECHO [line in out]
  (let [tokens (.split line ",")
        message (nth tokens 1)]
    (respond out message)))

(create-server
  9011
  (fn [in-stream out-stream]
    (with-open [in (reader in-stream)
                out (writer out-stream)]
      (doseq [line (read-lines in)]
        (try
          (cond (.startsWith line "ECHO") (ECHO line in out)
                (.startsWith line "LOGIN") (LOGIN line in out))
          (catch Exception e (ERROR out (.toString e))))))))
