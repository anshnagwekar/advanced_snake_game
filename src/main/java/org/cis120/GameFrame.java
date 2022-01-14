package org.cis120;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class GameFrame extends JPanel {
    private final int screenWidth = 600;
    private final int screenHeight = 600;
    private final int squareUnit = 25;
    private final String[][] coords = new String[screenHeight / (squareUnit)
            + 1][screenWidth / (squareUnit) + 1];
    private int score = 0;
    private boolean isRunning;
    private final int fps = 100;
    private List<Apple> appleList;
    private List<Apple> applesToRemove;
    private List<Apple> applesToAdd;
    private Timer timer;
    private Timer subTimer = null;
    private int secondsToEnd;
    private JFrame mainFrame;
    private final String filePath = "files/leaderboard.csv";
    private final String savePath = "files/state.csv";

    private int snakeLength = 1;
    private int[] snakeX = new int[(screenWidth * screenHeight) / (squareUnit * squareUnit)];
    private int[] snakeY = new int[(screenWidth * screenHeight) / (squareUnit * squareUnit)];
    private int snakeHeadX = (screenHeight / 2) / squareUnit;
    private int snakeHeadY = (screenWidth / 2) / squareUnit;
    private int dir = 0;
    /**
     * Mapping for directions:
     * UP = 0
     * DOWN = 1
     * LEFT = 2
     * RIGHT = 3
     */
    private String snakeColor;
    private String username;

    public GameFrame() {
        System.out.println("\nNew Game has been started.");
        System.out.println("Click snake picture for instructions window!");

    }

    /**
     * Initializes game with username and color
     */
    public void initGameSettings() {
        JFrame frame = new JFrame("Game Settings");
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        frame.setPreferredSize(new Dimension(250, 150));
        JLabel label = new JLabel("Enter username:");
        JTextField userField = new JTextField("default", 15);
        JRadioButton rb1 = new JRadioButton("Red");
        JRadioButton rb2 = new JRadioButton("Green");
        JRadioButton rb3 = new JRadioButton("Blue");
        JButton b = new JButton("Confirm");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rb1);
        bg.add(rb2);
        bg.add(rb3);
        panel.add(label);
        panel.add(userField);
        panel.add(rb1);
        panel.add(rb2);
        panel.add(rb3);
        panel.add(b);
        snakeColor = "green"; // default
        b.addActionListener(e -> {
            if (rb1.isSelected()) {
                snakeColor = "red";
            }
            if (rb2.isSelected()) {
                snakeColor = "green";
            }
            if (rb3.isSelected()) {
                snakeColor = "blue";
            }
            username = userField.getText();
            frame.dispose();
            newGame();
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    /**
     * Changes delay of timer (when collided with specific apple)
     * 
     * @param ms new delay
     */
    public void changeDelay(int ms) {
        secondsToEnd = 0;
        if (subTimer != null) { // makes sure any active subTimer stops
            subTimer.stop();
        }
        timer.setDelay(ms);
        subTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                secondsToEnd += 1;
                if (secondsToEnd == 5) { // once seconds for Timer ends, stop subTimer and set delay
                                         // back to fps
                    subTimer.stop();
                    timer.setDelay(fps);
                    subTimer = null;
                }
            }
        });
        subTimer.start();
    }

    /**
     * Sets up action listener, layout, and timer for the game
     */
    public void start() {
        mainFrame = new JFrame("Snake Game");
        mainFrame.setLayout(new BorderLayout());
        JLabel user = new JLabel("Username: " + username);
        JLabel applesEatenLabel = new JLabel(
                "Number of Apples Eaten: " + Integer.toString(snakeLength - 1)
        );
        JLabel scoreLabel = new JLabel("Score: " + score);
        JPanel panel = new JPanel();
        panel.add(user);
        panel.add(Box.createRigidArea(new Dimension(15, 0)));
        panel.add(applesEatenLabel); // change layout
        panel.add(Box.createRigidArea(new Dimension(15, 0)));
        panel.add(scoreLabel); // change layout
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        mainFrame.add(this, BorderLayout.CENTER);
        mainFrame.add(panel, BorderLayout.PAGE_END);

        mainFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (dir != 3) {
                            dir = 2;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (dir != 2) {
                            dir = 3;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (dir != 0) {
                            dir = 1;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (dir != 1) {
                            dir = 0;
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        pauseGame();
                        break;
                    default:
                        break;
                }
            }
        });

        timer = new Timer(fps, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
                applesEatenLabel
                        .setText("Number of Apples Eaten: " + Integer.toString(snakeLength - 1));
                scoreLabel.setText("Score: " + score);
                repaint();
            }
        });

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
    }

    /**
     * Starts a new game by generating apples and setting snake spawn to center
     */
    public void newGame() {
        start();
        isRunning = true;
        generateApples();

        snakeX[0] = snakeHeadX * squareUnit;
        snakeY[0] = snakeHeadY * squareUnit;
        timer.start();
    }

    /**
     * Saves the state of the game to csv file
     */
    public void saveStateGame() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(savePath, false));
            bw.write("username," + username);
            bw.newLine();
            bw.write("score," + score);
            bw.newLine();
            bw.write("applesEaten," + Integer.toString(snakeLength - 1));
            bw.newLine();
            bw.write("color," + snakeColor);
            bw.newLine();
            bw.write("snakeLength," + snakeLength);
            bw.newLine();
            bw.write("snakeHeadX," + snakeHeadX);
            bw.newLine();
            bw.write("snakeHeadY," + snakeHeadY);
            bw.newLine();
            bw.write("dir," + dir);
            bw.newLine();
            String build = "snakeX,";
            for (int i : snakeX) {
                build += Integer.toString(i) + ",";
            }
            build = build.substring(0, build.length() - 1);
            bw.write(build);
            bw.newLine();
            build = "snakeY,";
            for (int i : snakeY) {
                build += Integer.toString(i) + ",";
            }
            build = build.substring(0, build.length() - 1);
            bw.write(build);
            bw.newLine();
            for (Apple a : appleList) {
                bw.write(a.toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }

    /**
     * Reads saved game from csv file and initializes + resumes game accordingly
     * 
     * @param savePath path of csv to load game from
     * @return if file is empty or IOException occuers, returns false (else true)
     */
    public boolean resumeGame(String savePath) {
        List<String[]> vals = new ArrayList<String[]>();
        try {
            FileReader fr = new FileReader(savePath);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                vals.add(values);
            }
        } catch (IOException e) {
            return false;
        }
        if (vals.size() <= 1) {
            return false;
        }

        start();
        isRunning = true;
        appleList = new ArrayList<Apple>();
        snakeX = new int[(screenWidth * screenHeight) / (squareUnit * squareUnit)];
        snakeY = new int[(screenWidth * screenHeight) / (squareUnit * squareUnit)];

        username = vals.get(0)[1];
        score = Integer.parseInt(vals.get(1)[1]);
        snakeColor = vals.get(3)[1];
        snakeLength = Integer.parseInt(vals.get(4)[1]);
        snakeHeadX = Integer.parseInt(vals.get(5)[1]);
        snakeHeadY = Integer.parseInt(vals.get(6)[1]);
        dir = Integer.parseInt(vals.get(7)[1]);
        for (int i = 1; i <= snakeX.length; i++) {
            snakeX[i - 1] = Integer.parseInt(vals.get(8)[i]);
        }
        for (int i = 1; i <= snakeY.length; i++) {
            snakeY[i - 1] = Integer.parseInt(vals.get(9)[i]);
        }
        for (int i = 10; i < vals.size(); i++) {
            String label = vals.get(i)[1];
            int x = Integer.parseInt(vals.get(i)[2]);
            int y = Integer.parseInt(vals.get(i)[3]);
            switch (label) {
                case "GREEN":
                    appleList.add(new GreenApple(this, label, x, y));
                    break;
                case "BLUE":
                    appleList.add(new BlueApple(this, label, x, y));
                    break;
                case "RED":
                    appleList.add(new RedApple(this, label, x, y));
                    break;
                case "BROWN":
                    appleList.add(new BrownApple(this, label, x, y));
                    break;
                default:
                    break;
            }
        }

        for (String[] s : coords) {
            Arrays.fill(s, "");
        }
        for (Apple a : appleList) {

            coords[a.getxCoord()][a.getyCoord()] = a.getColor();
            System.out.println(a.getColor() + ": " + a.getxCoord() + "," + a.getyCoord());
        }
        timer.start();
        return true;
    }

    /**
     * Displays the pause JFrame and presents different options to users
     */
    public void pauseGame() {
        mainFrame.dispose();
        timer.stop();
        mainFrame = new JFrame("Game Paused");
        JPanel panel = new JPanel(new GridLayout(0, 3));
        JButton playAgain = new JButton("Start Again");
        playAgain.addActionListener(e -> {
            mainFrame.dispose();
            clearSaveState();
            Runnable game = new AdvancedSnakeGame();
            SwingUtilities.invokeLater(game);
        });
        JButton resume = new JButton("Resume");
        resume.addActionListener(e -> {
            mainFrame.dispose();
            saveStateGame();
            resumeGame(savePath);
        });
        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            mainFrame.dispose();
            saveStateGame();
            System.exit(0);
        });

        panel.add(resume);
        panel.add(playAgain);
        panel.add(quit);

        mainFrame.add(panel);
        mainFrame.setPreferredSize(new Dimension(300, 100));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);

    }

    /**
     * If the game ends or user wants to start again, clear the saved game state
     */
    public void clearSaveState() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(savePath, false));
            bw.write("");
            bw.close();
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }

    /**
     * Handles movement for snake
     */
    public void moveSnake() {
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (dir) {
            case 1:
                snakeY[0] = snakeY[0] - squareUnit;
                snakeHeadY -= 1;
                break;
            case 0:
                snakeY[0] = snakeY[0] + squareUnit;
                snakeHeadY += 1;
                break;
            case 2:
                snakeX[0] = snakeX[0] - squareUnit;
                snakeHeadX -= 1;
                break;
            case 3:
                snakeX[0] = snakeX[0] + squareUnit;
                snakeHeadX += 1;
                break;
            default:
                break;
        }

    }

    /**
     * Adds apple to list of apples to remove at end of current tick()
     * 
     * @param a Apple object
     */
    public void addAppleToRemove(Apple a) {
        if (a != null) {
            applesToRemove.add(a);
        }
    }

    /**
     * Adds apple to list of apples to add at end of current tick()
     * 
     * @param a Apple object
     */
    public void addApplesToAdd(Apple a) {
        if (a != null) {
            applesToAdd.add(a);
        }
    }

    /**
     * Called during every tick of the timer
     * Checks for game ending events, collisions, and moves the snake.
     * If game ends, then displays end game screen.
     */
    public void tick() {
        if (isRunning) {
            checkGameEndingEvents();
            checkAppleCollisions();
            moveSnake();
        }
        if (!isRunning) {
            timer.stop();
            endGameScreen();
        }

    }

    /**
     * End game screen, presents user option to record score in leaderboard, play
     * again, or quit.
     */
    public void endGameScreen() {
        mainFrame.dispose();
        clearSaveState();
        mainFrame = new JFrame("Game Over!");
        JPanel panel2 = new JPanel(new GridLayout(0, 3));
        JButton playAgain = new JButton("Play Again");
        playAgain.addActionListener(e -> {
            mainFrame.dispose();
            Runnable game = new AdvancedSnakeGame();
            SwingUtilities.invokeLater(game);
        });
        JButton record = new JButton("Record Entry");
        record.addActionListener(e -> {
            mainFrame.dispose();
            if (addEntryToCSV(
                    filePath, username, Integer.toString(snakeLength - 1), Integer.toString(score),
                    snakeColor
            )) {
                Leaderboard leaderboard = new Leaderboard(filePath);
                leaderboard.display();
            } else {
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(
                        frame, "An error has occurred when reading the leaderboard file. " +
                                "Please contact your administrator. "
                );
                frame.dispose();
                Runnable game = new AdvancedSnakeGame();
                SwingUtilities.invokeLater(game);
            }
        });
        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            mainFrame.dispose();
            System.exit(0);
        });
        panel2.add(playAgain);
        panel2.add(record);
        panel2.add(quit);

        JPanel panel1 = new JPanel(new GridLayout(3, 0));
        JLabel userL = new JLabel("Your username was " + username + ".");
        JLabel scoreL = new JLabel("You scored " + score + " points.");
        JLabel appleL = new JLabel("You ate " + Integer.toString(snakeLength - 1) + " apples.");
        panel1.add(userL);
        panel1.add(scoreL);
        panel1.add(appleL);

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(panel1);
        mainPanel.add(panel2);

        mainFrame.add(mainPanel);
        mainFrame.setPreferredSize(new Dimension(300, 150));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);

    }

    /**
     * Adds the current game statistics to the leaderboard file
     * 
     * @param filePath    leaderboard csv file path
     * @param username
     * @param applesEaten
     * @param score
     * @param color
     * @return
     */
    public boolean addEntryToCSV(
            String filePath, String username, String applesEaten, String score, String color
    ) {
        File file = Paths.get(filePath).toFile();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(username + "," + score + "," + applesEaten + "," + color);
            bw.newLine();
            bw.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks to see if any apples and snakes have collided.
     * If so, calls onCollision() method of respective apple and ensures that no
     * apples overlap in grid
     * Also removes/add apples after iterating through list in order to prevent
     * ConcurrentModification errors.
     */
    public void checkAppleCollisions() {
        applesToRemove = new ArrayList<Apple>();
        applesToAdd = new ArrayList<Apple>();
        for (Apple a : appleList) {
            boolean greenAppleCheck1 = (a instanceof GreenApple) &&
                    (snakeHeadY == a.getyCoord())
                    && (a.getxCoord() == snakeHeadX || a.getxCoord() == snakeHeadX - 1);
            boolean greenAppleCheck2 = (a instanceof GreenApple) &&
                    (snakeHeadX == a.getxCoord())
                    && (a.getyCoord() == snakeHeadY || a.getyCoord() == snakeHeadY - 1);
            if ((a.getyCoord() == snakeHeadY && a.getxCoord() == snakeHeadX) || greenAppleCheck1
                    || greenAppleCheck2) {
                coords[a.getxCoord()][a.getyCoord()] = "";
                System.out.println(
                        "[OLD] " + a.getColor() + ": " + a.getxCoord() + "," + a.getyCoord()
                );
                a.onCollision();
                System.out.println(
                        "[NEW] " + a.getColor() + ": " + a.getxCoord() + "," + a.getyCoord()
                );
                // ensure no apples are overlapping on grid
                if (coords[a.getxCoord()][a.getyCoord()].equals("")) {
                    coords[a.getxCoord()][a.getyCoord()] = a.getColor();
                } else {
                    while (!coords[a.getxCoord()][a.getyCoord()].equals("")) {
                        a.getNewCoords();
                        if (coords[a.getxCoord()][a.getyCoord()].equals("")) {
                            coords[a.getxCoord()][a.getyCoord()] = a.getColor();
                            break;
                        }
                    }
                }
                snakeLength++;
                System.out.println("Score: " + score);
            }
        }
        if (!applesToRemove.isEmpty()) {
            appleList.removeAll(applesToRemove);
        }
        if (!applesToAdd.isEmpty()) {
            for (Apple a : applesToAdd) {
                addApple(a);
            }
        }
    }

    /**
     * Checks for game ending events - hitting walls or own body
     */
    public void checkGameEndingEvents() {
        // checks if head touches any part of the pody
        for (int i = 1; i <= snakeLength; i++) {
            if ((snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                isRunning = false;
                System.out.println("Hit your own body!");
            }
        }
        // check if head touches left border or right border
        if (snakeHeadX < 0 || snakeHeadX > getGameWidth() - 1) {
            isRunning = false;
            System.out.println("Hit left/right edge!");
        }
        // check if head touches top or bottom border
        if (snakeHeadY < 0 || snakeHeadY > getGameHeight() - 1) {
            isRunning = false;
            System.out.println("Hit top/bottom edge!");
        }
    }

    /**
     * Generates apples for a new game and ensures none of the apples overlap
     */
    public void generateApples() {
        for (String[] s : coords) {
            Arrays.fill(s, "");
        }

        appleList = new ArrayList<Apple>();
        appleList.add(new RedApple(this));
        appleList.add(new RedApple(this));
        appleList.add(new RedApple(this));
        appleList.add(new BrownApple(this));
        appleList.add(new BrownApple(this));
        appleList.add(new BrownApple(this));
        appleList.add(new BrownApple(this));
        appleList.add(new GreenApple(this));
        appleList.add(new GreenApple(this));
        appleList.add(new GreenApple(this));
        appleList.add(new GreenApple(this));
        appleList.add(new BlueApple(this));

        for (Apple a : appleList) {
            while (coords[a.getxCoord()][a.getyCoord()].equals("")) {
                if (coords[a.getxCoord()][a.getyCoord()].equals("")) {
                    coords[a.getxCoord()][a.getyCoord()] = a.getColor();
                } else {
                    a.getNewCoords();
                }
            }
            System.out.println(a.getColor() + ": " + a.getxCoord() + "," + a.getyCoord());
        }
        this.repaint();
    }

    /**
     * Adds apple to the appleList. Ensures that coordinates do not overlap
     * 
     * @param a Apple object
     */
    public void addApple(Apple a) {
        appleList.add(a);
        while (coords[a.getxCoord()][a.getyCoord()].equals("")) {
            if (coords[a.getxCoord()][a.getyCoord()].equals("")) {
                coords[a.getxCoord()][a.getyCoord()] = a.getColor();
            } else {
                a.getNewCoords();
            }
        }
        System.out
                .println("NEW APPLE " + a.getColor() + ": " + a.getxCoord() + "," + a.getyCoord());
    }

    /**
     * Finds arbitrary apple of a specified color to remove. Returns the apple.
     * 
     * @param appleColor color of apple to remove
     */
    public Apple removeApple(String appleColor) {
        Apple appleToRemove = null;
        for (Apple a : appleList) {
            if (a.getColor().equals(appleColor)) {
                appleToRemove = a;
            }
        }
        if (appleToRemove != null) {
            System.out.println(
                    "APPLE TO REMOVE AT: " + appleToRemove.getxCoord() + ","
                            + appleToRemove.getyCoord()
            );
        } else {
            System.out.println("NO APPLE OF " + appleColor + " TO REMOVE!");
        }
        return appleToRemove;
    }

    /**
     * Paints all the apples and snake body if game is running.
     * 
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isRunning) {
            for (Apple a : appleList) {
                a.drawApple(g);
            }

            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.PINK);
                    if (dir == 0) {
                        g.fillRect(
                                snakeX[i], snakeY[i], squareUnit, squareUnit - (squareUnit / 2)
                        );
                    }
                    if (dir == 1) {
                        g.fillRect(
                                snakeX[i], snakeY[i] + (squareUnit / 2), squareUnit,
                                squareUnit - (squareUnit / 2)
                        );
                    }
                    if (dir == 3) {
                        g.fillRect(
                                snakeX[i], snakeY[i], squareUnit - (squareUnit / 2), squareUnit
                        );
                    }
                    if (dir == 2) {
                        g.fillRect(
                                snakeX[i] + (squareUnit / 2), snakeY[i],
                                squareUnit - (squareUnit / 2), squareUnit
                        );
                    }
                    g.fillOval(snakeX[i], snakeY[i], squareUnit, squareUnit);
                } else {
                    if (snakeColor.equals("red")) {
                        g.setColor(new Color((int) (150 * Math.random()) + 50, 0, 0));
                    }
                    if (snakeColor.equals("green")) {
                        g.setColor(new Color(0, (int) (150 * Math.random()) + 50, 0));
                    }
                    if (snakeColor.equals("blue")) {
                        g.setColor(new Color(0, 0, (int) (150 * Math.random()) + 50));
                    }
                    g.fillRect(snakeX[i], snakeY[i], squareUnit, squareUnit);
                }
            }
        } else {
            System.out.println("End Game");
        }

    }

    /**
     * Incremements score by specified increment. Ensures no negative scores.
     * 
     * @param incr increment
     */
    public void incrementScore(int incr) {
        score += incr;
        if (score < 0) {
            score = 0;
        }
    }

    public int getGameWidth() {
        return screenWidth / squareUnit;
    }

    public int getGameHeight() {
        return screenHeight / squareUnit;
    }

    public int getSquare() {
        return squareUnit;
    }
}
