(ns masodik.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Helpers

(def jatek (atom {:kapcsolo "fo" :lepes 0 :eredmeny "Tippelj"}))
(defonce szam (atom 0))
(def y (rand-int 101))
(defn tippeles [jatek y] (cond
 	   (< @szam y)
             (swap! jatek assoc :eredmeny (str "Nagyobb mint " @szam))
           (> @szam y)
             (swap! jatek assoc :eredmeny (str "Kisebb mint " @szam)) 
           :else (swap! jatek assoc :kapcsolo "nyer")))

;; -------------------------
;; Elements

(defn gomb [jatek tipus] [:button {:on-click #(case tipus
      "Küldés" (do (tippeles jatek y) (swap! jatek update :lepes inc)) 
      "Újra" (do (swap! jatek assoc :kapcsolo "fo")
                           (swap! jatek assoc :lepes 0)
			   (swap! jatek assoc :eredmeny "Tippelj")
                           (reset! szam 0))	  
)} tipus ])
(defn cim [szoveg szin] [:div {:style {:color szin}} [:h2 szoveg]])
(defn mezo [] [:input {:type "text"
            	       :value @szam
            	       :on-change #(reset! szam (-> % .-target .-value))}])
(defn blokk1 [] [:div 
  (cim "Gondoltam egy számra 0 és 100 között") 
  [:div [:p "A szám:" @szam]]
  [:div (mezo) (gomb jatek "Küldés")]
  [:div [:p "Lépések: " (:lepes @jatek)]]
  [:div [:p (:eredmeny @jatek)]]])
(defn blokk2 [] [:div 
	(cim "Nyertél")
	[:p "A " y "-re gondoltam"]
	[:p "Lépéseid száma: " (:lepes @jatek)]
	(gomb jatek "Újra")
])
(defn panel [jatek] (case (:kapcsolo @jatek)
	"fo" (blokk1)
	"nyer" (blokk2)))

;; -------------------------
;; Views

(defn home-page [ratom]
   [:div [:a {:href "/about"} "go to about page"]])

(defn about-page [ratom]
  [:div (cim "About masodik")
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(def app-state (atom {:page #'home-page}))

(defn current-page [] [:div
  (panel jatek)
  [(get @app-state :page) app-state]])

(secretary/defroute "/" []
  (swap! app-state assoc :page #'home-page))

(secretary/defroute "/about" []
  (swap! app-state assoc :page #'about-page))

;; -----------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
