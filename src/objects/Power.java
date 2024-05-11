package objects;

import java.awt.geom.Rectangle2D;

import main.Game;

public class Power {

	private Rectangle2D.Float hitbox;
	private int dir = 1;
	private boolean active = true, reload = true;
	private float SPEED = 1f;
	private int type;
	


	public Power(int x, int y, int type) {
		int xOffset = (int) (-3 * Game.SCALE);
		int yOffset = (int) (5 * Game.SCALE);

		hitbox = new Rectangle2D.Float(x + xOffset, y + yOffset, 7, 4);
		this.type = type;
	}
	
	public void updatePos() {
		hitbox.y += (int) dir * SPEED;
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
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
