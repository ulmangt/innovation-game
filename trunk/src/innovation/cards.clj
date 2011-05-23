(ns innovation.cards)

(def symbols #{ :crown :leaf :factory :castle :clock :bulb :hex })
(def colors #{ :red :green :blue :purple :yellow })
(def splays #{ :top :none :left :right :up })

;debugging parts of expressions
(defmacro dbg [x]
  `(let [x# ~x]
    (println "dbg:" '~x "=" x#)
    x#))

; a struct representing a dogma ability on a card
; symbol - the symbol that the effect keys off of
; text - the text of the dogma effect
; fn - a handler function which takes arguments [game player]
;      game - the current state of the game
;      player - the player performing the dogma action
(defstruct dogma :symbol :text :fn)

; a struct representing a card
; name - a string containing the name of the card
; age - a number 1-10 representing the age of the card
; color - the color of the card
; symbols - a length 4 vector containing the symbols on the card
; dogmas - a vector containing the dogma effects of the card in the order they occur
(defstruct card :name :age :color :symbols :dogmas)

(def city-states
  (struct-map card :name "City States"
                   :age 1
                   :color :purple
                   :symbols [:hex :crown :crown :castle]
                   :dogma (fn [_] _)))

(def the-wheel
  (struct-map card :name "The Wheel"
                   :age 1
                   :color :green
                   :symbols [:hex :castle :castle :castle]
                   :dogma (vector
                            (struct-map dogma
                              :symbol :castle
                              :fn (fn [game player]
                                    )))))

(def mysticism
  (struct-map card :name "Mysticism"
                   :age 1
                   :color :purple
                   :symbols [:hex :castle :castle :castle]
                   :dogma (fn [_] _)))

(def writing
  (struct-map card :name "Writing"
                   :age 1
                   :color :blue
                   :symbols [:hex :bulb :bulb :crown]
                   :dogma (fn [_] _))) 

(def sailing
  (struct-map card :name "Sailing"
                   :age 1
                   :color :green
                   :symbols [:crown :crown :hex :leaf]
                   :dogma (fn [_] _)))

(def age-1-cards
  (list
    city-states
    the-wheel
    mysticism
    writing
    sailing))


(def age-2-cards
  (list))

(def age-3-cards
  (list))

(def age-4-cards
  (list))

(def astronomy
  (struct-map card :name "Astronomy"
                   :age 5
                   :color :purple
                   :symbols [:crown :bulb :bulb :hex]
                   :dogma (fn [_] _)))

(def age-5-cards
  (list
    astronomy))

(def age-6-cards
  (list))

(def age-7-cards
  (list))

(def age-8-cards
  (list))

(def age-9-cards
  (list))

(def age-10-cards
  (list))

(def all-cards-list
  (concat age-1-cards age-2-cards age-3-cards age-4-cards age-5-cards
          age-6-cards age-7-cards age-8-cards age-9-cards age-10-cards))

(def all-cards-age
  (hash-map 1 age-1-cards 2 age-2-cards 3 age-3-cards 4 age-4-cards 5 age-5-cards
            6 age-6-cards 7 age-7-cards 8 age-8-cards 9 age-9-cards 10 age-10-cards))

(def all-cards-name
  (apply hash-map (interleave (map :name all-cards-list) all-cards-list)))

(defn get-card [name]
  (all-cards-name name))

(defn get-cards [age]
  (all-cards-age age))
