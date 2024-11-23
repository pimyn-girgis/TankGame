package com.tankgame.entities;

import com.tankgame.utils.Direction;
import java.awt.*;

public class Missile extends GameObject {
    private final Direction direction;
    private boolean active = true;
    private final boolean fromPlayer;  // To distinguish between player and house missiles

    public Missile(double x, double y, Direction direction, boolean fromPlayer) {
        super(x, y, 8, 8);  // Small size for missile
        this.direction = direction;
        this.fromPlayer = fromPlayer;
    }

    @Override
    public void update() {
        if (active) {
            double SPEED = 5.0;
            x += direction.getDx() * SPEED;
            y += direction.getDy() * SPEED;

            // Deactivate missile if it goes off screen
            if (x < 0 || x > 800 || y < 0 || y > 600) {
                active = false;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(fromPlayer ? Color.RED : Color.BLACK);
            g.fillOval((int)x, (int)y, width, height);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }
}