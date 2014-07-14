(ns typhoon.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :refer [split join replace]]))

(def chal "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis ac posuere justo, vulputate porttitor metus. Nulla et nulla eu nisi laoreet convallis ultrices vitae risus.")

(defn words [text]
  (split text #"\s"))

(defn split-text [text]
  (let [ws (words text)]
    [(str (first ws) " ") (join " " (rest ws))]))

(defn calc-wpm [num-words t timestamp]
  (if (= num-words 0) 0
    (.round js/Math (/ num-words (/ (- t timestamp) 60)))))

(defn next-word [state]
  (let [{:keys [timer start completed current num total]} state
        [word remaining] (split-text (:remaining state))
        new-num (inc num)]
    (conj state {:remaining remaining
                 :completed (str completed current)
                 :challenge word
                 :current ""
                 :num new-num
                 :error false
                 :wpm (calc-wpm new-num timer start)
                 :finished (= new-num total)})))

(defn create-challenge [text time]
  (let [[word remaining] (split-text text)]
    {:remaining remaining
     :completed ""
     :challenge word
     :current ""
     :num 0
     :start time
     :timer time
     :total (count (words text))
     :wpm 0
     :error false
     :finished false}))

(defn partial-match? [{:keys [current challenge]}]
  (and (<= (count current) (count challenge))
       (every? identity (map = (seq current) challenge))))

(defn match? [{:keys [current challenge]}]
  (= current challenge))

(defn iterate-state [state value]
  (let [s (assoc state :current value)]
    (cond
     (:finished s) state
     (match? s) (next-word s)
     :else
     (assoc s :error (not (partial-match? s))))))

(defn now []
  (/ (.now js/Date) 1000))

(defn by-id [id]
  (.getElementById js/document id))

(def state (atom {}))

(defn new-challenge! [text]
  (reset! state (create-challenge text (now)))
  (.focus (by-id "input")))

(defn iterate! [value]
  (reset! state (iterate-state @state value)))

(defn append-dot [col word]
  (conj (conj col word)
        [:span {:class "dot"} " "]))

(defn dots [node text]
  (reduce append-dot node (words text)))

(defn text [{:keys [error completed challenge remaining]}]
  [:section {:class "text"}
   [:p {:class "paragraph"}
    (dots [:span {:class "completed dotted"}] completed)
    [:span {:class "remaining"}
     (dots [:span {:class (str "current dotted" (if error " error"))}] challenge)
     (dots [:span {:class "dotted"}] remaining)]]])

(defn input [{:keys [challenge finished current error]}]
  [:input {:id "input"
           :placeholder (str challenge "·")
           :type "text"
           :value current
           :class (str "input" (if error " error"))
           :spell-check false
           :on-change #(iterate! (-> %
                                     .-target
                                     .-value))}])

(defn challenge [state]
  [:section {:class "challenge"}
   (if (:finished state)
     [:button {:on-click #(new-challenge! chal) :class "button"} "Again"]
     (input state))])

(defn wpm-text [{:keys [finished wpm num timer start]}]
  [:section {:class "stats"}
   [:em {:title "words per second"}
    [:span {:class "wpm"} (if finished wpm (calc-wpm num timer start))]
    " wpm"]])

(defn app []
  [:div
   (wpm-text @state)
   (text @state)
   (challenge @state)])

(defn mountit []
  (reagent/render-component [app] (by-id "game")))

(mountit)
(js/setInterval #(swap! state assoc :timer (now)) 1000)

(new-challenge! chal)

