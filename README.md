# **Blackjack – Java AWT Application**

## **Overview**
This project is a Java‑based Blackjack game that combines a graphical user interface (GUI) built with **Java AWT** and a fully functional **terminal‑driven game engine**. It was created as a learning project to explore GUI layout management, event handling, card‑game logic, and multi‑deck shuffling.

The application currently displays an AWT window with buttons for **Hit**, **Stand**, and **Double**, while the core gameplay—including betting, card dealing, and win/loss evaluation—runs in the terminal. Future updates may migrate more of the gameplay into the GUI.

---

## **Features**
### **Gameplay**
- Supports **2, 4, or 6 decks** of cards  
- Implements **Fisher–Yates shuffle** for realistic randomization  
- Handles:
  - Player and dealer turns  
  - Blackjack detection  
  - Insurance and even‑money options  
  - Double‑down logic  
  - Soft 17 rules for the dealer  
- Tracks a **bank account** starting at \$500  
- Validates bets and prevents negative balances  

### **GUI (Java AWT)**
- Main window titled **“Black Jack”**
- Panels for:
  - Dealer hand  
  - Player hand  
  - Action buttons  
  - Bank display and messages  
- Buttons included:
  - **Hit**
  - **Stand**
  - **Double**
- Layouts used:
  - `BorderLayout`
  - `GridLayout`
  - `FlowLayout`

The GUI is currently visual only; game actions still occur in the terminal.

---

## **Project Structure**
The primary class is:

```
Blackjack.java
```

It contains:
- The `main` method  
- GUI setup  
- Deck creation and shuffling  
- Game loop  
- Helper methods:
  - `getHandValue()` – calculates hand totals with Ace handling  
  - `isSoft17()` – determines if the dealer must hit  
  - `setLabel()` – safely updates AWT labels on the event queue  

---

## **How to Run**
1. Ensure the file is inside a **src** folder marked as a **Sources Root** in IntelliJ.  
2. Right‑click `Blackjack.java` → **Run 'Blackjack.main()'**  
3. The AWT window will appear, and the terminal will prompt for:
   - Number of decks  
   - Bet amounts  
   - Player decisions (hit, stand, double)  

---

## **Future Improvements**
- Move gameplay logic from terminal to GUI  
- Display card graphics instead of text  
- Add animations or transitions  
- Implement chip denominations and betting UI  
- Add sound effects or game history tracking  

---

## **Purpose**
This project serves as a hands‑on exploration of:
- Java AWT GUI programming  
- Event‑driven design  
- Game logic implementation  
- Data structures for card games  
- Randomization and probability handling  
