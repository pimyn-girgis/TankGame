package com.tankgame.entities;

import java.awt.*;

public class Mine extends GameObject {
    private boolean isVisible = true;
    private long lastBlinkTime = System.currentTimeMillis();
    private static final long BLINK_INTERVAL = 1000; // Blink every second

    public Mine(double x, double y) {
        super(x, y, 20, 20);  // Size of mine
    }

    @Override
    public void update() {
        // Blinking effect
        if (System.currentTimeMillis() - lastBlinkTime >= BLINK_INTERVAL) {
            isVisible = !isVisible;
            lastBlinkTime = System.currentTimeMillis();
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isVisible) return;

        Graphics2D g2d = (Graphics2D) g;

        // Draw mine body
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval((int)x, (int)y, width, height);

        // Draw warning marks
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        // Draw X marks
        g2d.drawLine((int)x + 5, (int)y + 5, (int)x + width - 5, (int)y + height - 5);
        g2d.drawLine((int)x + width - 5, (int)y + 5, (int)x + 5, (int)y + height - 5);
    }
}