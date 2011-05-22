(ns innovation.core
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
(defstruct card :name :age :color :symbols :dogma)

; a struct representing a stack of cards
; (which must all be of the same color
(defstruct stack :color :splay :cards)

; create a test card
(def astronomy 
  (struct-map card :name "Astronomy"
                   :age 5
                   :color :purple
                   :symbols [:crown :bulb :bulb :hex]
                   :dogma (fn [_] _)))

(def city_states
  (struct-map card :name "City States"
                   :age 1
                   :color :purple
                   :symbols [:hex :crown :crown :castle]
                   :dogma (fn [_] _)))

; create a test stack
(def purple-stack-1
  (struct-map stack :color :purple
                    :splay :none
                    :cards (list astronomy city_states)))

(def purple-stack-2
  (struct-map stack :color :purple
                    :splay :none
                    :cards (list city_states astronomy)))

(def purple-stack-3
  (struct-map stack :color :purple
                    :splay :none
                    :cards (list city_states)))

(def purple-stack-4
  (struct-map stack :color :purple
                    :splay :none
                    :cards (list)))

; splay a stack
(defn splay-stack [stack splay]
  (assoc stack :splay splay))

; tuck a card
(defn tuck-card [stack card]
  (let [{cards :cards} stack]
    (concat cards (list card))))

; meld a card
(defn meld-card [stack card]
  (let [{cards :cards} stack]
    (cons card cards)))
    

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
