package com.tankgame.ui;

import com.tankgame.entities.House;
import com.tankgame.entities.Missile;
import com.tankgame.entities.Tank;
import com.tankgame.entities.Mine;
import com.tankgame.entities.Mountain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private volatile boolean running = false;

    private int mineCount;

    private Tank playerTank;
    private House house;
    private final ArrayList<Missile> missiles = new ArrayList<>();
    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
    private final ArrayList<Mine> mines = new ArrayList<>();
    private final ArrayList<Mountain> mountains = new ArrayList<>();

    public GamePanel() {
        setBackground(Color.GRAY);
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
    }

    private void generateMountains() {
        createMountainCluster(300, 100, 3);
        createMountainCluster(600, 400, 3);
    }

    private void createMountainCluster(int centerX, int centerY, int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int width = 80 + rand.nextInt(40);
            int height = 100 + rand.nextInt(50);
            int offsetX = rand.nextInt(100) - 50;
            int offsetY = rand.nextInt(60) - 30;

            Mountain mountain = new Mountain(
                    centerX + offsetX - (double) width /2,
                    centerY + offsetY - (double) height /2,
                    width,
                    height
            );

            if (isValidMountainPosition(mountain)) {
                mountains.add(mountain);
            }
        }
    }

    private boolean isValidMountainPosition(Mountain mountain) {
        // Check distance from house
        double distToHouse = Math.sqrt(
                Math.pow(mountain.getX() - house.getX(), 2) +
                        Math.pow(mountain.getY() - house.getY(), 2)
        );

        // Check distance from tank's starting position
        double distToTank = Math.sqrt(
                Math.pow(mountain.getX() - playerTank.getX(), 2) +
                        Math.pow(mountain.getY() - playerTank.getY(), 2)
        );

        return distToHouse > 150 && distToTank > 200;
    }

    public void startGame(int missileAggressiveness, int mineCount) {
        this.mineCount = mineCount;

        // Create tank at random position on the right side
        int tankX = getWidth() - 200 + (int)(Math.random() * 150);
        int tankY = (int)(Math.random() * (getHeight() - 40));
        playerTank = new Tank(tankX, tankY);
        house = new House(50, (double) getHeight() /2 - 30, missileAggressiveness);
        generateMines();
        generateMountains();

        if (gameThread == null || !running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    private void handleInput() {
        if (playerTank != null) {
            playerTank.setMoving(false);

            for (Integer keyCode : pressedKeys) {
                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        playerTank.rotateLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        playerTank.rotateRight();
                        break;
                    case KeyEvent.VK_UP:
                        playerTank.setMoving(true);
                        playerTank.setMoveDirection(true);
                        break;
                    case KeyEvent.VK_DOWN:
                        playerTank.setMoving(true);
                        playerTank.setMoveDirection(false);
                        break;
                    case KeyEvent.VK_SPACE:
                        Missile newMissile = playerTank.fireMissile();
                        if (newMissile != null) {
                            missiles.add(newMissile);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;
        int FPS = 60;
        double ns = 1000000000.0 / FPS;
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                handleInput();
                update();
                delta--;
            }

            repaint();

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
            }

            // The game is extremely laggy without this. Don't delete.
            // According to stackoverflow, this prevents cpu `overuse`
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (!running) return;

        if (playerTank != null && house != null) {
            double oldX = playerTank.getX();
            double oldY = playerTank.getY();
            playerTank.update();

            boolean collision = false;
            for (Mountain mountain : mountains) {
                if (mountain.intersects(playerTank.getBounds())) {
                    collision = true;
                    break;
                }
            }
            collision |= playerTank.getBounds().intersects(house.getBounds());

            if (collision) {
                playerTank.setX(oldX);
                playerTank.setY(oldY);
            }

            house.update();

            Missile houseMissile = house.fireMissile(playerTank);
            if (houseMissile != null) {
                missiles.add(houseMissile);
            }

            for (Mine mine : mines) {
                mine.update();

                if (playerTank.getBounds().intersects(mine.getBounds())) {
                    while (playerTank.getHealth() != 0) {
                        playerTank.takeDamage();
                    }
                }
            }

            Iterator<Missile> it = missiles.iterator();
            while (it.hasNext()) {
                Missile missile = it.next();
                missile.update();

                boolean missileCollision = false;
                for (Mountain mountain : mountains) {
                    if (mountain.intersects(missile.getBounds())) {
                        missileCollision = true;
                        break;
                    }
                }

                if (missileCollision) {
                    missile.setActive(false);
                }

                if (missile.isActive()) {
                    if (missile.isFromPlayer()) {
                        if (missile.getBounds().intersects(house.getBounds())) {
                            house.takeDamage();
                            missile.setActive(false);
                        }
                    } else {
                        if (missile.getBounds().intersects(playerTank.getBounds())) {
                            playerTank.takeDamage();
                            missile.setActive(false);
                        }
                    }
                } else {
                    it.remove();
                }
            }

            if (house.isDestroyed() || playerTank.isDestroyed()) {
                running = false;
            }
        }
    }

    private void generateMines() {
        mines.clear();

        for (int i = 0; i < mineCount; i++) {
            boolean validPosition = false;
            double mineX = 0;
            double mineY = 0;

            while (!validPosition) {
                mineX = Math.random() * (getWidth() - 40);
                mineY = Math.random() * (getHeight() - 40);

                double distToHouse = Math.sqrt(
                        Math.pow(mineX - house.getX(), 2) +
                                Math.pow(mineY - house.getY(), 2)
                );

                double distToTank = Math.sqrt(
                        Math.pow(mineX - playerTank.getX(), 2) +
                                Math.pow(mineY - playerTank.getY(), 2)
                );

                if (distToHouse > 100 && distToTank > 150) {
                    validPosition = true;
                }
            }

            mines.add(new Mine(mineX, mineY));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Mountain mountain : mountains) {
            mountain.render(g2d);
        }

        for (Mine mine : mines) {
            mine.render(g2d);
        }

        if (house != null) {
            house.render(g2d);
        }

        if (playerTank != null) {
            playerTank.render(g2d);
        }

        for (Missile missile : missiles) {
            missile.render(g2d);
        }

        if (!running) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String message = "";
            if (house != null) {
                message = house.isDestroyed() ? "You Win!" : "Game Over!";
            }
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(message, x, y);
        }
    }
}