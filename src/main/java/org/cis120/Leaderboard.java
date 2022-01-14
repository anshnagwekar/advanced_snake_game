package org.cis120;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Reads leaderboard data through CSV file
 * and creates way to format org.cis120.Leaderboard
 */
public class Leaderboard extends JPanel {
    private BufferedReader br;
    private String filePath;
    private List<String[]> leaderboardVals;

    public Leaderboard(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads leaderboard csv file and initializes values
     * 
     * @return boolean value for if file read successfully
     */
    public boolean initializeLeaderBoard() {
        leaderboardVals = new ArrayList<String[]>();
        try {
            FileReader fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                leaderboardVals.add(values);
            }
        } catch (IOException e) {
            return false;
        }
        return true;

    }

    /**
     * Graphics for leaderboard
     * Uses Arrays.sort() with a Comparator object to sort the leaderboard entries
     */
    public void display() {
        JFrame frame = new JFrame("Leaderboard");
        frame.getContentPane().add(this);
        if (initializeLeaderBoard()) {
            String[][] vals = new String[leaderboardVals.size()][leaderboardVals.get(0).length];
            vals = leaderboardVals.toArray(vals);
            Arrays.sort(vals, new Comparator<String[]>() {
                public int compare(String[] a, String[] b) {
                    if (Integer.parseInt(a[1]) > Integer.parseInt(b[1])) {
                        return -1;
                    }
                    return 1;
                }
            });
            String[] colNames = { "Name", "Score", "Apples Eaten", "Snake Color" };
            JTable table = new JTable(vals, colNames);
            table.setDefaultEditor(Object.class, null);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            frame.add(scrollPane);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        } else {
            JOptionPane.showMessageDialog(
                    frame, "An error has occurred when reading the leaderboard file. " +
                            "Please contact your administrator. "
            );
            frame.dispose();
            Runnable game = new AdvancedSnakeGame();
            SwingUtilities.invokeLater(game);
        }
    }
}
