(ns innovation.test
  (:use (innovation core cards)))

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

