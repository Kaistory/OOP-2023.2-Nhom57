//package Main;
//
//
//import Background.*;
//import SpaceObject.Asteroid;
//
//import javax.swing.*;
//import java.awt.*;
//
//
//public class GamePanel01 extends JPanel implements Runnable {
//    //Screen setting
//    final int originalTileSize = 16; //16x16 tile
//    final int scale = 3;
//    public final int tileSize = originalTileSize*scale; //48x48 title
//    final int maxScreenCol = 18;
//    final int maxScreenRow = 14;
//    final int screenWidth = tileSize*maxScreenCol;
//    final int screenHeight= tileSize*maxScreenRow;
//
//    //FPS
//    int FPS = 60;
//
//    Asteroid asteroid = new Asteroid();
//    BackgroundManager backgroundManager = new BackgroundManager(this);
//    Thread gameThread;
//    public GamePanel01(){
//        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
//        this.setBackground(Color.RED);
//        this.setDoubleBuffered(true);
//    }
//
//    public void startGameThread(){
//        gameThread = new Thread(this);
//        gameThread.start();
//    }
//
//    @Override
//    public void run() {
//
//        double drawInterval = 1000000000/FPS;
//        double nextDrawTime = System.nanoTime() + drawInterval;
//        while(gameThread != null){
//
//            long currentTime = System.nanoTime();
//
//            //Update information such as position
//                update();
//            //Draw
//                repaint();
//
//                try {
//                    double remainngTime = nextDrawTime - System.nanoTime();
//                    remainngTime = remainngTime/1000000;
//
//                    if (remainngTime < 0) {
//                        remainngTime =0;
//                    }
//                    Thread.sleep((long)remainngTime);
//                    nextDrawTime+= drawInterval;
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//        }
//    }
//    public void update(){
//        asteroid.update();
//    }
//    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D)g;
//        backgroundManager.draw(g2);
//        asteroid.draw(g2);
//        g.dispose();
//    }
//}


package Main;

import Background.BackgroundManager;
import SpaceObject.Asteroid;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel01 extends JPanel implements Runnable {

    //    //Screen setting
    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize*scale; //48x48 title
    final int maxScreenCol = 18;
    final int maxScreenRow = 14;
    final int screenWidth = tileSize*maxScreenCol;
    final int screenHeight= tileSize*maxScreenRow;

    //FPS
    int FPS = 60;
    private List<Asteroid> asteroids;
    private BackgroundManager backgroundManager;
    private Thread gameThread;

    public GamePanel01() {
        // Khởi tạo danh sách thiên thạch
        asteroids = new ArrayList<>();
        backgroundManager = new BackgroundManager(this);

        // Thêm thiên thạch ban đầu
        createAsteroid();

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.RED);
        this.setDoubleBuffered(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            long currentTime = System.nanoTime();

            // Tạo thiên thạch mới sau một khoảng thời gian
            if (currentTime >= nextAsteroidTime) {
                createAsteroid();
                nextAsteroidTime = currentTime + asteroidInterval;
            }

            // Cập nhật vị trí của các thiên thạch
            updateAsteroids();

            // Vẽ lại màn hình
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private long nextAsteroidTime = System.nanoTime(); // Thời điểm tạo thiên thạch tiếp theo
    private final long asteroidInterval = 2000000000; // Khoảng thời gian tạo thiên thạch (2 giây)

    private void createAsteroid() {
        Asteroid asteroid = new Asteroid();
        asteroids.add(asteroid);
    }

    private void updateAsteroids() {
        // Cập nhật vị trí của từng thiên thạch
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroid = asteroids.get(i);
            asteroid.update();

            // Xóa thiên thạch nếu ra khỏi màn hình
            if (asteroid.getY() > screenHeight) {
                asteroids.remove(i);
                i--; // Điều chỉnh chỉ số sau khi xóa phần tử
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Vẽ nền và các thiên thạch
        backgroundManager.draw(g2);
        for (Asteroid asteroid : asteroids) {
            asteroid.draw(g2);
        }

        g2.dispose();
    }
}

