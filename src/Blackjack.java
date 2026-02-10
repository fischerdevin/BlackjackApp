import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Scanner;


public class Blackjack {
    public static void main(String[] args) throws InterruptedException {

        // For the pop up application
        //Still deciding if i want to stay on terminal or move this way
        Frame frame = new Frame("Black Jack");
        Button hitBtn = new Button("Hit");
        Button standBtn = new Button("Stand");
        Button doubleBtn = new Button("Double");



        frame.setLayout(new BorderLayout());

        // These are like divs/section
        Panel dealerPanel = new Panel();
        Panel playerPanel = new Panel();
        Panel BottomContainer = new Panel();
        Panel optionPanel = new Panel();
        Panel bankPanel = new Panel();

        BottomContainer.setLayout(new GridLayout(2,1));

        BottomContainer.setPreferredSize(new Dimension(300,100));
        dealerPanel.setPreferredSize(new Dimension(300, 150));
        playerPanel.setPreferredSize(new Dimension(300, 150));

        optionPanel.setPreferredSize(new Dimension(300, 80));
        bankPanel.setPreferredSize(new Dimension(300, 100));

        Label dealerLabel = new Label("Dealer Hand");
        Label dealerTotalLabel = new Label("0");
        Label playerLabel = new Label("Player Hand");
        Label playerTotalLabel = new Label("0");
        Label bankLabel = new Label("Bank: $500");

        // This is used for messages like !enough money to bet
        Label msgLabel = new Label("");
        msgLabel.setForeground(Color.RED);

        dealerLabel.setAlignment(Label.CENTER);
        playerLabel.setAlignment(Label.CENTER);
        bankLabel.setAlignment(Label.RIGHT);

        bankPanel.setLayout(new BorderLayout());
        bankPanel.add(bankLabel, BorderLayout.EAST);
        bankPanel.add(msgLabel, BorderLayout.WEST);


        optionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        optionPanel.add(hitBtn);
        optionPanel.add(standBtn);
        optionPanel.add(doubleBtn);

        dealerPanel.add(dealerLabel);
        playerPanel.add(playerLabel);
        BottomContainer.add(optionPanel);
        BottomContainer.add(bankPanel);

        frame.add(dealerPanel, BorderLayout.NORTH);
        frame.add(playerPanel, BorderLayout.CENTER);
        frame.add(BottomContainer, BorderLayout.SOUTH);



        frame.pack();
        frame.setVisible(true);


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        int numDecks = 6;
        Scanner scnr = new Scanner(System.in);

        System.out.println("How many Decks would you like to play with 2,4,6 ?");
        numDecks = scnr.nextInt();


        String[] suits = {"♦", "♠", "♥", "♣"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        int numCardsPerDeck = suits.length * ranks.length;
        int numCards = numCardsPerDeck * numDecks;

        int[][] deck = new int[numCards][2];


        // Creates a deck of cards with number of decks = numDecks
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

        // Shuffle Decks Fisher-yates algo
        Random r = new Random();
        for (int i = numCards - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);

            int tempSuit = deck[i][0];
            int tempRank = deck[i][1];

            deck[i][0] = deck[j][0];
            deck[i][1] = deck[j][1];

            deck[j][0] = tempSuit;
            deck[j][1] = tempRank;

        }

        // Array for Player and Dealer
        int MAX_HAND = 12;
        int[][] playerHand = new int[MAX_HAND][2];
        int[][] dealerHand = new int[MAX_HAND][2];

        int bankAccount = 500;
        int bet = 0;

        int playerCount, dealerCount;
        int currentCard = 0;

        while (currentCard < numCards * 0.75) {

            if (bankAccount <= 0) {
                System.out.println("You are out of money");
                break;
            }

            playerCount = 0;
            dealerCount = 0;

            do {
                System.out.println("Bet Amount: ");
                bet = scnr.nextInt();

                if (bet > bankAccount) {
                    System.out.println("Do not have enough");
                } else if (bet <= 0) {
                    System.out.println("Bet must be greater than 0");
                }
            } while (bet > bankAccount || bet <= 0);

            if (currentCard >= numCards) break;

            playerHand[playerCount++] = deck[currentCard++];
            dealerHand[dealerCount++] = deck[currentCard++];
            playerHand[playerCount++] = deck[currentCard++];
            dealerHand[dealerCount++] = deck[currentCard++];

            int playerTotal = getHandValue(playerHand, playerCount, ranks);
            int dealerTotal = getHandValue(dealerHand, dealerCount, ranks);

            boolean playerBJ = (playerCount == 2 && playerTotal == 21);
            boolean dealerBJ = (dealerCount == 2 && dealerTotal == 21);
            boolean dealerAceShowing = ranks[dealerHand[0][1]].equals("A");


            if (playerBJ || dealerBJ) {
                System.out.println("Initial hands:");
                System.out.println("Player hand: " + suits[playerHand[0][0]] + ranks[playerHand[0][1]] + " " +
                        suits[playerHand[1][0]] + ranks[playerHand[1][1]]);
                System.out.println("Dealer hand: " + suits[dealerHand[0][0]] + ranks[dealerHand[0][1]] + " " +
                        suits[dealerHand[1][0]] + ranks[dealerHand[1][1]]);
                if (playerBJ && dealerAceShowing && !dealerBJ) {
                    System.out.println("Even money: (y/n)");
                    boolean evenMoney = scnr.next().equalsIgnoreCase("y");

                    if (evenMoney) {
                        System.out.println("Ez Win!");
                        bankAccount += bet;
                        continue;
                    }

                }

                if (!playerBJ && dealerAceShowing) {
                    System.out.println("Insurance: y/n half your bet");
                    boolean insurance = scnr.next().equalsIgnoreCase("y");
                    int insuranceBet = bet / 2;

                    if (insurance) {
                        bankAccount -= insuranceBet;

                        if (dealerBJ) {
                            System.out.println("Dealer BJ! Insurance Pays " + insuranceBet * 2);
                            bankAccount += insuranceBet * 2;
                            bankAccount -= bet;
                            continue;
                        } else {
                            System.out.println("Dealer no BJ... Insurance Lost");
                        }
                    }

                    if (!insurance && dealerBJ) {
                        System.out.println("Dealer BJ");
                        bankAccount -= bet;
                        continue;
                    }
                }

                if (playerBJ && dealerBJ) {
                    System.out.println("Push! Both Bj");
                } else if (dealerBJ && !dealerAceShowing) {
                    System.out.println("Dealer BJ");
                    bankAccount -= bet;
                } else if (playerBJ) {
                    System.out.println("BJ!");
                    bankAccount += (int) (1.5 * bet);
                }
                continue;
            }
            for (int i = 0; i < playerCount; i++) {
                System.out.print(
                        suits[playerHand[i][0]] + " " + ranks[playerHand[i][1]] + " "
                );

            }

            System.out.print("\nYou Have: " + playerTotal);
            Thread.sleep(1500);


            System.out.println("\nDealer hand:");
            System.out.println(
                    suits[dealerHand[0][0]] + " " + ranks[dealerHand[0][1]] + " " + "\uD83C\uDCA0"
            );

            boolean isActive = true;
            while (isActive) {
                if (playerTotal > 21) {
                    System.out.println("Player Busted with " + playerTotal + "!");
                    isActive = false;
                    break;
                }

                System.out.print("Hit, Stand, or Double (h,s,d): ");
                String choice = scnr.next();

                if (choice.equalsIgnoreCase("d")) {
                    if (bet * 2 <= bankAccount) {
                        bet *= 2; // double the bet

                        playerHand[playerCount++] = deck[currentCard++];
                        playerTotal = getHandValue(playerHand, playerCount, ranks);

                        System.out.println("You Double Down and draw: " +
                                suits[playerHand[playerCount - 1][0]] + " " +
                                ranks[playerHand[playerCount - 1][1]]);
                        System.out.println("Your Total is now: " + playerTotal);

                        // Double down forces stand
                        isActive = false;
                        Thread.sleep(1000);
                    } else {
                        System.out.println("Not enough money to double down!");
                    }
                } else if (choice.equalsIgnoreCase("h")) {
                    playerHand[playerCount++] = deck[currentCard++];
                    playerTotal = getHandValue(playerHand, playerCount, ranks);

                    System.out.println("You Draw: " +
                            suits[playerHand[playerCount - 1][0]] + " " +
                            ranks[playerHand[playerCount - 1][1]]);
                    System.out.println("Your Total is now: " + playerTotal);
                } else {
                    isActive = false; // stand
                }
            }

            boolean playerBusted = playerTotal > 21;

            System.out.println("\nDealer shows:");
            System.out.println(suits[dealerHand[0][0]] + " " + ranks[dealerHand[0][1]] + " " + suits[dealerHand[1][0]] + " " + ranks[dealerHand[1][1]]);

            if (!playerBusted) {
                while (dealerTotal < 17 || isSoft17(dealerHand, dealerCount, ranks)) {
                    dealerHand[dealerCount++] = deck[currentCard++];
                    dealerTotal = getHandValue(dealerHand, dealerCount, ranks);

                    System.out.println("Dealer Draws: " +
                            suits[dealerHand[dealerCount - 1][0]] + " " +
                            ranks[dealerHand[dealerCount - 1][1]]
                    );
                    System.out.println("Dealers Total is now: " + dealerTotal);
                }
            }

            boolean dealerBusted = dealerTotal > 21;

            if (playerBusted) {
                System.out.println("You Busted! Dealer Won :( ");
                bankAccount -= bet;
            } else if (dealerBusted) {
                System.out.println("You Won! Dealer Busted :) ");
                bankAccount += bet;
            } else {
                if (playerTotal > dealerTotal) {
                    System.out.println("You Win!");
                    bankAccount += bet;
                } else if (playerTotal < dealerTotal) {
                    System.out.println("Dealer Wins");
                    bankAccount -= bet;
                } else {
                    System.out.println("Push");
                }
            }
        }
    }


    // Gets Cards in hand and adds up total value
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


    // Checks dealer for soft 17 to continue hitting until hard 17
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

    // This is msgUpdater function
    static void setLabel(Label label, String text) {
        EventQueue.invokeLater(() -> label.setText(text));
    }


}