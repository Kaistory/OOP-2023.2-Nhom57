package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.plaf.synth.SynthScrollPaneUI;

import entities.Player;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import static utilz.Constants.ObjectConstants.*;

public class ObjectManager {
	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] cannonImgs;
	private BufferedImage cannonBallImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Bullet> bullets;
	private ArrayList<Cannon> cannons;
	
	public ObjectManager(Playing playing) {
		this.playing = playing;
//		currentLevel = playing.getLevelManager().getCurrentLevel();
		loadImgs();
		
		potions  = new ArrayList<>();
		
		
		containers = new ArrayList<>();
		
		
		cannons = new ArrayList<>();
		
		
		bullets = new ArrayList<Bullet>();
		addEnemy();
	}
	
	public void addEnemy() {
		int yPos = 120;
		int sizeEnemy = 10;
		while(potions.size() < sizeEnemy && yPos < Game.GAME_HEIGHT - 200) {
			Random ramdom = new Random();
			int xDefault = 0;//ramdom.nextInt(0, 50);
			int size =  ramdom.nextInt(3, 5);
			int xPos, type;
			for(int i = 0; i < size; i++) {
				if(xDefault >= Game.GAME_WIDTH - 900 || potions.size() >= sizeEnemy)
					break;
				xPos = ramdom.nextInt(1,10);
				xDefault = xDefault + xPos * 75;
				if(xPos % 2 == 1)
					type =RED_POTION;
				else type = BLUE_POTION;
				potions.add(new Potion(xDefault ,yPos, type));
				
			}
			yPos += 75;
		}
		
//		potions.add(new Potion(600, 200, RED_POTION));
//		potions.add(new Potion(700, 200, BLUE_POTION));
		
//		containers.add(new GameContainer(500, 300, BARREL));
//		containers.add(new GameContainer(600, 300, BARREL));
		
		
		
		cannons.add(new Cannon(700, 300, 5));
	}
	
	public void checkObjectTouched(java.awt.geom.Rectangle2D.Float hitbox) {
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	}
	
	public void applyEffectToPlayer(Potion p) {
//		if (p.getObjType() == RED_POTION)
//			playing.getPlayer().changeHealth(RED_POTION_VALUE);
//		else
//			playing.getPlayer().changePower(BLUE_POTION_VALUE);
	}
	
	
	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][6];

		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(200 * j, 200 * i, 200, 200);
		

		
		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];

		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
		
		cannonImgs = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

		for (int i = 0; i < cannonImgs.length; i++)
			cannonImgs[i] = temp.getSubimage(i * 40, 0, 40, 26);

		
		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
	}
	
	public void update(int[][] lvlData, Player player) {
		for(Potion p : potions) {
			if(p.isActive()) {
				
				p.update();
			if(p.getAniIndex() == 4  && p.getAniTick() == 0)
				bullets.add(new Bullet((int)p.getHitbox().x - 10, (int)p.getHitbox().y, -1));
		}
		}
		
		for(GameContainer gc : containers) {
			if(gc.isActive())
				gc.update();
		}
		updateBullet(lvlData, player);

	}
	
	private void updateBullet(int[][] lvlData, Player player) {
		for(Bullet b : bullets) {
			if(b.isActive()) {
				b.updatePos();
				if(b.getHitbox().intersects(player.getHitbox()) && b.getDir() == -1) {
					b.setActive(false);
					player.changeHealth(-25);
				}
				for(Potion p : potions)
				if(b.getHitbox().intersects(p.getHitbox()) && p.isActive() && b.getDir() == 1) {
					b.setActive(false);
					p.setActive(false);
				}
			}
		}
	}
	
	
	
	public void draw(Graphics g, int xLvlOffset) {
		drawPotion(g, xLvlOffset);
		drawContainer(g, xLvlOffset);
		drawBullet(g, xLvlOffset);
		
	}
	private void drawContainer(Graphics g, int xLvlOffset) {

		for (GameContainer gc : containers)
			if (gc.isActive()) {
				int type = 0;
				if (gc.getObjType() == BARREL)
					type = 1;
				g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH,
						CONTAINER_HEIGHT, null);
			}
	}
	
	private void drawPotion(Graphics g, int xLvlOffset) {
		// TODO Auto-generated method stub
		for (Potion p : potions)
			if (p.isActive()) {
				int type = 0;
				if (p.getObjType() == RED_POTION)
					type = 1;
				g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - (p.getxDrawOffset() - xLvlOffset) * 3), (int) (p.getHitbox().y - p.getyDrawOffset() *  3), POTION_WIDTH * 3, POTION_HEIGHT * 3,
						null);
			}		
	}
	private void drawBullet(Graphics g, int xLvlOffset) {
		// TODO Auto-generated method stub
		for (Bullet b : bullets)
			if (b.isActive()) {
				g.drawImage(cannonBallImg, (int) (b.getHitbox().x  - xLvlOffset), (int) (b.getHitbox().y), POTION_WIDTH, POTION_HEIGHT,
						null);
			}		
	}
	public void addBullet(int x, int y, int dir) {
		
		bullets.add(new Bullet(x, y, dir));
	}
	public void loadObjects() {
		potions  = new ArrayList<>();
		
		containers = new ArrayList<>();
		
		cannons = new ArrayList<>();
		
		bullets = new ArrayList<Bullet>();
		bullets.clear();
		addEnemy();
	}
	public void resetAllObjects() {
		loadObjects();
		for (Potion p : potions)
			p.reset();
		for (GameContainer gc : containers)
			gc.reset();
		for (Cannon c : cannons)
			c.reset();
		bullets.clear();
		
	}
	
	public boolean checkComplete() {
		for (Potion p : potions)
			if(p.isActive())
				return false;
		return true;
	}
	
	
}
