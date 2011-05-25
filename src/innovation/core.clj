(ns innovation.core
  (:use (innovation definitions)
        clojure.contrib.import-static
        clojure.set
        [clojure.contrib.seq-utils :only (includes?)]))

; splay a stack
(defn splay-stack [stack splay]
  (assoc stack :splay splay))

; tuck a card
(defn tuck-card-stack [stack card]
  (let [{cards :cards} stack]
    (assoc stack :cards (vec (concat cards (list card))))))

; meld a card
(defn meld-card-stack [stack card]
  (let [{cards :cards} stack]
    (assoc stack :cards (vec (cons card cards)))))

; remove bottom card
(defn remove-bottom-card [stack]
  (let [{cards :cards} stack]
    (if (empty? cards)
      stack
      (let [new-stack (assoc stack :cards (pop cards))
						new-size (count (:cards new-stack))]
				(if (<= new-size 1)
					(assoc new-stack :splay :none)
					new-stack)))))

; remove top card
(defn remove-top-card [stack]
  (let [{cards :cards} stack]
    (if (empty? cards)
      stack
      (let [new-stack (assoc stack :cards (subvec cards 1 (count cards)))
						new-size (count (:cards new-stack))]
				(if (<= new-size 1)
					(assoc new-stack :splay :none)
					new-stack)))))

; remove a given card from a stack
(defn remove-card [stack card-name]
	(let [new-stack (dbg (assoc stack :cards (vec (remove #(= card-name (:name %)) (:cards stack)))))
				new-size (dbg (count (:cards new-stack)))]
		(if (<= new-size 1)
			(assoc new-stack :splay :none)
			new-stack)))

; peek top card
(defn peek-top-card [stack]
  (let [{cards :cards} stack]
    (if (empty? cards)
        nil
        (nth cards 0))))

; get a player by their id / seat-number
(defn get-player [game player-id]
  (let [{players :players} game]
    (nth players player-id)))

; add a card to player's hand
(defn add-card-hand [player card]
  (let [{hand :hand} player]
    (assoc player :hand (cons card hand))))

; generates a predicate which checks for a given card
(defn is-card? [card]
  (fn [x] (= (:name card) (:name x))))

; remove a card from player's hand
(defn remove-card-hand [player card]
  (let [{hand :hand} player]
    (assoc player :hand (remove (is-card? card) hand))))

; tests whether the given player has the given card
(defn has-card? [player card]
  (contains? (:hand player) card))

; searches the players hand for the named card
(defn get-card-hand [player card-name]
  (let [{hand :hand} player]
    (first (filter #(= card-name (:name %)) hand))))

; searches the stack for the named card
(defn get-card-stack [player card-name stack-color]
	(let [{hand :hand stacks :stacks} player
			  stack (stack-color stacks)
				cards (:cards stack)]
		(first (filter #(= card-name (:name %)) cards))))

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

; returns the highest age card among a player's top cards
(defn highest-top-card-age [player]
  (let [{stacks :stacks} player
        top-card-ages (filter number? (map #(:age (peek-top-card %)) (vals stacks)))]
    (if (empty? top-card-ages)
      0
      (apply max top-card-ages))))

; returns the top cards which satisfy a given predicate
(defn get-top-cards [player pred]
  (let [{stacks :stacks} player
        top-cards (map #(peek-top-card %) (vals stacks))
        top-cards-non-nil (remove nil? top-cards)]
    (filter pred top-cards-non-nil)))

(defn get-top-cards-color [player color]
  (get-top-cards player #(= color (:color %))))

(defn get-top-cards-highest [player]
  (let [highest-age (highest-top-card-age player)]
    (get-top-cards player #(= highest-age (:age %)))))

(defn get-top-cards-with-symbol [player symbol]
  (get-top-cards player #(some #{symbol} (get-symbols % :top))))

(defn get-top-cards-without-symbol [player symbol]
  (get-top-cards player #(not (some #{symbol} (get-symbols % :top)))))

;;;;
;;;; game actions
;;;; all functions take a game state object and return an updated game state object
;;;;

; draw a card from a specific age
(defn draw-card [game player-id age]
  (let [{piles :piles} game
        player (get-player game player-id)]
    (loop [current-age age]
      (if (= current-age 11)
        nil
        (let [pile (piles current-age)
              card (peek-top-card pile)]
          (if (not (nil? card))
            (let [player (add-card-hand player card)
                  pile (remove-top-card pile)]
              (assoc-in
                (assoc-in game [:players player-id] player)
                [:piles current-age] pile))
            (recur (+ current-age 1))))))))

; draw action
(defn draw-card-action [game player-id]
  (draw-card game player-id (highest-top-card-age (get-player game player-id))))

; return a card from the player's hand
(defn return-card-hand [game player-id card-name]
  (let [{piles :piles} game
        player (get-player game player-id)
        card (get-card-hand player card-name)
        age (:age card)
        pile (piles age)]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:piles age] (tuck-card-stack pile card))))

; return a card from a stack
(defn return-card-stack [game player-id card-name stack-color]
	  (let [{piles :piles} game
           player (get-player game player-id)
				   stack (stack-color (:stacks player))
           card (get-card-stack player card-name stack-color)
           age (:age card)
           pile (piles age)
					 game-1 (assoc-in
										game
										[:players player-id :stacks stack-color]
										(remove-card stack card-name))]
			(assoc-in
				game-1
				[:piles age]
				(tuck-card-stack pile card))))

; meld a card
(defn meld-card [game player-id card-name]
  (let [player (get-player game player-id)
        card (get-card-hand player card-name)
        color (:color card)
        stack (color (:stacks player))]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:players player-id :stacks color] (meld-card-stack stack card))))

; tuck a card
(defn tuck-card [game player-id card-name]
  (let [player (get-player game player-id)
        card (get-card-hand player card-name)
        color (:color card)
        stack (color (:stacks player))]
    (assoc-in
      (assoc-in game [:players player-id] (remove-card-hand player card))
      [:players player-id :stacks color] (tuck-card-stack stack card))))

(defn splay-stack [game player-id stack-color splay]
	(let [player (get-player game player-id)
				stack (stack-color (:stacks player))
				stack-size (count (:cards stack))
				actual-splay (if (<= stack-size 1) :none splay)]
		(assoc-in game [:players player-id :stacks stack-color :splay] actual-splay)))
			
