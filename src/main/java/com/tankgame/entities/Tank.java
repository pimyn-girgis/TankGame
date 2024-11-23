package com.tankgame.entities;

import com.tankgame.utils.Direction;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Tank extends GameObject {
    private Direction direction;
    private int health;
    private boolean isMoving;
    private boolean moveDirection;

    // Add rotation related fields
    private double currentAngle = 0; // Current angle in degrees
    private double targetAngle = 0;  // Target angle in degrees
    private long lastRotationTime = 0;
    private static final long ROTATION_DELAY = 200; // Milliseconds between rotations
    private long lastFireTime = 0;
    private static final long FIRE_RATE = 500;

    public Tank(double x, double y) {
        super(x, y, 40, 40);
        this.direction = Direction.NORTH;
        this.health = 3;
        this.isMoving = false;
    }

    @Override
    public void update() {
        if (currentAngle != targetAngle) {
            double diff = targetAngle - currentAngle;
            if (diff > 180) diff -= 360;
            if (diff < -180) diff += 360;

            double ROTATION_SPEED = 3.0;
            if (Math.abs(diff) <= ROTATION_SPEED) {
                currentAngle = targetAngle;
            } else if (diff > 0) {
                currentAngle += ROTATION_SPEED;
            } else {
                currentAngle -= ROTATION_SPEED;
            }

            if (currentAngle >= 360) currentAngle -= 360;
            if (currentAngle < 0) currentAngle += 360;
        }

        if (isMoving) {
            double MOVE_SPEED = 3.0;
            if (moveDirection) {
                x += direction.getDx() * MOVE_SPEED;
                y += direction.getDy() * MOVE_SPEED;
            } else {
                x -= direction.getDx() * MOVE_SPEED;
                y -= direction.getDy() * MOVE_SPEED;
            }

            x = Math.max(0, Math.min(x, 800 - width));
            y = Math.max(0, Math.min(y, 600 - height));
        }
    }

    public void setMoveDirection(boolean moveDirection) {
        this.moveDirection = moveDirection;
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform oldTransform = g2d.getTransform();

        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarY = (int)y - 15;

        // Health bar background (red)
        g2d.setColor(Color.RED);
        g2d.fillRect((int)x, healthBarY, healthBarWidth, healthBarHeight);

        // Health bar foreground (green)
        g2d.setColor(Color.GREEN);
        int currentHealthWidth = ((health * healthBarWidth) / 3);
        g2d.fillRect((int)x, healthBarY, currentHealthWidth, healthBarHeight);

        // Translate to tank position and rotate for tank rendering
        g2d.translate(x + (double) width /2, y + (double) height /2);
        g2d.rotate(Math.toRadians(currentAngle));

        // Draw tank body
        g2d.setColor(Color.GREEN);
        g2d.fillRect(-width/2, -height/2, width, height);

        // Draw tank cannon
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(-5, -height/2 - 10, 10, 20);

        // Restore the original transform
        g2d.setTransform(oldTransform);
    }

    public void rotateLeft() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRotationTime >= ROTATION_DELAY) {
            lastRotationTime = currentTime;
            targetAngle -= 45;
            if (targetAngle < 0) targetAngle += 360;
            updateDirectionFromAngle();
        }
    }

    public void rotateRight() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRotationTime >= ROTATION_DELAY) {
            lastRotationTime = currentTime;
            targetAngle += 45;
            if (targetAngle >= 360) targetAngle -= 360;
            updateDirectionFromAngle();
        }
    }

    private void updateDirectionFromAngle() {
        // Convert angle to direction enum
        int dirIndex = (int)Math.round(targetAngle / 45) % 8;
        direction = Direction.values()[dirIndex];
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    public Missile fireMissile() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= FIRE_RATE) {
            lastFireTime = currentTime;

            double missileX = x + (double) width /2 - 4 + direction.getDx() * ((double) height /2 + 10);
            double missileY = y + (double) height /2 - 4 + direction.getDy() * ((double) height /2 + 10);

            return new Missile(missileX, missileY, direction, true);
        }
        return null;
    }

    public void takeDamage() {
        health--;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }
}