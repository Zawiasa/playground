(ns agrajz.core
  (:require
   [reagent.core :as reagent]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom {}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Defs

(defonce tagok (atom '("huh")))
(defonce nev (atom 0))
(defn felvesz [player] (swap! tagok conj player))
(defn adatbevitel []  
  [:div
    [:input {:type "text"
;;	     :value @nev
             :on-change #(reset! nev (-> % .-target .-value))}]
    [:button {:on-click #(do
                            (felvesz @nev)
                            (reset! nev ""))} "Hozzáad" ]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [ratom]
  [:div (adatbevitel)
   [:div @tagok]
   [:div [:p "Ez van a boxban: " @nev]]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
