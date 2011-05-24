(ns innovation.initialize
  (:import (java.util UUID))
  (:use (innovation cards)
        (innovation definitions)))

; create an empty stack
(defn new-stack [color]
  (struct-map stack :color color :splay :none :cards []))

; create a map with stacks for each color
; suitable for the :stacks key in the player struct
(defn new-stacks []
  {:red (new-stack :red)
   :blue (new-stack :blue)
   :green (new-stack :green)
   :yellow (new-stack :yellow)
   :purple (new-stack :purple)})

; create a new player
(defn new-player [id name]
  (struct-map player
    :id id
    :name name
    :stacks (new-stacks)
    :hand '()
    :score '()
    :achievements '()
    :foreshadow '() ))

; creates a map suitable for the :piles key of the game struct
(defn new-piles []
  {1 {:cards (shuffle age-1-cards)} 2 {:cards []} 3 {:cards []} 4 {:cards []} 5 {:cards (shuffle age-5-cards)} 6 {:cards []} 7 {:cards []} 8 {:cards []} 9 {:cards []} 10 {:cards []}})

; create a new game
(defn new-game [players]
  (struct-map game
    :id (UUID/randomUUID)
    :players (shuffle players)
    :turn-num 1
    :player-num 0
    :actions 1
    :state nil
    :achievements '()
    :piles (new-piles)))

