package com.tankgame.entities;

import com.tankgame.utils.Direction;
import java.awt.*;

public class House extends GameObject {
    private int health = 3;
    private long lastFireTime = 0;
    private final int aggressiveness;  // Controls fire rate (1-10)
    private static final int BASE_FIRE_RATE = 2000; // Base milliseconds between shots

    public House(double x, double y, int aggressiveness) {
        super(x, y, 60, 60);  // House size 60x60 pixels
        this.aggressiveness = aggressiveness;
    }

    public Missile fireMissile(Tank targetTank) {
        if (health <= 0) return null;

        long currentTime = System.currentTimeMillis();
        long fireRate = BASE_FIRE_RATE - (aggressiveness * 150L);

        if (currentTime - lastFireTime >= fireRate) {
            lastFireTime = currentTime;
            double tankCenterX = targetTank.getX() + (double) targetTank.width /2;
            double tankCenterY = targetTank.getY() + (double) targetTank.height /2;
            double houseCenterX = x + (double) width /2;
            double houseCenterY = y + (double) height /2;

            double dx = tankCenterX - houseCenterX;
            double dy = tankCenterY - houseCenterY;
            double angle = Math.atan2(dy, dx);

            // Get direction to tank
            Direction direction = getDirectionToTarget(angle);

            return new Missile(
                    houseCenterX - 4,  // Center missile on house
                    houseCenterY - 4,
                    direction,
                    false
            );
        }
        return null;
    }

    private Direction getDirectionToTarget(double angle) {
        // Convert angle to degrees (0 to 360)
        double degrees = Math.toDegrees(angle);
        if (degrees < 0) degrees += 360;

        // Convert to the closest 45-degree direction
        // 0/360 = East, 45 = Southeast, 90 = South, etc.
        int directionIndex = (int) Math.round(degrees / 45) % 8;

        directionIndex = (directionIndex + 2) % 8;  // Adjust for enum ordering

        return Direction.values()[directionIndex];
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics g) {
        // Draw house
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y, width, height);

        // Draw roof
        int[] xPoints = {(int)x - 10, (int)x + width/2, (int)x + width + 10};
        int[] yPoints = {(int)y, (int)y - 20, (int)y};
        g.fillPolygon(xPoints, yPoints, 3);

        // Draw health bar
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 30, width, 5);
        g.setColor(Color.GREEN);
        g.fillRect((int)x, (int)y - 30, (width * health) / 3, 5);
    }

    public void takeDamage() {
        if (health > 0) {
            health--;
        }
    }

    public boolean isDestroyed() {
        return health <= 0;
    }
}