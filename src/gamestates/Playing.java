package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;
import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
	private Player player;
	private LevelManager levelManager;
	
	private EnemyManager enemyManager;
	private ObjectManager objectManager;
	private PauseOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused = false;

	private int xLvlOffset;
	private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
	private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
	private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
	private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;

	private BufferedImage backgroundImg, bigCloud, smallCloud;
	private int[] smallCloudsPos;
	private Random rnd = new Random();
	private boolean playerDying = false;
	private boolean gameOver;
	private boolean lvlCompleted;

	public Playing(Game game) {
		super(game);
		initClasses();

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
		bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
		smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
		smallCloudsPos = new int[8];
		for (int i = 0; i < smallCloudsPos.length; i++)
			smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));
		loadStartLevel();
	}

	private void loadStartLevel() {
		// TODO Auto-generated method stub
		
	}

	private void initClasses() {
		levelManager = new LevelManager(game);
		enemyManager = new EnemyManager(this);
		objectManager = new ObjectManager(this);
		
		player = new Player(Game.GAME_WIDTH / 2 - 50, Game.GAME_HEIGHT - 150, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());

		pauseOverlay = new PauseOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);
		levelCompletedOverlay = new LevelCompletedOverlay(this);
	}

	@Override
	public void update() {
		if(objectManager.checkComplete() && lvlCompleted == false)
		lvlCompleted = true;
		if (paused) {
			pauseOverlay.update();
		} else if (lvlCompleted) {
			levelCompletedOverlay.update();
		} else if (gameOver) {
			gameOverOverlay.update();
		} else if (playerDying) {
			player.update();
		} else {
			levelManager.update();
			objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			player.update();
			checkCloseToBorder();
		}

	}

	private void checkCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int diff = playerX - xLvlOffset;

		if (diff > rightBorder)
			xLvlOffset += diff - rightBorder;
		else if (diff < leftBorder)
			xLvlOffset += diff - leftBorder;

		if (xLvlOffset > maxLvlOffsetX)
			xLvlOffset = maxLvlOffsetX;
		else if (xLvlOffset < 0)
			xLvlOffset = 0;

	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

		
//		drawClouds(g);

//		levelManager.draw(g, xLvlOffset);
		player.render(g, xLvlOffset);
		enemyManager.draw(g, xLvlOffset);
		objectManager.draw(g, xLvlOffset);
		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if (lvlCompleted)
			levelCompletedOverlay.draw(g);
		else if (gameOver)
			gameOverOverlay.draw(g);
		
	}

	private void drawClouds(Graphics g) {

		for (int i = 0; i < 3; i++)
			g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

		for (int i = 0; i < smallCloudsPos.length; i++)
			g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!gameOver)
			{
			if (e.getButton() == MouseEvent.BUTTON1)
				{
					player.powerAttack(20);
				}
			}
				
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_W:
				player.setUp(true);
				break;
			case KeyEvent.VK_S:
				player.setDown(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_J:
				
				player.powerAttack(20);
				if(player.getPowerAttackActive() == false)
					{
						objectManager.addBullet((int)player.getHitbox().x - 35,(int)player.getHitbox().y, 1);
						player.setAttacking(true);
					}
				break;
			case KeyEvent.VK_K:
				
				player.powerAttack(40);
				if(player.getPowerAttackActive() == false)
					{
						objectManager.addBullet((int)player.getHitbox().x - 15,(int)player.getHitbox().y, 1);
						objectManager.addBullet((int)player.getHitbox().x - 55,(int)player.getHitbox().y, 1);
						player.setDoubleattacking(true);
					}
				break;
			case KeyEvent.VK_ESCAPE:
				paused = !paused;
				break;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!gameOver)
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_W:
				player.setUp(false);
				break;
			case KeyEvent.VK_S:
				player.setDown(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			}

	}

	public void mouseDragged(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mousePressed(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		} else
			gameOverOverlay.mousePressed(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseReleased(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		} else
			gameOverOverlay.mouseReleased(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseMoved(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
		} else
			gameOverOverlay.mouseMoved(e);

	}
	
	public void setLevelCompleted(boolean levelCompleted) {
		this.lvlCompleted = levelCompleted;
	}
	
	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}

	public void unpauseGame() {
		paused = false;
	}

	public void windowFocusLost() {
		player.resetDirBooleans();
	}

	public Player getPlayer() {
		return player;
	}
	
	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
		
	}

	public void resetAll() {
		gameOver = false;
		paused = false;
		lvlCompleted = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
		objectManager.resetAllObjects();
		
	}
	
	public void setGameOver(boolean gameover) {
		this.gameOver = gameover;
	}
	
	public void setComplete(boolean lvlComplete) {
		this.lvlCompleted = lvlComplete;
	}
}