package Main;

import javax.swing.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Space War");

        GamePanel01 gamePanel01 = new GamePanel01();
        window.add(gamePanel01);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel01.startGameThread();
    }
}





