/*
 * 
 * 
 */
package com.mycompany.brickbreaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Jackson
 */
public class GamePanel extends JPanel implements ActionListener {

    private enum Direction {
        LEFT,
        RIGHT,
        STILL
    }

    private GameWindow parentWindow;
    private Timer timer;

    private boolean running = false;
    private final int TIMER_DELAY = 5;

    private final int ORIGINAL_BIT_SIZE = 8;
    private final int SCALE = 3;
    private final int BIT_SIZE = ORIGINAL_BIT_SIZE * SCALE;

    private final int TILES_X = 20;
    private final int TILES_Y = 24;

    private final int SCREEN_WIDTH = BIT_SIZE * TILES_X;
    private final int SCREEN_HEIGHT = BIT_SIZE * TILES_Y;

    private final int PLATFORM_BITS = 5;
    private final int PLATFORM_WIDTH = BIT_SIZE * PLATFORM_BITS;
    private final int PLATFORM_SPEED = 8;
    
    private final int HITBOX = 5;

    private Direction direction = Direction.STILL;

    private int platformX = SCREEN_WIDTH / 2;
    private int platformY = SCREEN_HEIGHT - (BIT_SIZE * 3);
    
    private List<Brick> bricks = new ArrayList<>();
    
    private int ballSpeedX = 5;
    private int ballSpeedY = 5;
    private int NEGATIVE_SPEED = -5;
    private int POSITIVE_SPEED = 5;
    
    private int ballX = platformX;
    private int ballY = platformY - BIT_SIZE;
    
    private int points = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(new KeyHandler());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (running) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(platformX, platformY, PLATFORM_WIDTH, BIT_SIZE);
            
            g2d.fillOval(ballX, ballY, BIT_SIZE, BIT_SIZE);
            
            for (Brick brick : bricks) {
                g2d.fillRect(brick.getX(), brick.getY(), BIT_SIZE, BIT_SIZE);
            }
        } else {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            g2d.drawString("¡GAME OVER!", getWidth() / 2 - 170, getHeight() / 2);
        }
    }

    public void startGameThread() {
        timer = new Timer(TIMER_DELAY, this);
        timer.start();
        running = true;
        
        for (int i = BIT_SIZE * 3; i < SCREEN_WIDTH - (BIT_SIZE * 3); i+=BIT_SIZE) {
            for (int j = BIT_SIZE * 2; j < BIT_SIZE * 6; j+=BIT_SIZE) {
                bricks.add(new Brick(i, j));
            }
        }
    }

    private void movePlatform() {
        switch (direction) {
            case Direction.LEFT -> {
                if ( platformX >= PLATFORM_SPEED ) {
                    platformX -= PLATFORM_SPEED;
                } else {
                    platformX = 0;
                }
            }
            case Direction.RIGHT -> {
                if ( (platformX + PLATFORM_WIDTH) < SCREEN_WIDTH ) {
                    platformX += PLATFORM_SPEED;
                } else {
                    platformX = SCREEN_WIDTH - PLATFORM_WIDTH;
                }
            }
            case Direction.STILL -> platformX += 0;
        }
    }

    private void moveBall() {
        ballY+=ballSpeedY;
        ballX+=ballSpeedX;
        
        if (ballX >= platformX && ballX <= (platformX + PLATFORM_WIDTH) && 
                ballY >= (platformY - BIT_SIZE) && ballY <= platformY) {
            ballSpeedX = NEGATIVE_SPEED;
            ballSpeedY = NEGATIVE_SPEED;
        }
        
        if (ballX > SCREEN_WIDTH - BIT_SIZE) {
            ballSpeedX = NEGATIVE_SPEED;
        }
        if (ballX < 0) {
            ballSpeedX = POSITIVE_SPEED;
        }
        
        if (ballY < 0) {
            ballSpeedY = POSITIVE_SPEED;
        }
    }
    
    private void checkColision() {
        for (Iterator<Brick> iterator = bricks.iterator(); iterator.hasNext();) {
            Brick next = iterator.next();
            
            if (leftColision(next)) {
                ballSpeedX = NEGATIVE_SPEED;
                iterator.remove();
                points++;
            } 
            else if (rightColision(next)) {
                ballSpeedX = POSITIVE_SPEED;
                iterator.remove();
                points++;
            }
            else if (upColision(next)) {
                ballSpeedY = NEGATIVE_SPEED;
                iterator.remove();
                points++;
            }
            else if (downColision(next)) {
                ballSpeedY = POSITIVE_SPEED;
                iterator.remove();
                points++;
            }
        }
    }
    
    private boolean downColision(Brick brick) {
        return ( ballY >= brick.getY() + BIT_SIZE && ballY <= brick.getY() + BIT_SIZE + HITBOX) && 
                ( ballX >= brick.getX() - BIT_SIZE && ballX <= brick.getX() + BIT_SIZE );
    }

    private boolean upColision(Brick brick) {
        return ( ballY >= brick.getY() - BIT_SIZE && ballY <= brick.getY() - BIT_SIZE + HITBOX) && 
                ( ballX >= brick.getX() - BIT_SIZE && ballX <= brick.getX() + BIT_SIZE );
    }

    private boolean rightColision(Brick brick) {
        return ( ballX >= brick.getX() + BIT_SIZE && ballX <= brick.getX() + BIT_SIZE + HITBOX) && 
                ( ballY >= brick.getY() - BIT_SIZE && ballY <= brick.getY() + BIT_SIZE );
    }

    private boolean leftColision(Brick brick) {
        return ( ballX >= brick.getX() - BIT_SIZE && ballX <= brick.getX() - BIT_SIZE + HITBOX) && 
                ( ballY >= brick.getY() - BIT_SIZE && ballY <= brick.getY() + BIT_SIZE );
    }

    private void detectGameOver() {
        if (ballY >= SCREEN_HEIGHT) {
            running = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            movePlatform();
            moveBall();
            detectGameOver();
            checkColision();
        }
        repaint();
    }

    public class KeyHandler implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_D ->
                    direction = Direction.RIGHT;
                case KeyEvent.VK_A ->
                    direction = Direction.LEFT;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_D || key == KeyEvent.VK_A) direction = Direction.STILL;
        }
    }

}
