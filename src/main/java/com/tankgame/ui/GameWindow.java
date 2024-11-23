package com.tankgame.ui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private final GamePanel gamePanel;
    private final ControlPanel controlPanel;

    public GameWindow() {
        setTitle("Tank Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(800, 600));

        // Create control panel with sliders
        controlPanel = new ControlPanel(this);
        setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Pack and center the window
        pack();
        setLocationRelativeTo(null);
    }

    public void startGame(int missileAggressiveness, int mineCount) {
        remove(controlPanel);
        pack();
        setLocationRelativeTo(null);
        gamePanel.startGame(missileAggressiveness, mineCount);
    }
}