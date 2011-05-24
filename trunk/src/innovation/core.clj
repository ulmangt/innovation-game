(ns innovation.core
  (:import (java.util UUID))
  (:use (innovation cards)
        clojure.contrib.import-static
        clojure.set
        [clojure.contrib.seq-utils :only (includes?)]))

; a struct representing a stack of cards
; (which must all be of the same color
(defstruct stack :color :splay :cards)

; a struct representing a player
; hand, score, achieve, and foreshadow are vectors
; of cards with low indices on top
(defstruct player :id :name :stacks :hand :score :achievements :foreshadow)

; a struct representing the entire state of a game
; players - a vector of the players in turn order
; achievements - a seq of available achievements
; piles - a map from integer (card :age) to card
; turn-num - the game round number
; player-num - the current player
; actions - the number of actions the current player has
; state - custom information about the current state of the game
;         the information contained here may be different depending
;         on what the game is currently waiting on (it is used to
;         keep track of the sequence of events that happen during
;         dogma execution)
(defstruct game :id :players :turn-num :player-num :actions :state :achievements :piles)

; create an empty stack
(defn new-stack [color]
  (struct-map stack :color color :splay :none :cards []))

; create a map with stacks for each color
; suitable for the :stacks key in the player struct
(defn new-stacks []
  {:red (new-stack :red)
   :blue (new-stack :blue)
   :green (new-stack :green)
   :yellow (new-stack :yellow)
   :purple (new-stack :purple)})

; create a new player
(defn new-player [id name]
  (struct-map player
    :id id
    :name name
    :stacks (new-stacks)
    :hand '()
    :score '()
    :achievements '()
    :foreshadow '() ))

; creates a map suitable for the :piles key of the game struct
(defn new-piles []
  {1 {:cards (shuffle age-1-cards)} 2 {:cards []} 3 {:cards []} 4 {:cards []} 5 {:cards (shuffle age-5-cards)} 6 {:cards []} 7 {:cards []} 8 {:cards []} 9 {:cards []} 10 {:cards []}})

; create a new game
(defn new-game [players]
  (struct-map game
    :id (UUID/randomUUID)
    :players (shuffle players)
    :turn-num 1
    :player-num 0
    :actions 1
    :state nil
    :achievements '()
    :piles (new-piles)))

; splay a stack
(defn splay-stack [stack splay]
  (assoc stack :splay splay))

; tuck a card
(defn tuck-card-stack [stack card]
  (let [{cards :cards} stack]
    (assoc stack :cards (vec (concat cards (list card))))))

; meld a card
(defn meld-card-stack [stack card]
  (let [{cards :cards} stack]
    (assoc stack :cards (vec (cons card cards)))))

; remove bottom card
(defn remove-bottom-card [stack]
  (let [{cards :cards} stack]
    (if (empty? cards)
      stack
      (assoc stack :cards (pop cards)))))

; remove top card
(defn remove-top-card [stack]
  (let [{cards :cards} stack]
    (if empty? cards)
      stack
      (assoc stack :cards (subvec cards 1 (count cards)))))

; peek top card
(defn peek-top-card [stack]
  (let [{cards :cards} stack]
    (if (empty? cards)
        nil
        (nth cards 0))))

; get a player by their id / seat-number
(defn get-player [game player-id]
  (let [{players :players} game]
    (nth players player-id)))

; add a card to player's hand
(defn add-card-hand [player card]
  (let [{hand :hand} player]
    (assoc player :hand (cons card hand))))

; generates a predicate which checks for a given card
(defn is-card? [card]
  (fn [x] (= (:name card) (:name x))))

; remove a card from player's hand
(defn remove-card-hand [player card]
  (let [{hand :hand} player]
    (assoc player :hand (remove (is-card? card) hand))))

; tests whether the given player has the given card
(defn has-card? [player card]
  (contains? (:hand player) card))


; gets the symbols visible on the card for a given splay
; assumes the card is not the top card unless the splay is :none
(defn get-symbols [card splay]
  (cond
    (= splay :top)   (card :symbols)
    (= splay :none)  []
    (= splay :left)  (subvec (card :symbols) 3 4)
    (= splay :right) (subvec (card :symbols) 0 2)
    (= splay :up)    (subvec (card :symbols) 1 4)))

; counts the visible symbols of a particular type on the card
; for a given splay
(defn count-symbols [card splay symbol]
  (if (nil? card)
    0
    (count (filter #(= symbol %) (get-symbols card splay)))))

; counts the number of visible symbols of the given
; type in the stack
(defn count-symbols-stack [stack symbol]
  (let [{:keys [splay cards]} stack
        top-card (first cards)
        bottom-cards (rest cards)
        top-count (count-symbols top-card :top symbol)
        bottom-count (apply + (map #(count-symbols % splay symbol) bottom-cards))]
    (+ top-count bottom-count)))

; returns the highest age card among a player's top cards
(defn highest-top-card-age [player]
  (let [{stacks :stacks} player
        top-card-ages (filter number? (map #(:age (peek-top-card %)) (vals stacks)))]
    (if (empty? top-card-ages)
      0
      (apply max top-card-ages))))

; returns the top cards which satisfy a given predicate
(defn get-top-cards [player pred]
  (let [{stacks :stacks} player
        top-cards (vals stacks)]
    (filter pred top-cards)))

(defn get-top-cards-color [player color]
  (get-top-cards player #(= color (:color %))))

(defn get-top-cards-highest [player]
  (let [highest-age (highest-top-card-age player)]
    (get-top-cards player #(= highest-age (:age %)))))

(defn get-top-cards-with-symbol [player symbol]
  (get-top-cards player #(contains? (get-symbols :top))))

(defn get-top-cards-without-symbol [player symbol]
  (get-top-cards player #(not (contains? (get-symbols :top)))))

;;;;
;;;; game actions
;;;; all functions take a game state object and return an updated game state object
;;;;

; draw a card
(defn draw-card [game player-id age]
  (let [{piles :piles} game
        player (get-player game player-id)]
    (loop [current-age age]
      (if (= current-age 11)
        nil
        (let [pile (piles current-age)
              card (peek-top-card pile)]
          (if (not (nil? card))
            (let [player (add-card-hand player card)
                  pile (remove-top-card pile)]
              (assoc-in
                (assoc-in game [:players player-id] player)
                [:piles current-age] pile))
            (recur (+ current-age 1))))))))

; return a card
(defn return-card [game player-id card-name]
  (let [{piles :piles} game
        player (get-player game player-id)
        card (get-card card-name)
        age (:age card)
        pile (piles age)]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:piles age] (tuck-card-stack pile card))))

; meld a card
(defn meld-card [game player-id card-name]
  (let [player (get-player game player-id)
        card (get-card card-name)
        color (:color card)
        stack (color (:stacks player))]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:players player-id :stacks color] (meld-card-stack stack card))))

; tuck a card
(defn tuck-card [game player-id card-name]
  (let [player (get-player game player-id)
        card (get-card card-name)
        color (:color card)
        stack (color (:stacks player))]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:players player-id :stacks color] (tuck-card-stack stack card))))
