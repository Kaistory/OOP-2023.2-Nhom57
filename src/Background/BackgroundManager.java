package Background;

import Main.GamePanel01;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackgroundManager extends Background {
    private GamePanel01 gamePanel01;
    public BackgroundManager (GamePanel01 gamePanel01){
        getBackground();
        this.gamePanel01=gamePanel01;
    }
    public void getBackground(){
            try {
                background01 = ImageIO.read(getClass().getResourceAsStream("/Res/Background/background01.png"));
            }catch (IOException e){
                e.printStackTrace();
        }
    }
    public void draw(Graphics g){
        int panelWidth = gamePanel01.getWidth();
        int panelHeight = gamePanel01.getHeight();
        BufferedImage image = background01;
        g.drawImage(background01,0,0,panelWidth,panelHeight,null);
    }
}
