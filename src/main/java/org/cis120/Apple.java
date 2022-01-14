package org.cis120;

import java.awt.*;

public abstract class Apple {
    protected Color color = Color.WHITE; // default
    protected int incr;
    protected int xCoord;
    protected int yCoord;
    protected GameFrame game;
    protected String label;

    public Apple(GameFrame g) {
        game = g;
        getNewCoords();
    }

    public Apple(GameFrame g, String label, int x, int y) {
        game = g;
        xCoord = x;
        yCoord = y;
        this.label = label;
    }

    public void onCollision() {
        game.incrementScore(incr);
        getNewCoords();
    }

    public void drawApple(Graphics g) {
        g.setColor(color);
        g.drawOval(
                game.getSquare() * xCoord, game.getSquare() * yCoord,
                game.getSquare(), game.getSquare()
        );
        g.fillOval(
                game.getSquare() * xCoord, game.getSquare() * yCoord,
                game.getSquare(), game.getSquare()
        );
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public String getColor() {
        return label;
    }

    /**
     * Uses Math.random() to generate new coords and ensures that coords are within
     * bounds.
     */
    public void getNewCoords() {
        xCoord = (int) (Math.random() * game.getGameWidth() - 2);
        yCoord = (int) (Math.random() * game.getGameHeight() - 2);
        if (xCoord < 1) {
            xCoord = 1;
        }
        if (yCoord < 1) {
            yCoord = 1;
        }
    }

    @Override
    public String toString() {
        return "Apple," + label + "," + getxCoord() + "," + getyCoord();
    }

}

class RedApple extends Apple {

    public RedApple(GameFrame g) {
        super(g);
        this.incr = 1;
        this.color = Color.RED;
        this.label = "RED";
    }

    public RedApple(GameFrame g, String label, int x, int y) {
        super(g, label, x, y);
        incr = 1;
        color = Color.RED;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        double chance = Math.random();
        if (chance < 0.5) {
            // 50% chance that a brown apple is added by eating a red apple!
            game.addApplesToAdd(new BrownApple(game));
        }

    }
}

class BlueApple extends Apple {

    public BlueApple(GameFrame g) {
        super(g);
        incr = 1;
        color = Color.BLUE;
        this.label = "BLUE";
    }

    public BlueApple(GameFrame g, String label, int x, int y) {
        super(g, label, x, y);
        incr = 1;
        color = Color.BLUE;
    }

    @Override
    public void onCollision() {
        super.onCollision();
        game.changeDelay(200);
    }
}

class BrownApple extends Apple {

    public BrownApple(GameFrame g) {
        super(g);
        incr = -1;
        color = new Color(102, 51, 0);
        this.label = "BROWN";
    }

    public BrownApple(GameFrame g, String label, int x, int y) {
        super(g, label, x, y);
        incr = -1;
        color = new Color(102, 51, 0);
    }

    @Override
    public void onCollision() {
        super.onCollision();
        game.changeDelay(60);
    }
}

class GreenApple extends Apple {
    private double direction;

    public GreenApple(GameFrame g) {
        super(g);
        incr = 2;
        color = Color.GREEN;
        this.label = "GREEN";
        direction = Math.random();
    }

    public GreenApple(GameFrame g, String label, int x, int y) {
        super(g, label, x, y);
        incr = 2;
        color = Color.GREEN;
        direction = Math.random();
    }

    @Override
    public void onCollision() {
        super.onCollision();
        direction = Math.random();
        double chance = Math.random();
        if (chance < 0.25) {
            // 25% chance that a brown apple is removed by eating a green apple!
            game.addAppleToRemove(game.removeApple("BROWN"));
        }
    }

    /**
     * Checks direction of green apple with Math.random() and draws apple
     * accordingly
     * 
     * @param g Graphics object
     */
    @Override
    public void drawApple(Graphics g) {
        if (direction < 0.25) {
            this.xCoord += 1;
        } else if (direction < 0.5) {
            this.xCoord -= 1;
        } else if (direction < 0.75) {
            this.yCoord -= 1;
        } else {
            this.yCoord += 1;
        }

        if (this.xCoord > game.getGameWidth()) {
            this.xCoord = 0;
        }
        if (this.xCoord < 0) {
            this.xCoord = game.getGameWidth();
        }
        if (this.yCoord > game.getGameHeight()) {
            this.yCoord = 0;
        }
        if (this.yCoord < 0) {
            this.yCoord = game.getGameHeight();
        }

        g.setColor(color);
        g.drawOval(
                game.getSquare() * xCoord, game.getSquare() * yCoord,
                game.getSquare(), game.getSquare()
        );
        g.fillOval(
                game.getSquare() * xCoord, game.getSquare() * yCoord,
                game.getSquare(), game.getSquare()
        );
    }

}
