package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {
	private Player player;
	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 25;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false,doubleattacking = false;
	public void setDoubleattacking(boolean doubleattacking) {
		this.doubleattacking = doubleattacking;
	}
	private boolean left, up, right, down, jump;
	private float playerSpeed = 1.0f * Game.SCALE;
	private int[][] lvlData;
	private float xDrawOffset = 21 * Game.SCALE;
	private float yDrawOffset = 4 * Game.SCALE;
	
	private int flipW = 1;

	// Jumping / Gravity
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = false;
	private int healthPlayer;
	private Playing playing;
	
	// StatusBarUI
		private BufferedImage statusBarImg;

		private int statusBarWidth = (int) (192 * Game.SCALE);
		private int statusBarHeight = (int) (58 * Game.SCALE);
		private int statusBarX = (int) (0 * Game.SCALE);
		private int statusBarY = (int) (0 * Game.SCALE);

		private int healthBarWidth = (int) (100 * Game.SCALE);
		private int healthBarHeight = (int) (20 * Game.SCALE);
		private int healthBarXStart = (int) (50 * Game.SCALE);
		private int healthBarYStart = (int) (14 * Game.SCALE);
		private int healthWidth = healthBarWidth;

		private int powerBarWidth = (int) (63 * Game.SCALE);
		private int powerBarHeight = (int) (7 * Game.SCALE);
		private int powerBarXStart = (int) (51 * Game.SCALE);
		private int powerBarYStart = (int) (36 * Game.SCALE);
		private int powerWidth = powerBarWidth;
		private int powerMaxValue = 200;
		private int powerValue = powerMaxValue;
		
		private boolean powerAttackActive;
		private int powerAttackTick;
		private int powerGrowSpeed = 15;
		private int powerGrowTick;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		loadAnimations();
		this.playing = playing;
		initHitbox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));
		this.maxHealth = 100;
		this.currentHealth = 100;
	}
	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}

	public void update() {
		updateHealthBar();
		updatePowerBar();
		
		if(currentHealth <= 0) {
//			if(state != DEAD) {
//				state = DEAD;
//				aniTick = 0;
//				aniIndex = 0;
//				playing.setPlayerDying(true);
//			} else if(aniTick == GetSpriteAmount(DEAD) - 1 && aniTick >= 24) {
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
				playing.setGameOver(true);
				playing.getObjectManager().setScore(0);
//			}
//			else updateAnimationTick();
			return;
		}
		
		
		updatePos();
		updateAnimationTick();
		setAnimation();
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
		
	}
	
	private void updatePowerBar() {
		powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);

		powerGrowTick++;
		if (powerGrowTick >= powerGrowSpeed) {
			powerGrowTick = 0;
			changePower(1);
		}
	}
	public void changePower(int value) {
		powerValue += value;
		powerValue = Math.max(Math.min(powerValue, powerMaxValue), 0);
	}
	
	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset, (int) (hitbox.y - yDrawOffset), width, height, null);
//		drawHitbox(g);
		drawUI(g); //bar
	}

	private void drawUI(Graphics g) {
		
		//health
				g.setColor(new Color(13, 222, 244));
				g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
	
	
		//power
		g.setColor(Color.yellow);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
		
		//Background
				g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
				
	}
	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
				doubleattacking = false;
			}

		}
	}

	private void setAnimation() {
		int startAni = playerAction;
		
		if (moving)
			playerAction = RUNNING;
//		else
//			playerAction = IDLE;
//
//		if (inAir) {
//			if (airSpeed < 0)
//				playerAction = JUMP;
//			else
//				playerAction = FALLING;
//		}
//
		if (attacking)
			{
				playerAction = ATTACK_1;
				playing.getGame().getAudioPlayer().playAttackSound();
			}
		if (doubleattacking)
			{
				playerAction = ATTACK_DOUBLE;
				playing.getGame().getAudioPlayer().playAttackSound();
			}

		if (startAni != playerAction)
			resetAniTick();
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;

		if (jump)
			jump();

		if (!inAir)
			if ((!left && !right) || (right && left))
				return;

		float xSpeed = 0 , ySpeed = 0;

		if (left && !right)
			xSpeed -= playerSpeed;
		if (right && !left)
			xSpeed += playerSpeed;
		
		if (up && !down)
			ySpeed -= playerSpeed;
		if (down && !up)
			ySpeed += playerSpeed;
		
			updateYPos(ySpeed);
			updateXPos(xSpeed);
		moving = true;
	}

	private void jump() {
		if (inAir)
			return;
		inAir = true;
		airSpeed = jumpSpeed;

	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;

	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}
		
	}
	
	private void updateYPos(float ySpeed) {
		if (CanMoveHere(hitbox.x , hitbox.y + ySpeed, hitbox.width, hitbox.height, lvlData) ) {
			hitbox.y += ySpeed;
		} else {
			hitbox.y = GetEntityYPosNextToWall(hitbox, ySpeed);
		}
		
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

		animations = new BufferedImage[4][6];
		for (int j = 0; j < animations.length; j++)
			for (int i = 0; i < animations[j].length; i++)
				animations[j][i] = img.getSubimage(j * 192, 1034 - (i+1) * 172, 192, 172);
		
		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;

	}

	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void changeHealth(int value) {
		currentHealth += value;
		if(currentHealth <= 0)
			currentHealth = 0;
		else if(currentHealth > maxHealth)
			currentHealth = maxHealth;
		
	}
	public void kill() {
		currentHealth = 0;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		state = IDLE;
		currentHealth = maxHealth;
		powerValue = powerMaxValue;
		hitbox.x = x;
		hitbox.y = y;

		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
		
	}
	public Player getPlayer() {
		return player;
	}
	
	public Playing getPlaying() {
		return playing;
	}
	public void powerAttack(int value) {
		if (powerValue >= value) {
			powerAttackActive = false;
			changePower(-10);
		}
		else {
			powerAttackActive = true;
		}
	}
	public boolean getPowerAttackActive() {
		return this.powerAttackActive;
	}
}