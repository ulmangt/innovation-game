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
(defstruct card :id :name :age :color :symbols :dogma)

; a struct representing a stack of cards
; (which must all be of the same color
(defstruct stack :color :splay :cards)

; a struct representing a player
; hand, score, achieve, and foreshadow are vectors
; of cards with low indices on top
(defstruct player :id :name :stacks :hand :score :achievements :foreshadow)

; a struct representing the entire state of a game
; players is a vector of the players in turn order
; achievements is a seq of available achievements
; piles a map from integer (card :age) to card
(defstruct game :id :players :achievements :piles)

; create an empty stack
(defn new-stack [color]
  (struct-map stack :color color :splay :node :cards []))

; create a test game
(def test-game
  (struct-map game
    :id (UUID/randomUUID)
    :players []
    :achievements []
    :piles {1 [] 2 [] 3 [] 4 [] 5 [] 6 [] 7 [] 8 [] 9 [] 10 []}))

;create a test player
(def test-player-1
  (struct-map player
    :id (UUID/randomUUID)
    :name "Beep Beep"
    :hand []
    :score []
    :foreshadow []
    :achievements []
    :stacks {:red (new-stack :red)
             :blue (new-stack :blue)
             :green (new-stack :green)
             :yellow (new-stack :yellow)
             :purple (new-stack :purple)}))



; create a test card
(def astronomy 
  (struct-map card :id 1
                   :name "Astronomy"
                   :age 5
                   :color :purple
                   :symbols [:crown :bulb :bulb :hex]
                   :dogma (fn [_] _)))

(def city-states
  (struct-map card :id 2
                   :name "City States"
                   :age 1
                   :color :purple
                   :symbols [:hex :crown :crown :castle]
                   :dogma (fn [_] _)))

; create a test stack
(def purple-stack-1
  (struct-map stack :color :purple
                    :splay :none
                    :cards [astronomy city-states]))

(def purple-stack-2
  (struct-map stack :color :purple
                    :splay :none
                    :cards [city-states astronomy]))

(def purple-stack-3
  (struct-map stack :color :purple
                    :splay :none
                    :cards [city-states]))

(def purple-stack-4 (new-stack :purple))

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
