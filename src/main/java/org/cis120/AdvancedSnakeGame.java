package org.cis120;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AdvancedSnakeGame implements Runnable {

    private JFrame frame = new JFrame("Ansh's Crazy Snake");
    private JFrame instructFrame = null;
    private GameFrame game;
    private Leaderboard leaderboard;
    private BufferedImage img;
    private final String imageFile = "files/snake.jpeg";
    private final String savePath = "files/state.csv";
    private final String filePath = "files/leaderboard.csv";

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // Create a panel to store the two components
        // and make this panel the contentPane of the frame
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        try {
            if (img == null) {
                img = ImageIO.read(new File(imageFile));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }

        JButton instructions = new JButton(new ImageIcon(img));
        panel.add(instructions);

        JButton startButton = new JButton("Play");
        panel.add(startButton);

        JButton resumeButton = new JButton("Load");
        panel.add(resumeButton);
        game = new GameFrame();

        JButton leaderBoardButton = new JButton("Leaderboard");
        panel.add(leaderBoardButton);
        leaderboard = new Leaderboard(filePath);

        startButton.addActionListener(e -> {
            frame.dispose();
            game.initGameSettings();
        });
        resumeButton.addActionListener(e -> {
            frame.dispose();
            if (!game.resumeGame(savePath)) {
                JOptionPane.showMessageDialog(frame, "There is no saved game on the computer!");
                frame.dispose();
                Runnable game = new AdvancedSnakeGame();
                SwingUtilities.invokeLater(game);
            }
        });
        leaderBoardButton.addActionListener(e -> {
            frame.dispose();
            leaderboard.display();
        });
        instructions.addActionListener(e -> {
            String instruct = "Instructions for how to play Ansh's Crazy Snake";
            String instruct2 = "This is the classic snake game with a few crazy twists!" +
                    " First off, the goal"
                    +
                    " remains the same: score as many points while staying alive! Eating" +
                    " apples will increase "
                    +
                    " your score and increase the length of your snake. There are " +
                    "4 different types"
                    +
                    " of apples: ";
            String apple1 = "The red apple is your standard apple. It lengthens your " +
                    "snake by 1 and adds 1 to your "
                    +
                    "score. However, every time you eat a red apple, there is a 50% " +
                    "chance that you spawn a brown"
                    +
                    "apple into the game.";
            String apple2 = "The green apple is a bonus apple. It moves around the " +
                    "screen, either horizontally "
                    +
                    "or vertically. Since it is harder to eat, it adds 2 to your " +
                    "score and only lengthens your "
                    +
                    " snake by 1. Additionally, every time you eat a green apple," +
                    " there is a 25% chance that you "
                    +
                    "remove an existing (if any) brown apples. ";
            String apple3 = "The blue apple is a freeze apple. It is stationary, " +
                    "but if you eat the apple, it will"
                    +
                    " slow the game down for 5 seconds. It lengthens your snake by " +
                    "1 and adds 1 to your score. It "
                    +
                    " should be used to negate the effects of the brown apple or " +
                    "to make it easier to catch green"
                    +
                    " apples.";
            String apple4 = "The brown apple is the rotten apple. It is stationary," +
                    " but if you eat the apple, it"
                    +
                    " will speed up the game for 5 seconds. It lengthens your snake " +
                    "by 1 and DECREASES your score"
                    +
                    " by 1. Do your best to avoid these apples!";
            String movement = "Movement follows the same rule as regular Snake. " +
                    "Use the arrow keys to control your "
                    +
                    "snake (up is north, left is west, etc.) and press RETURN/ENTER to " +
                    "pause your game. You "
                    +
                    " can always resume your game, but if you would like to play " +
                    "the game later, you can press "
                    +
                    "Quit and it will automatically save your game. Then, press " +
                    "Load to open the saved game.";
            String endGame = "The game will be over if your snake dies. Your snake " +
                    "dies if it hits the borders or "
                    +
                    " if it accidentally eats any part of its own body.";
            String leader = "After the game is finished, you have the option to " +
                    "record your results to the "
                    +
                    " leaderboard to see how you did compared to others! If you " +
                    "would like to do so, press Record "
                    +
                    "once your game is over. If not, then press Quit or Play " +
                    "Again if you would like to do so!";
            final String instructionString = "<p>" + instruct2 + "</p><ul><li>" + apple1
                    + "</li><li>" + apple2 + "</li><li>" +
                    apple3 + "</li><li>" + apple4 + "</li></ul><p>" + movement + "</p><br><p>"
                    + endGame + "</p><br><p>" + leader;
            final String html = "<html><body style='width: 500'>" + instructionString
                    + "</body></html>";
            JOptionPane.showMessageDialog(
                    null, html, instruct, JOptionPane.INFORMATION_MESSAGE
            );
        });

        frame.setPreferredSize(new Dimension(300, 275));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
