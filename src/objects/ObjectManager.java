package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.plaf.synth.SynthScrollPaneUI;

import audio.AudioPlayer;
import entities.Player;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import static utilz.Constants.ObjectConstants.*;

public class ObjectManager {
	private Playing playing;
	private BufferedImage[][] potionImgs;
	private BufferedImage enemyBallImg, powerImg;
	private BufferedImage cannonBallImg , cannonBallImg1;
	private ArrayList<Potion> potions;
	private ArrayList<Bullet> bullets;
	private ArrayList<Power> powers;
	private  int sizeEnemy = 5;
	private int score;
	

	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImgs();
		potions  = new ArrayList<>();
		
		bullets = new ArrayList<>();
		
		powers = new ArrayList<>();
		
		score = 0;
		addEnemy();
	}
	
	public void addEnemy() {
		int yPos = 0;
		if(sizeEnemy >= 25)
			sizeEnemy = 25;
		while(potions.size() < sizeEnemy && yPos < Game.GAME_HEIGHT - 200) {
			Random ramdom = new Random();
			int xDefault = 0;//ramdom.nextInt(0, 50);
			int size =  ramdom.nextInt(3, 6);
			int xPos, type;
			for(int i = 0; i < size; i++) {
				if(xDefault >= Game.GAME_WIDTH - 900 || potions.size() >= sizeEnemy)
					break;
				xPos = ramdom.nextInt(1,10);
				xDefault = xDefault + xPos * 75;
				if(xPos % 2 == 1)
					type = RED_POTION;
				else type = BLUE_POTION;
				potions.add(new Potion(xDefault ,yPos, type));
				
			}
			yPos += 75;
		}	
	}
	
	public void checkObjectTouched(java.awt.geom.Rectangle2D.Float hitbox) {
		for (Potion p : potions)
			if (p.isActive()) {
				if (hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
				}
			}
	}

	
	
	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][6];

		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(200 * j, 200 * i, 200, 200);		
		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
		cannonBallImg1 = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL_2);
		enemyBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL_1);
		powerImg = LoadSave.GetSpriteAtlas(LoadSave.POWER);
	}
	
	public void update(int[][] lvlData, Player player) {
		updatePotition(lvlData, player);
		updateBullet(lvlData, player);
		updatePower(lvlData, player);
	}
	
	
	private void updatePotition(int[][] lvlData, Player player) {
		for(Potion p : potions) {
			if(p.isActive()) {
				
				p.update();
			if(p.getAniIndex() == 4  && p.getAniTick() == 0)
				{
				
					Bullet.changeSPEED(1f + sizeEnemy / 10);
				
					bullets.add(new Bullet((int)p.getHitbox().x - 10, (int)p.getHitbox().y, -1));
				}
			if(p.getHitbox().intersects(player.getHitbox())) {
				p.setActive(false);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
				score -= 1;
				player.changeHealth(-20);
				
			}
		}
		}
		for(Bullet b : bullets) {
			if(b.isActive() && b.getDir() == 1) {
				//b.updatePos();
				for(Potion p : potions)
					if(b.getHitbox().intersects(p.getHitbox()) && p.isActive()) {
						Random ran = new Random();
						if(ran.nextInt(1, 5) == 3)
							powers.add(new Power((int)p.getHitbox().x - 10, (int)p.getHitbox().y, -1));
						score += 1;
						if(score < 50)
						b.setActive(false);
						p.setActive(false);
					}
			}
		}
	}
	
	private void updateBullet(int[][] lvlData, Player player) {
		for(Bullet b : bullets) {
			if(b.isActive()) {
				b.updatePos();
				if(b.getHitbox().intersects(player.getHitbox()) && b.getDir() == -1) {
					b.setActive(false);
					playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
					score -= 1;
					player.changeHealth(-20);
				}
			}
		}
		

	}
	
	public void updatePower(int[][] lvlData, Player player) {
		for(Power power : powers)
		if(power.isActive())
			{
				power.updatePos();
				if(power.getHitbox().intersects(player.getHitbox())) {
					player.changeHealth(20);
					power.setActive(false);
					score += 2;
					//playing.getGame().getAudioPlayer().playEffect(AudioPlayer.);
				}
			}
	}
	
	
	public void draw(Graphics g, int xLvlOffset) {
		drawPotion(g, xLvlOffset);
		drawBullet(g, xLvlOffset);
		drawPower(g, xLvlOffset);
		g.setColor(Color.white);
		g.setFont(new Font("TimesRoman", Font.PLAIN | Font.BOLD, 20));
		g.drawString("Score: " + score, 10, 820);
	}

	private void drawPower(Graphics g, int xLvlOffset) {
		for(Power power: powers)
		if(power.isActive())
			g.drawImage(powerImg, (int) (power.getHitbox().x  - xLvlOffset), (int) (power.getHitbox().y), POTION_WIDTH, POTION_HEIGHT,
					null);
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
				if(b.getDir() == 1) { 
				if(score < 50) {
					g.drawImage(cannonBallImg, (int) (b.getHitbox().x  - xLvlOffset), (int) (b.getHitbox().y), POTION_WIDTH, POTION_HEIGHT,
						null);}
				else g.drawImage(cannonBallImg1, (int) (b.getHitbox().x  - xLvlOffset), (int) (b.getHitbox().y), POTION_WIDTH, POTION_HEIGHT,
						null);}
				else
					g.drawImage(enemyBallImg, (int) (b.getHitbox().x  - xLvlOffset), (int) (b.getHitbox().y),(int) (POTION_WIDTH *1.5),(int)(POTION_HEIGHT * 1.5),
							null);
			}		
	}
	public void addBullet(int x, int y, int dir) {
		
		bullets.add(new Bullet(x, y, dir));
	}
	public void loadObjects() {
		potions  = new ArrayList<>();		
		bullets = new ArrayList<Bullet>();
		powers = new ArrayList<Power>();
		addEnemy();
	}
	public void resetAllObjects() {
		loadObjects();
		for (Potion p : potions)
			p.reset();
		bullets.clear();
		powers.clear();
	}
	
	public boolean checkComplete() {
		for (Potion p : potions)
			if(p.isActive())
				return false;
		return true;
	}
	
	
	public int getSizeEnemy() {
		return sizeEnemy;
	}

	public void setSizeEnemy(int sizeEnemy) {
		this.sizeEnemy += sizeEnemy;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
