package com.tankgame.entities;

import java.awt.*;

public class Mountain extends GameObject {
    private final Polygon mountainShape;

    public Mountain(double x, double y, int width, int height) {
        super(x, y, width, height);

        mountainShape = new Polygon();
        mountainShape.addPoint((int)x + width/2, (int)y);  // Top
        mountainShape.addPoint((int)x, (int)y + height);   // Bottom left
        mountainShape.addPoint((int)x + width, (int)y + height); // Bottom right
    }

    @Override
    public void update() {
        // Mountains don't need updating
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw mountain body
        g2d.setColor(new Color(139, 69, 19));  // Brown
        g2d.fill(mountainShape);

        // Draw snow cap
        g2d.setColor(Color.WHITE);
        Polygon snowCap = new Polygon();
        snowCap.addPoint((int)x + width/2, (int)y);  // Peak
        snowCap.addPoint((int)x + width/2 - 15, (int)y + 20);
        snowCap.addPoint((int)x + width/2 + 15, (int)y + 20);
        g2d.fill(snowCap);
    }

    @Override
    public Rectangle getBounds() {
        return mountainShape.getBounds();
    }

    public boolean intersects(Rectangle rect) {
        return mountainShape.intersects(rect);
    }
}