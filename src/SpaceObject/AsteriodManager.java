//package SpaceObject;
//
//import java.awt.Graphics2D;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class AsteroidManager {
//    private List<Asteroid> asteroids;
//
//    public AsteroidManager() {
//        asteroids = new ArrayList<>();
//    }
//
//    public void createAsteroid() {
//        Random random = new Random();
//        int x = random.nextInt(2000); // Vị trí x ngẫu nhiên trên màn hình
//        int y = 0; // Bắt đầu từ trên cùng của màn hình
//        int speed = 3; // Tốc độ rơi của thiên thạch
//
//        Asteroid asteroid = new Asteroid(x, y, speed); // Tạo một thiên thạch mới
//        asteroids.add(asteroid); // Thêm vào danh sách thiên thạch
//    }
//
//    public void update() {
//        // Cập nhật vị trí của từng thiên thạch
//        for (Asteroid asteroid : asteroids) {
//            asteroid.update();
//        }
//
//        // Loại bỏ các thiên thạch đã rơi xuống dưới màn hình
//        asteroids.removeIf(asteroid -> asteroid.getY() > 600); // Giả sử màn hình có chiều cao 600 pixels
//    }
//
//    public void draw(Graphics2D g) {
//        // Vẽ từng thiên thạch trên màn hình
//        for (Asteroid asteroid : asteroids) {
//            asteroid.draw(g);
//        }
//    }
//}
//
