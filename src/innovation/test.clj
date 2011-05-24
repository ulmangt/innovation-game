(ns innovation.test
  (:import (java.util UUID))
  (:use (innovation core cards)))

; create a test game
(def test-game (new-game [(new-player 0 "Player0") (new-player 1 "Player1")]))

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

(def test-game-1 (meld-card (meld-card (meld-card (draw-card (draw-card (draw-card test-game 0 1) 0 2) 0 1) 0 "Astronomy") 0 "Mysticism") 0 "The Wheel"))
(get-top-cards-with-symbol (get-player test-game-1 0) :castle)
