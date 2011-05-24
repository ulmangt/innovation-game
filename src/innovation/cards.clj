(ns innovation.cards
  (:use (innovation core)
        (innovation definitions)))

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
                              :fn (fn [game player-id]
                                    (repeat-action draw-card 2 [game player-id 1]))))))

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
