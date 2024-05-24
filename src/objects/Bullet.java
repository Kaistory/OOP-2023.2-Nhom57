package objects;

import static utilz.Constants.ObjectConstants.BOX;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;
//import static utilz.Constants.Projectiles.*;

public class Bullet {
	
	private Rectangle2D.Float hitbox;
	private int dir;
	private boolean active = true, reload = true;
	public static float SPEED = 1f;
	

	protected static int Tick;

	public Bullet(int x, int y, int dir) {
		int xOffset = (int) (-3 * Game.SCALE);
		int yOffset = (int) (5 * Game.SCALE);

		if (dir == 1)
			xOffset = (int) (29 * Game.SCALE);

		hitbox = new Rectangle2D.Float(x + xOffset, y + yOffset, 7, 4);
		this.dir = dir;
	}

	public void updatePos() {
		hitbox.y -= (int) dir * SPEED;
		updateTick();
	}
	public void drawHitbox(Graphics g) {
		g.setColor(Color.PINK);
		int xLvlOffset = 0;
		g.drawRect((int) hitbox.x - xLvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
	}
	//thoigian ra dan
	public void updateTick() {
		Tick ++;
		if(Tick > 10)
			Tick = 11;
	}
	
	public void setPos(int x, int y) {
		hitbox.x = x;
		hitbox.y = y;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
	
	public int getDir() {
		return dir;
	}
	public void setSPEED(float sPEED) {
		SPEED = sPEED;
	}

	public static void changeSPEED(float sp) {
		SPEED = sp;
		
	}
}
