# Blackjack (Java AWT) 

A desktop Blackjack game built with **Java AWT** featuring a casino-style felt table UI, Unicode card glyphs, bankroll tracking, and split-hand gameplay.

---

## Features

* **Blackjack gameplay**

  * Hit / Stand / Double / Split
  * Dealer plays to **17** (hits on **soft 17**)
  * Blackjack pays **3:2** (1.5× bet)

* **Splitting**

  * Split up to **4 total hands**
  * **Aces can only be split once** (tracked in code; split-ace hands auto-finish after receiving one card)

* **Insurance / Even Money**

  * If dealer shows an Ace, you can take **Insurance** (½ bet)
  * If you have Blackjack vs dealer Ace, you can take **Even Money**

* **Bank + Stats**

  * Bankroll, wins, losses persist to `player.txt`

* **UI**

  * Green felt table
  * Large card symbols (Unicode playing cards)
  * Button colors:

    * Hit = bright green
    * Stand = red
    * Double = yellow
    * Split = light blue
  * Message banner: **red background with white text**

---

## Requirements

* **Java 8+** (recommended: 11+)
* Desktop OS (Windows / macOS / Linux) with a standard Java runtime

---

## How to Run

### Option A: Terminal

1. Save the file as:

   * `Blackjack.java`

2. Compile:

   ```bash
   javac Blackjack.java
   ```

3. Run:

   ```bash
   java Blackjack
   ```

### Option B: IDE

* Open the project/folder in IntelliJ, Eclipse, or VS Code
* Run the `Blackjack` class

---

## Player Save File (`player.txt`)

The game reads and writes a file named `player.txt`.

**Format:**

```text
username,bankAccount,wins,losses
```

**Example:**

```text
Player,500,3,2
```

If `player.txt` does not exist, the game starts with default values.

---

## Controls

* **Play**: starts a hand using the bet amount
* **Hit**: draw another card
* **Stand**: end your current hand
* **Double**: double your bet and draw exactly one card (only on first move)
* **Split**: split into another hand (only on first move when both cards match)

---

## Game Rules (Implementation Notes)

* Dealer’s **hole card** is hidden until resolve
* Dealer hits on **soft 17**
* Shoe supports **2, 4, 6, or 8 decks**
* Shoe reshuffles automatically when low (based on a 75% threshold)
* Split hands are supported up to **4 total hands**
* Split aces are restricted and auto-finish after receiving one card

---

## Notes / Tips

* Unicode playing card glyphs may look different depending on your OS and font support
* If the UI looks too big or too small, adjust the font sizes in `createCardLabel(...)`

---

## Future Improvements

* Add a “New Player / Change Name” option in the UI
* Add surrender, side bets, or configurable rules
* Replace Unicode cards with image-based card assets
* Add animations and sound effects

