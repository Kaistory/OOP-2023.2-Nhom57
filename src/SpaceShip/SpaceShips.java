package SpaceShip;

import Main.GamePanel01;
import Main.KeyHandler;
import SpaceObject.SpaceObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class SpaceShips extends SpaceObject {
    GamePanel01 gp;
    KeyHandler keyH;
    private BufferedImage image;
    public SpaceShips(GamePanel01 gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH=keyH;

        setDefaultValues();
        getSpaceShipImage();

    }
    private void getSpaceShipImage() {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/Res/spaceShip01.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultValues() {
        x= 100;
        y=100;
        speed =5;
    }

    public void update() {
        if (keyH.upPressed == true && y > 0){
            y -= speed;
        }
        else if (keyH.downPressed == true && y <= gp.getHeight() - gp.tileSize ){
            y += speed;
        }
        else if (keyH.leftPressed == true && x >= 0){
            x -= speed;
        }
        else if (keyH.righgtPressed == true && x <= gp.getWidth() - gp.tileSize){
            x += speed;
        }
    }
    public void draw(Graphics2D g2){
//        g2.setColor(Color.BLUE);
//        g2.fillRect(x,y,gp.tileSize,gp.tileSize);
        g2.drawImage(image,x,y,gp.tileSize, gp.tileSize, null);
    }

}
