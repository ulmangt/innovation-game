(ns innovation.core
  (:import (java.util UUID))
  (:use clojure.contrib.import-static
        clojure.set
        [clojure.contrib.seq-utils :only (includes?)]))

(def symbols #{ :crown :leaf :factory :castle :clock :bulb :hex })
(def colors #{ :red :green :blue :purple :yellow })
(def splays #{ :top :none :left :right :up })

;debugging parts of expressions
(defmacro dbg [x]
  `(let [x# ~x]
    (println "dbg:" '~x "=" x#)
    x#))

; a struct representing a card
; name - a string containing the name of the card
; age - a number 1-10 representing the age of the card
; color - the color of the card
; symbols - a length 4 vector containing the symbols on the card
; dogmas - a vector containing the dogma effects of the card in the order they occur
(defstruct card :name :age :color :symbols :dogmas)

; a struct representing a dogma ability on a card
; symbol - the symbol that the effect keys off of
; text - the text of the dogma effect
; fn - a handler function which takes arguments [game player]
;      game - the current state of the game
;      player - the player performing the dogma action
(defstruct dogma :symbol :text :fn)

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
; turn - the id of the player whose turn it is
; actions - the number of actions the current player has
; state - custom information about the current state of the game
;         the information contained here may be different depending
;         on what the game is currently waiting on (it is used to
;         keep track of the sequence of events that happen during
;         dogma execution)
(defstruct game :id :players :turn :actions :state :achievements :piles)

; create an empty stack
(defn new-stack [color]
  (struct-map stack :color color :splay :node :cards []))

; splay a stack
(defn splay-stack [stack splay]
  (assoc stack :splay splay))

; tuck a card
(defn tuck-card [stack card]
  (let [{cards :cards} stack]
    (assoc stack :cards (vec (concat cards (list card))))))

; meld a card
(defn meld-card [stack card]
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
    (if empty? cards)
      nil
      (nth 0 cards)))

; get a player
(defn get-player [game player-id]
  (let [{players :players} game]
    (nth player-id players)))

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

; draw a card
(defn draw-card [game player age]
  (let [{piles :piles} game]
    (loop [current-age age]
      (if (= current-age 11)
        nil
        (let [pile (piles current-age)
              card (peek-top-card pile)]
          (if (not (nil? card))
            (let [player (add-card-hand player card)
                  pile (remove-top-card pile)]
              (assoc-in
                (assoc-in
                  game
                  [:players (:id player)] player)
                [:piles current-age] pile)
            (recur (+ current-age 1)))))))))

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
