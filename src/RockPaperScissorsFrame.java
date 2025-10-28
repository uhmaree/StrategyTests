import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {
    private JTextField playerWinsField, computerWinsField, tiesField;
    private JTextArea resultsArea;
    private int playerWins = 0, computerWins = 0, ties = 0;
    private int rockCount = 0, paperCount = 0, scissorsCount = 0;
    private String lastPlayerMove = "";

    private Strategy cheat = new Cheat(); // external
    private Strategy random = new RandomStrategy(); // external
    private Random rand = new Random();

    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel();
        JButton rockBtn = new JButton("Rock");
        JButton paperBtn = new JButton("Paper");
        JButton scissorsBtn = new JButton("Scissors");
        JButton quitBtn = new JButton("Quit");

        buttonPanel.add(rockBtn);
        buttonPanel.add(paperBtn);
        buttonPanel.add(scissorsBtn);
        buttonPanel.add(quitBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // --- Stats Panel ---
        JPanel statsPanel = new JPanel(new GridLayout(3, 2));
        statsPanel.add(new JLabel("Player Wins:"));
        playerWinsField = new JTextField("0", 5);
        playerWinsField.setEditable(false);
        statsPanel.add(playerWinsField);

        statsPanel.add(new JLabel("Computer Wins:"));
        computerWinsField = new JTextField("0", 5);
        computerWinsField.setEditable(false);
        statsPanel.add(computerWinsField);

        statsPanel.add(new JLabel("Ties:"));
        tiesField = new JTextField("0", 5);
        tiesField.setEditable(false);
        statsPanel.add(tiesField);

        add(statsPanel, BorderLayout.WEST);

        // --- Results Area ---
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        // --- Button Actions ---
        ActionListener listener = e -> {
            String playerMove = "";
            if (e.getSource() == rockBtn) playerMove = "R";
            else if (e.getSource() == paperBtn) playerMove = "P";
            else if (e.getSource() == scissorsBtn) playerMove = "S";
            else if (e.getSource() == quitBtn) System.exit(0);

            playRound(playerMove);
        };

        rockBtn.addActionListener(listener);
        paperBtn.addActionListener(listener);
        scissorsBtn.addActionListener(listener);
        quitBtn.addActionListener(listener);
    }

    private void playRound(String playerMove) {
        // Track counts
        switch (playerMove) {
            case "R": rockCount++; break;
            case "P": paperCount++; break;
            case "S": scissorsCount++; break;
        }

        // Pick strategy based on probability
        int prob = rand.nextInt(100) + 1;
        Strategy strategy;
        String strategyName;

        if (prob <= 10) { strategy = cheat; strategyName = "Cheat"; }
        else if (prob <= 30) { strategy = new LeastUsed(); strategyName = "Least Used"; }
        else if (prob <= 50) { strategy = new MostUsed(); strategyName = "Most Used"; }
        else if (prob <= 70) { strategy = new LastUsed(); strategyName = "Last Used"; }
        else { strategy = random; strategyName = "Random"; }

        String computerMove = strategy.getMove(playerMove);
        String result = determineWinner(playerMove, computerMove);

        resultsArea.append(result + " (Computer: " + strategyName + ")\n");
        updateStats();
        lastPlayerMove = playerMove;
    }

    private String determineWinner(String player, String computer) {
        if (player.equals(computer)) {
            ties++;
            return "Tie! Both chose " + player;
        }
        if ((player.equals("R") && computer.equals("S")) ||
                (player.equals("P") && computer.equals("R")) ||
                (player.equals("S") && computer.equals("P"))) {
            playerWins++;
            return moveName(player) + " beats " + moveName(computer) + " (Player wins)";
        } else {
            computerWins++;
            return moveName(computer) + " beats " + moveName(player) + " (Computer wins)";
        }
    }

    private String moveName(String move) {
        switch (move) {
            case "R": return "Rock";
            case "P": return "Paper";
            case "S": return "Scissors";
            default: return "?";
        }
    }

    private void updateStats() {
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    // --- Inner Strategies ---
    private class LeastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (rockCount <= paperCount && rockCount <= scissorsCount) return "P"; // beats Rock
            if (paperCount <= rockCount && paperCount <= scissorsCount) return "S"; // beats Paper
            return "R"; // beats Scissors
        }
    }

    private class MostUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (rockCount >= paperCount && rockCount >= scissorsCount) return "P"; // beats Rock
            if (paperCount >= rockCount && paperCount >= scissorsCount) return "S"; // beats Paper
            return "R"; // beats Scissors
        }
    }

    private class LastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (lastPlayerMove.isEmpty()) return random.getMove(playerMove);
            switch (lastPlayerMove) {
                case "R": return "P";
                case "P": return "S";
                case "S": return "R";
                default: return random.getMove(playerMove);
            }
        }
    }
}