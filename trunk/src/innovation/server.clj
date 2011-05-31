(ns innovation.server
  (:import (java.util.concurrent.locks ReentrantLock)
           (java.util UUID))
  (:use (innovation initialize)
        [clojure.contrib.server-socket :only (create-server)]
        [clojure.contrib.duck-streams :only (writer reader read-lines)]))

(def lock (new ReentrantLock))
(def id-ref (atom {}))
(def players-ref (atom {}))
(def games-ref (atom {}))

(defn respond [out text]
  (do
    (.println out text)
    (.flush out)))

;(defn get-players [id]
;  (map :name (filter? #(= (:id %) id) (vals (deref players-ref)))))
;
;(defn remove-id [id]
;  (let [names (get-players id)]
;    (swap! players-ref #(

(defn get-player-id [id]
  (let [name ((deref id-ref) id)]
    ((deref players-ref) name)))

(defn ERROR [out text]
  (respond out (format "%s,%s" "ERROR" text)))

(defn LOGIN [line in out id]
  (let [tokens (.split line ",")
        username (nth tokens 1)
        password (nth tokens 2)]
      (let [players (deref players-ref)
            player (get players username)]
        (if (nil? player)
          (let [new-player (new-player username password in out)]
            (swap! players-ref assoc username new-player)
            (swap! id-ref assoc id username))
          (if (= (:password player) password)
            (do
              (respond out (format "%s %s %s",username,password,(:password player)))
              (swap! players-ref assoc-in [username :in] in)
              (swap! players-ref assoc-in [username :out] out)
              (swap! id-ref assoc id username))
            (ERROR out "Incorrect password provided."))))))

(defn ECHO [line in out]
  (let [tokens (.split line ",")
        message (nth tokens 1)]
    (respond out message)))

(defn NEWGAME [line player]
  (let [out (:out player)]
    (respond out "NEWGAME")))

(create-server
  9011
  (fn [in-stream out-stream]
    (with-open [in (reader in-stream)
                out (writer out-stream)]
      (let [id (UUID/randomUUID)]
        (doseq [line (read-lines in)]
          (.lock lock)
          (try
            (cond
              (.startsWith line "ECHO") (ECHO line in out)
              (.startsWith line "LOGIN") (LOGIN line in out id)
              :else
                (let [player (get-player-id id)]
                  (cond
                    (.startsWith line "NEWGAME") (NEWGAME line player))))
            (catch Exception e (ERROR out (.toString e)))
            (finally (.unlock lock))))))))
