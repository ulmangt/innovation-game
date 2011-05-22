(ns innovation.cards
  (:use (innovation core)))

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
