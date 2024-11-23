package com.tankgame.ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private JSlider missileAggressivenessSlider;
    private JSlider mineCountSlider;
    private JButton startButton;
    private boolean gameStarted = false;
    private final GameWindow gameWindow;


    public ControlPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Sliders
        JLabel missileLabel = new JLabel("Missile Aggressiveness:");
        missileAggressivenessSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        missileAggressivenessSlider.setMajorTickSpacing(1);
        missileAggressivenessSlider.setPaintTicks(true);
        missileAggressivenessSlider.setPaintLabels(true);
        missileAggressivenessSlider.setEnabled(true);
        JLabel mineLabel = new JLabel("Number of Mines:");
        mineCountSlider = new JSlider(JSlider.HORIZONTAL, 5, 20, 10);
        mineCountSlider.setMajorTickSpacing(5);
        mineCountSlider.setPaintTicks(true);
        mineCountSlider.setPaintLabels(true);
        mineCountSlider.setEnabled(true);

        // Start Button
        startButton = new JButton("Start Game");
        startButton.addActionListener(_ -> {
            if (!gameStarted) {
                gameStarted = true;
                missileAggressivenessSlider.setEnabled(false);
                mineCountSlider.setEnabled(false);
                startButton.setEnabled(false);
                startGame();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(missileLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(missileAggressivenessSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        add(mineLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        add(mineCountSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(startButton, gbc);
    }

    private void startGame() {
        gameStarted = true;
        // Store the values before hiding the panel
        int missileValue = missileAggressivenessSlider.getValue();
        int mineValue = mineCountSlider.getValue();

        // Hide the entire control panel
        setVisible(false);

        // Notify parent window that game is starting
        gameWindow.startGame(missileValue, mineValue);
        // This will be implemented later to start the actual game
        System.out.println("Game Starting with:");
        System.out.println("Missile Aggressiveness: " + missileAggressivenessSlider.getValue());
        System.out.println("Mine Count: " + mineCountSlider.getValue());
    }
}