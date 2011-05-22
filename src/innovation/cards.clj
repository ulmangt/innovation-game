(ns innovation.cards
  (:use (innovation core)))

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
                   :dogma (fn [_] _)))

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

(def astronomy
  (struct-map card :name "Astronomy"
                   :age 5
                   :color :purple
                   :symbols [:crown :bulb :bulb :hex]
                   :dogma (fn [_] _)))
