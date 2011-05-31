(ns innovation.definitions)


(defstruct lobby :players)


(def symbols #{ :crown :leaf :factory :castle :clock :bulb :hex })
(def colors #{ :red :green :blue :purple :yellow })
(def splays #{ :top :none :left :right :up })

;debugging parts of expressions
(defmacro dbg [x]
  `(let [x# ~x]
    (println "dbg:" '~x "=" x#)
    x#))

;(repeat draw-card 3 [game player-id age])
; calls draw card with given arguments 3 times
; feeding the result back into the first argument
; of the next call to draw card
(defmacro repeat-action [action-fn times [& args]]
  `(let [other-args# '~(rest args)
         game# ~(first args)]
     (loop [g# game# t# ~times]
       (if (= 0 t#)
         g#
         (recur (apply ~action-fn g# other-args#) (- t# 1))))))


; a struct representing a stack of cards
; (which must all be of the same color
(defstruct stack :color :splay :cards)

; a struct representing a player
; hand, score, achieve, and foreshadow are vectors
; of cards with low indices on top
; in, out are streams
(defstruct player :name :password :in :out :stacks :hand :score :achievements :foreshadow)

; a struct representing the entire state of a game
; players - a vector of the players in turn order
; achievements - a seq of available achievements
; piles - a map from integer (card :age) to card
; turn-num - the game round number
; player-num - the current player
; actions - the number of actions the current player has
; state - custom information about the current state of the game
;         the information contained here may be different depending
;         on what the game is currently waiting on (it is used to
;         keep track of the sequence of events that happen during
;         dogma execution)
(defstruct game :id :players :turn-num :player-num :actions :state :achievements :piles)

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

