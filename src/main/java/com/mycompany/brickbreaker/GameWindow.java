/*
 * 
 * 
 */
package com.mycompany.brickbreaker;

import javax.swing.JFrame;

/**
 *
 * @author Jackson
 */
public class GameWindow extends JFrame {

    private final GamePanel childPanel;
    
    public GameWindow(GamePanel panel) {
        setTitle("Brick breaker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
        setResizable(false);
        
        childPanel = panel;
    }
    
    public void launchGame() {
        setVisible(true);
        add(childPanel);
        pack();
        
        childPanel.startGameThread();
    }
    
}
