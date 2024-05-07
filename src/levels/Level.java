package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class Level {

	private BufferedImage img;
	
	private int[][] lvlData;
	private Point playerSpawn;


	public Level(int[][] lvlData) {
		this.lvlData = lvlData;
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

}
