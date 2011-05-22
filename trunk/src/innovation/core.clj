(ns innovation.core
  (:use clojure.contrib.import-static
        clojure.set
        [clojure.contrib.seq-utils :only (includes?)]))

(def symbols #{ :crown :leaf :factory :castle :clock :lightbulb :hex })
(def colors #{ :red :green :blue :purple :yellow })
(def splays #{ :none :left :right :up })

; a struct representing a card
(defstruct card :name :age :color :symbols :dogma)

; a struct representing a stack of cards
; (which must all be of the same color
(defstruct stack :color :splay :cards)

; create a test card
(def astronomy 
  (struct-map card :name "Astronomy"
                   :age 5
                   :color :purple
                   :symbols [:crown :lightbulb :lightbulb :hex]
                   :dogma (fn [_] _)))

(def city_states
  (struct-map card :name "City States"
                   :age 1
                   :color :purple
                   :symbols [:hex :crown :crown :castle]
                   :dogma (fn [_] _)))

; create a test stack
(def purple-stack
  (struct-map stack :color :purple
                    :splay :none
                    :cards (list astronomy city_states)))

; gets the symbols visible on the card for a given splay
; assumes the card is not the top card unless the splay is :none
(defn get-symbols [card splay]
  (cond
    (= splay :none)  (card :symbols)
    (= splay :left)  (subvec (card :symbols) 3 4)
    (= splay :right) (subvec (card :symbols) 0 2)
    (= splay :up)    (subvec (card :symbols) 1 4)))

; counts the visible symbols of a particular type on the card
; for a given splay
(defn count-symbols [card splay symbol]
  (count (filter #(= symbol %) (get-symbols card splay))))

(defn count-symbols-stack [stack symbol]
  (let [{:keys [splay cards]} stack]
    cards))
