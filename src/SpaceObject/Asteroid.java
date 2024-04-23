package SpaceObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Asteroid extends SpaceObject {
    private BufferedImage image;
    private Random random;

    public Asteroid() {
        random = new Random();
        setDefaultValues();
        getAsteroidImage();
    }

    private void getAsteroidImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/Res/thien_thach-01.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultValues() {
        int randomInt = random.nextInt(800); // Giới hạn vị trí x trong khoảng từ 0 đến 799
        x = randomInt;
        y = 0;
        speed = 3;
    }

    public void update() {
        y += speed;
    }

    public void draw(Graphics2D g) {
        g.drawImage(image, x, y, 20, 20, null);
    }

    public int getY() {
        return y;
    }
}

