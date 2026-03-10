import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Blackjack extends Frame {

    private static final Color FELT_GREEN = new Color(22, 121, 10);

    // Player data
    private String username;
    private int bankAccount = 500;
    private int wins = 0;
    private int losses = 0;

    // Game state
    private boolean gameActive = false;
    private boolean firstMove = false;
    private int currentBet = 0;
    private int currentCard = 0;
    private int numDecks = 2;

    // Deck data
    private final String[] suits = {"♦", "♠", "♥", "♣"};
    private final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private int[][] deck;
    private int numCards;

    // Player split-hand data
    private final int MAX_HAND = 12;
    private final int MAX_SPLIT_HANDS = 4;

    private int[][][] playerHands = new int[MAX_SPLIT_HANDS][MAX_HAND][2];
    private int[] playerCounts = new int[MAX_SPLIT_HANDS];
    private int[] handBets = new int[MAX_SPLIT_HANDS];
    private boolean[] handFinished = new boolean[MAX_SPLIT_HANDS];
    private boolean[] splitAceHand = new boolean[MAX_SPLIT_HANDS];

    private int totalHands = 1;
    private int activeHand = 0;
    private boolean hasSplitAces = false;

    // Dealer hand
    private int[][] dealerHand = new int[MAX_HAND][2];
    private int dealerCount = 0;

    // UI
    private final Panel dealerPanel = new Panel();
    private final Panel playerPanel = new Panel();
    private final Panel bottomContainer = new Panel();
    private final Panel optionPanel = new Panel();
    private final Panel bankPanel = new Panel();
    private final Panel betPanel = new Panel();

    private final Button hitBtn = new Button("Hit");
    private final Button standBtn = new Button("Stand");
    private final Button doubleBtn = new Button("Double");
    private final Button splitBtn = new Button("Split");
    private final Button playBtn = new Button("Play");

    private final TextField betField = new TextField(10);
    private final Choice deckChoice = new Choice();

    private final Label dealerLabel = new Label("Dealer Hand", Label.CENTER);
    private final Label dealerTotalLabel = new Label("Total: ?", Label.CENTER);
    private final Label playerLabel = new Label("Player Hand", Label.CENTER);
    private final Label playerTotalLabel = new Label("Total: 0", Label.CENTER);
    private final Label bankLabel = new Label("", Label.RIGHT);
    private final Label msgLabel = new Label("", Label.CENTER);
    private final Label deckLabel = new Label("Decks:");

    private final Panel dealerCardsPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
    private final Panel playerHandsPanel = new Panel();

    private final Panel msgBox = new Panel(new BorderLayout());

    private final Random random = new Random();

    public Blackjack() {
        loadPlayer();
        buildUI();
        createAndShuffleDeck();
        updateBankLabel();
    }

    public static void main(String[] args) {
        Blackjack game = new Blackjack();
        game.setVisible(true);
    }

    private void styleButton(Button b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private void applyButtonColors() {
        // Hit Btn
        styleButton(hitBtn, new Color(0, 200, 0), Color.BLACK);

        // Stand Btn
        styleButton(standBtn, new Color(220, 0, 0), Color.BLACK);

        // Double Btn
        styleButton(doubleBtn, new Color(255, 215, 0), Color.BLACK);

        // Split Btn
        styleButton(splitBtn, new Color(120, 190, 255), Color.BLACK);
        //Play Btn
        styleButton(playBtn, new Color(182, 168, 158, 82), Color.BLACK);
    }

    private void buildUI() {
        setTitle("Black Jack");
        setLayout(new BorderLayout());
        setSize(1080, 760);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePlayer(username, bankAccount, wins, losses);
                System.exit(0);
            }
        });

        Font titleFont = new Font("SansSerif", Font.BOLD, 20);
        Font totalFont = new Font("SansSerif", Font.PLAIN, 16);
        Font msgFont = new Font("SansSerif", Font.BOLD, 14);

        dealerLabel.setFont(titleFont);
        playerLabel.setFont(titleFont);
        dealerTotalLabel.setFont(totalFont);
        playerTotalLabel.setFont(totalFont);
        msgLabel.setFont(msgFont);

        setBackground(FELT_GREEN);

        dealerPanel.setBackground(FELT_GREEN);
        playerPanel.setBackground(FELT_GREEN);

        dealerCardsPanel.setBackground(FELT_GREEN);
        playerHandsPanel.setBackground(FELT_GREEN);

        bottomContainer.setBackground(FELT_GREEN);
        betPanel.setBackground(FELT_GREEN);
        optionPanel.setBackground(FELT_GREEN);
        bankPanel.setBackground(FELT_GREEN);

        dealerLabel.setBackground(FELT_GREEN);
        dealerTotalLabel.setBackground(FELT_GREEN);
        playerLabel.setBackground(FELT_GREEN);
        playerTotalLabel.setBackground(FELT_GREEN);
        bankLabel.setBackground(FELT_GREEN);
        deckLabel.setBackground(FELT_GREEN);

        dealerLabel.setForeground(Color.WHITE);
        dealerTotalLabel.setForeground(Color.WHITE);
        playerLabel.setForeground(Color.WHITE);
        playerTotalLabel.setForeground(Color.WHITE);
        bankLabel.setForeground(Color.WHITE);
        deckLabel.setForeground(Color.WHITE);

        msgLabel.setForeground(Color.WHITE);
        msgLabel.setBackground(new Color(200, 0, 0));
        msgLabel.setText(" ");

        msgBox.setBackground(new Color(200, 0, 0));
        msgBox.add(msgLabel, BorderLayout.CENTER);
        msgBox.setPreferredSize(new Dimension(200, 28));

        playerHandsPanel.setLayout(new GridLayout(1, 1, 0, 8));
        playerHandsPanel.setBackground(FELT_GREEN);

        dealerPanel.setLayout(new BorderLayout());
        Panel dealerTop = new Panel(new GridLayout(2, 1));
        dealerTop.setBackground(FELT_GREEN);
        dealerTop.add(dealerLabel);
        dealerTop.add(dealerTotalLabel);
        dealerPanel.add(dealerTop, BorderLayout.NORTH);

        Panel dealerCardsWrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        dealerCardsWrapper.setBackground(FELT_GREEN);
        dealerCardsPanel.setPreferredSize(new Dimension(780, 110));
        dealerCardsWrapper.add(dealerCardsPanel);
        dealerPanel.add(dealerCardsWrapper, BorderLayout.CENTER);

        playerPanel.setLayout(new BorderLayout());
        playerLabel.setText(username + " Hand");
        Panel playerTop = new Panel(new GridLayout(2, 1));
        playerTop.setBackground(FELT_GREEN);
        playerTop.add(playerLabel);
        playerTop.add(playerTotalLabel);
        playerPanel.add(playerTop, BorderLayout.NORTH);

        Panel playerCardsWrapper = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        playerCardsWrapper.setBackground(FELT_GREEN);
        playerHandsPanel.setPreferredSize(new Dimension(780, 420));
        playerCardsWrapper.add(playerHandsPanel);
        playerPanel.add(playerCardsWrapper, BorderLayout.CENTER);

        bottomContainer.setLayout(new GridLayout(2, 1));
        bottomContainer.setPreferredSize(new Dimension(300, 110));

        betPanel.setLayout(new FlowLayout());
        Label betLabel = new Label("Bet:");
        betLabel.setForeground(Color.WHITE);
        betLabel.setBackground(FELT_GREEN);
        betPanel.add(betLabel);
        betPanel.add(betField);
        betPanel.add(playBtn);

        optionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        optionPanel.add(hitBtn);
        optionPanel.add(standBtn);
        optionPanel.add(doubleBtn);
        optionPanel.add(splitBtn);

        applyButtonColors();

        deckChoice.add("2");
        deckChoice.add("4");
        deckChoice.add("6");
        deckChoice.add("8");

        bankPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bankPanel.add(deckLabel);
        bankPanel.add(deckChoice);
        bankPanel.add(bankLabel);
        bankPanel.add(msgBox);

        bottomContainer.add(betPanel);
        bottomContainer.add(bankPanel);

        add(dealerPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

        playBtn.addActionListener(e -> startHand());
        hitBtn.addActionListener(e -> hitPlayer());
        standBtn.addActionListener(e -> standPlayer());
        doubleBtn.addActionListener(e -> doubleDown());
        splitBtn.addActionListener(e -> splitHand());

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        doubleBtn.setEnabled(false);
        splitBtn.setEnabled(false);

        setHandsDisplay("", "", false);
    }

    private void loadPlayer() {
        try {
            Scanner fileScanner = new Scanner(new File("player.txt"));
            if (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                username = parts[0];
                bankAccount = Integer.parseInt(parts[1]);
                wins = Integer.parseInt(parts[2]);
                losses = Integer.parseInt(parts[3]);
            } else {
                username = "Player";
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("player.txt not found! using default values.");
            username = "Player";
        }
    }

    private void createAndShuffleDeck() {
        numDecks = Integer.parseInt(deckChoice.getSelectedItem());
        int numCardsPerDeck = suits.length * ranks.length;
        numCards = numCardsPerDeck * numDecks;
        deck = new int[numCards][2];

        int index = 0;
        for (int d = 0; d < numDecks; d++) {
            for (int i = 0; i < suits.length; i++) {
                for (int j = 0; j < ranks.length; j++) {
                    deck[index][0] = i;
                    deck[index][1] = j;
                    index++;
                }
            }
        }

        for (int i = numCards - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int tempSuit = deck[i][0];
            int tempRank = deck[i][1];

            deck[i][0] = deck[j][0];
            deck[i][1] = deck[j][1];

            deck[j][0] = tempSuit;
            deck[j][1] = tempRank;
        }

        currentCard = 0;
    }

    private void startHand() {
        if (gameActive) return;

        int bet;
        try {
            bet = Integer.parseInt(betField.getText().trim());
        } catch (Exception ex) {
            setMessage("Invalid bet");
            return;
        }

        if (bet <= 0 || bet > bankAccount) {
            setMessage("Invalid bet amount");
            return;
        }

        if (currentCard >= numCards * 0.75) {
            createAndShuffleDeck();
            setMessage("Reshuffling shoe...");
        } else {
            setMessage("");
        }

        currentBet = bet;
        gameActive = true;
        firstMove = true;

        totalHands = 1;
        activeHand = 0;
        hasSplitAces = false;

        for (int i = 0; i < MAX_SPLIT_HANDS; i++) {
            playerCounts[i] = 0;
            handBets[i] = 0;
            handFinished[i] = false;
            splitAceHand[i] = false;
        }

        dealerCount = 0;
        handBets[0] = currentBet;

        deckChoice.setEnabled(false);
        switchPanel(bottomContainer, betPanel, optionPanel);

        playerHands[0][playerCounts[0]++] = drawCard();
        playerHands[0][playerCounts[0]++] = drawCard();

        dealerHand[dealerCount++] = drawCard();
        dealerHand[dealerCount++] = drawCard();

        updateDisplay(false);

        int playerTotal = getHandValue(playerHands[0], playerCounts[0], ranks);
        int dealerTotal = getHandValue(dealerHand, dealerCount, ranks);

        boolean playerBJ = (playerCounts[0] == 2 && playerTotal == 21);
        boolean dealerBJ = (dealerCount == 2 && dealerTotal == 21);
        boolean dealerAceShowing = ranks[dealerHand[0][1]].equals("A");

        if (!playerBJ && dealerAceShowing) {
            int insuranceBet = currentBet / 2;
            if (insuranceBet > 0) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Dealer shows an Ace.\nTake insurance for $" + insuranceBet + "?",
                        "Insurance",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    bankAccount -= insuranceBet;
                    updateBankLabel();

                    if (dealerBJ) {
                        bankAccount += insuranceBet;
                        bankAccount += currentBet;

                        if (!playerBJ) {
                            bankAccount -= currentBet;
                            losses++;
                        }

                        updateBankLabel();
                        updateDisplay(true);
                        finishHand("Dealer has Blackjack! Insurance pays.");
                        return;
                    } else {
                        setMessage("Dealer does not have Blackjack. Insurance lost.");
                    }
                }
            }
        }

        if (playerBJ && dealerAceShowing) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You have Blackjack and dealer shows Ace.\nTake even money?",
                    "Even Money",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                bankAccount += currentBet;
                wins++;
                updateBankLabel();
                updateDisplay(true);
                finishHand("Even money taken. You win!");
                return;
            }
        }

        if (playerBJ || dealerBJ) {
            updateDisplay(true);

            if (playerBJ && dealerBJ) {
                finishHand("Push! Both have Blackjack.");
            } else if (dealerBJ) {
                bankAccount -= currentBet;
                losses++;
                updateBankLabel();
                finishHand("Dealer Blackjack.");
            } else {
                bankAccount += (int) (1.5 * currentBet);
                wins++;
                updateBankLabel();
                finishHand("Blackjack! You win.");
            }
            return;
        }

        updateActionButtons();
    }

    private boolean canSplitCurrentHand() {
        if (!gameActive) return false;
        if (!firstMove) return false;
        if (activeHand >= totalHands) return false;
        if (playerCounts[activeHand] != 2) return false;
        if (totalHands >= MAX_SPLIT_HANDS) return false;

        int rank1 = playerHands[activeHand][0][1];
        int rank2 = playerHands[activeHand][1][1];

        if (rank1 != rank2) return false;
        if (handBets[activeHand] > bankAccount) return false;
        if (rank1 == 12 && hasSplitAces) return false;

        return true;
    }

    private void splitHand() {
        if (!canSplitCurrentHand()) {
            setMessage("Cannot split this hand.");
            return;
        }

        int handIndex = activeHand;
        int originalBet = handBets[handIndex];
        int splitRank = playerHands[handIndex][0][1];

        int newHandIndex = totalHands;
        totalHands++;

        int movedSuit = playerHands[handIndex][1][0];
        int movedRank = playerHands[handIndex][1][1];

        playerCounts[handIndex] = 1;

        playerCounts[newHandIndex] = 0;
        playerHands[newHandIndex][playerCounts[newHandIndex]][0] = movedSuit;
        playerHands[newHandIndex][playerCounts[newHandIndex]][1] = movedRank;
        playerCounts[newHandIndex]++;

        handBets[newHandIndex] = originalBet;
        handFinished[newHandIndex] = false;
        splitAceHand[newHandIndex] = false;

        int[] firstReplacement = drawCard();
        playerHands[handIndex][playerCounts[handIndex]][0] = firstReplacement[0];
        playerHands[handIndex][playerCounts[handIndex]][1] = firstReplacement[1];
        playerCounts[handIndex]++;

        int[] secondReplacement = drawCard();
        playerHands[newHandIndex][playerCounts[newHandIndex]][0] = secondReplacement[0];
        playerHands[newHandIndex][playerCounts[newHandIndex]][1] = secondReplacement[1];
        playerCounts[newHandIndex]++;

        if (splitRank == 12) {
            hasSplitAces = true;
            splitAceHand[handIndex] = true;
            splitAceHand[newHandIndex] = true;

            handFinished[handIndex] = true;
            handFinished[newHandIndex] = true;

            updateDisplay(false);
            updateActionButtons();

            if (allHandsFinished()) playDealerAndResolve();
            else moveToNextHand();
            return;
        }

        firstMove = true;
        updateDisplay(false);
        updateActionButtons();
        setMessage("Hand split. Playing hand " + (activeHand + 1));
    }

    private void hitPlayer() {
        if (!gameActive) return;
        if (handFinished[activeHand]) return;

        firstMove = false;
        doubleBtn.setEnabled(false);
        splitBtn.setEnabled(false);

        int[] newCard = drawCard();
        playerHands[activeHand][playerCounts[activeHand]][0] = newCard[0];
        playerHands[activeHand][playerCounts[activeHand]][1] = newCard[1];
        playerCounts[activeHand]++;

        int playerTotal = getHandValue(playerHands[activeHand], playerCounts[activeHand], ranks);
        updateDisplay(false);

        if (playerTotal > 21) {
            handFinished[activeHand] = true;
            setMessage("Hand " + (activeHand + 1) + " busted with " + playerTotal + "!");
            moveToNextHand();
        } else if (playerTotal == 21) {
            handFinished[activeHand] = true;
            moveToNextHand();
        } else {
            updateActionButtons();
        }
    }

    private void standPlayer() {
        if (!gameActive) return;
        if (handFinished[activeHand]) return;

        firstMove = false;
        handFinished[activeHand] = true;

        updateActionButtons();
        moveToNextHand();
    }

    private void doubleDown() {
        if (!gameActive) return;
        if (handFinished[activeHand]) return;

        if (!firstMove) {
            setMessage("You can only double on your first move.");
            return;
        }

        if (handBets[activeHand] * 2 > bankAccount) {
            setMessage("Not enough money to double down!");
            return;
        }

        firstMove = false;
        handBets[activeHand] *= 2;

        int[] newCard = drawCard();
        playerHands[activeHand][playerCounts[activeHand]][0] = newCard[0];
        playerHands[activeHand][playerCounts[activeHand]][1] = newCard[1];
        playerCounts[activeHand]++;

        int playerTotal = getHandValue(playerHands[activeHand], playerCounts[activeHand], ranks);
        updateDisplay(false);

        handFinished[activeHand] = true;

        if (playerTotal > 21) {
            setMessage("Hand " + (activeHand + 1) + " doubled and busted with " + playerTotal + "!");
        }

        moveToNextHand();
    }

    private boolean allHandsFinished() {
        for (int i = 0; i < totalHands; i++) {
            if (!handFinished[i]) return false;
        }
        return true;
    }

    private void moveToNextHand() {
        for (int i = activeHand + 1; i < totalHands; i++) {
            if (!handFinished[i]) {
                activeHand = i;
                firstMove = true;
                if (splitAceHand[activeHand]) {
                    handFinished[activeHand] = true;
                    moveToNextHand();
                    return;
                }
                updateDisplay(false);
                updateActionButtons();
                setMessage("Playing hand " + (activeHand + 1) + " of " + totalHands);
                return;
            }
        }
        playDealerAndResolve();
    }

    private void playDealerAndResolve() {
        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        doubleBtn.setEnabled(false);
        splitBtn.setEnabled(false);

        int dealerTotal = getHandValue(dealerHand, dealerCount, ranks);
        updateDisplay(true);

        while (dealerTotal < 17 || isSoft17(dealerHand, dealerCount, ranks)) {
            int[] newCard = drawCard();
            dealerHand[dealerCount][0] = newCard[0];
            dealerHand[dealerCount][1] = newCard[1];
            dealerCount++;

            dealerTotal = getHandValue(dealerHand, dealerCount, ranks);
            updateDisplay(true);
        }

        boolean dealerBusted = dealerTotal > 21;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < totalHands; i++) {
            int playerTotal = getHandValue(playerHands[i], playerCounts[i], ranks);
            int bet = handBets[i];
            boolean playerBJ = (playerCounts[i] == 2 && playerTotal == 21 && !splitAceHand[i]);

            result.append("H").append(i + 1).append(": ");

            if (playerTotal > 21) {
                bankAccount -= bet;
                losses++;
                result.append("Bust");
            } else if (playerBJ && dealerTotal != 21) {
                bankAccount += (int) (1.5 * bet);
                wins++;
                result.append("Blackjack");
            } else if (dealerBusted) {
                bankAccount += bet;
                wins++;
                result.append("Win");
            } else if (playerTotal > dealerTotal) {
                bankAccount += bet;
                wins++;
                result.append("Win");
            } else if (playerTotal < dealerTotal) {
                bankAccount -= bet;
                losses++;
                result.append("Lose");
            } else {
                result.append("Push");
            }

            if (i < totalHands - 1) result.append(" | ");
        }

        updateBankLabel();
        finishHand(result.toString());
    }

    private void updateActionButtons() {
        if (!gameActive || handFinished[activeHand]) {
            hitBtn.setEnabled(false);
            standBtn.setEnabled(false);
            doubleBtn.setEnabled(false);
            splitBtn.setEnabled(false);
            return;
        }

        hitBtn.setEnabled(!splitAceHand[activeHand]);
        standBtn.setEnabled(!splitAceHand[activeHand]);
        doubleBtn.setEnabled(firstMove && !splitAceHand[activeHand] && handBets[activeHand] * 2 <= bankAccount);
        splitBtn.setEnabled(canSplitCurrentHand());
    }

    private void finishHand(String message) {
        gameActive = false;
        firstMove = false;
        savePlayer(username, bankAccount, wins, losses);

        setMessage(message);

        switchPanel(bottomContainer, optionPanel, betPanel);

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        doubleBtn.setEnabled(false);
        splitBtn.setEnabled(false);

        betField.setText("");

        if (currentCard >= numCards * 0.75) {
            setMessage(message + "  |  Shoe low - reshuffle on next hand.");
            deckChoice.setEnabled(true);
        }
    }

    private int[] drawCard() {
        if (currentCard >= numCards) {
            createAndShuffleDeck();
        }
        return deck[currentCard++];
    }

    private void updateDisplay(boolean revealDealer) {
        dealerCardsPanel.removeAll();
        playerHandsPanel.removeAll();

        // IMPORTANT: GridLayout must match how many hands are being shown (fixes cutoff)
        playerHandsPanel.setLayout(new GridLayout(Math.max(totalHands, 1), 1, 0, 8));

        Panel dealerRow = buildCardRow(dealerHand, dealerCount, revealDealer);
        dealerCardsPanel.add(dealerRow);

        if (revealDealer) {
            int dealerTotal = getHandValue(dealerHand, dealerCount, ranks);
            dealerTotalLabel.setText("Total: " + dealerTotal);
        } else {
            int showing = getCardValue(dealerHand[0], ranks);
            dealerTotalLabel.setText("Showing: " + showing);
        }

        for (int i = 0; i < totalHands; i++) {
            playerHandsPanel.add(buildPlayerHandRow(i));
        }

        if (gameActive) {
            playerTotalLabel.setText("Playing hand " + (activeHand + 1) + " of " + totalHands);
        } else {
            playerTotalLabel.setText("Total Hands: " + totalHands);
        }

        dealerCardsPanel.validate();
        dealerCardsPanel.repaint();
        playerHandsPanel.validate();
        playerHandsPanel.repaint();
    }

    private void setHandsDisplay(String dealerText, String playerText, boolean revealDealer) {
        dealerCardsPanel.removeAll();
        playerHandsPanel.removeAll();

        dealerTotalLabel.setText(revealDealer ? "Total: 0" : "Total: ?");
        playerTotalLabel.setText("Total: 0");

        dealerCardsPanel.validate();
        dealerCardsPanel.repaint();
        playerHandsPanel.validate();
        playerHandsPanel.repaint();
    }

    private Label createCardLabel(String cardText, boolean redSuit) {
        Label cardLabel = new Label(cardText, Label.CENTER);
        cardLabel.setFont(new Font("Dialog", Font.PLAIN, 56));
        cardLabel.setForeground(redSuit ? new Color(180, 0, 0) : Color.BLACK);
        cardLabel.setBackground(Color.WHITE);
        cardLabel.setPreferredSize(new Dimension(72, 92));
        return cardLabel;
    }

    private boolean isRedSuitIndex(int suitIndex) {
        return suitIndex == 0 || suitIndex == 2;
    }

    private Panel buildCardRow(int[][] hand, int count, boolean revealAll) {
        Panel row = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row.setBackground(FELT_GREEN); // green table

        for (int i = 0; i < count; i++) {
            if (!revealAll && i == 1) {
                row.add(createCardLabel("\uD83C\uDCA0", false));
            } else {
                int suitIndex = hand[i][0];
                int rankIndex = hand[i][1];
                String card = cardToUnicode(suitIndex, rankIndex);
                row.add(createCardLabel(card, isRedSuitIndex(suitIndex)));
            }
        }

        return row;
    }

    private Panel buildPlayerHandRow(int handIndex) {
        Panel outer = new Panel(new BorderLayout());
        outer.setBackground(FELT_GREEN);

        String prefix = (gameActive && handIndex == activeHand && !handFinished[handIndex]) ? "-> " : "   ";
        int total = getHandValue(playerHands[handIndex], playerCounts[handIndex], ranks);

        String title = prefix + "Hand " + (handIndex + 1) +
                "   Bet: $" + handBets[handIndex] +
                "   Total: " + total +
                (splitAceHand[handIndex] ? "   (Split Aces)" : "");

        Label info = new Label(title, Label.CENTER);
        info.setFont(new Font("SansSerif", Font.BOLD, 16));
        info.setBackground(FELT_GREEN);
        info.setForeground(Color.WHITE);

        Panel cards = buildCardRow(playerHands[handIndex], playerCounts[handIndex], true);

        outer.add(info, BorderLayout.NORTH);
        outer.add(cards, BorderLayout.CENTER);

        return outer;
    }

    private void updateBankLabel() {
        bankLabel.setText("Bank: $ " + bankAccount + "   Wins: " + wins + "   Losses: " + losses);
    }

    private void setMessage(String text) {
        // keep height even when empty
        if (text == null || text.trim().isEmpty()) msgLabel.setText(" ");
        else msgLabel.setText(text);
    }

    private String cardToUnicode(int suitIndex, int rankIndex) {
        int base;
        switch (suitIndex) {
            case 1: base = 0x1F0A0; break; // spades
            case 2: base = 0x1F0B0; break; // hearts
            case 0: base = 0x1F0C0; break; // diamonds
            case 3: base = 0x1F0D0; break; // clubs
            default: base = 0x1F0A0;
        }

        int codeOffset;
        switch (rankIndex) {
            case 12: codeOffset = 0x1; break; // A
            case 0:  codeOffset = 0x2; break; // 2
            case 1:  codeOffset = 0x3; break; // 3
            case 2:  codeOffset = 0x4; break; // 4
            case 3:  codeOffset = 0x5; break; // 5
            case 4:  codeOffset = 0x6; break; // 6
            case 5:  codeOffset = 0x7; break; // 7
            case 6:  codeOffset = 0x8; break; // 8
            case 7:  codeOffset = 0x9; break; // 9
            case 8:  codeOffset = 0xA; break; // 10
            case 9:  codeOffset = 0xB; break; // J
            case 10: codeOffset = 0xD; break; // Q
            case 11: codeOffset = 0xE; break; // K
            default: codeOffset = 0x1;
        }

        return new String(Character.toChars(base + codeOffset));
    }

    static int getHandValue(int[][] hand, int count, String[] ranks) {
        int total = 0;
        int aces = 0;

        for (int i = 0; i < count; i++) {
            String rank = ranks[hand[i][1]];

            if (rank.equals("A")) {
                total += 11;
                aces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                total += 10;
            } else {
                total += Integer.parseInt(rank);
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    static boolean isSoft17(int[][] hand, int count, String[] ranks) {
        int total = 0;
        int aces = 0;

        for (int i = 0; i < count; i++) {
            String rank = ranks[hand[i][1]];
            if (rank.equals("A")) {
                total += 11;
                aces++;
            } else if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) {
                total += 10;
            } else {
                total += Integer.parseInt(rank);
            }
        }

        return total == 17 && aces > 0;
    }

    private int getCardValue(int[] card, String[] ranks) {
        String rank = ranks[card[1]];
        if (rank.equals("A")) return 11;
        if (rank.equals("K") || rank.equals("Q") || rank.equals("J")) return 10;
        return Integer.parseInt(rank);
    }

    public static void savePlayer(String username, int bankAccount, int wins, int losses) {
        try {
            FileWriter fw = new FileWriter("player.txt");
            fw.write(username + "," + bankAccount + "," + wins + "," + losses + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void switchPanel(Panel container, Panel remove, Panel add) {
        container.remove(remove);
        container.add(add, 0);
        container.validate();
        container.repaint();
    }
}