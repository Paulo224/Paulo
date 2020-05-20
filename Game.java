package com.UltraUI.com.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.UltraUI.entities.BulletShoot;
import com.UltraUI.entities.Enemy;
import com.UltraUI.entities.Entity;
import com.UltraUI.entities.Player;
import com.UltraUI.graficos.Spritesheet;
import com.UltraUI.graficos.UI;
import com.UltraUI.world.World;

public class Game extends Canvas implements Runnable,KeyListener{
	
	/**
	 * 
	 */
	//Public 
		public static World world;
		public static Player player;
		public static Random rand;
		public UI ui;
		public static Spritesheet spritesheet;
		public static List<Enemy> enemies;
		public static List<Entity> entities;
		public static List<BulletShoot> bullets;	
		public static JFrame frame;
		public static final int WIDTH = 240;
		public static final int HEIGHT = 160;
		public static String gameState = "MENU";
		public static Menu menu;
	//
	//Private
		private BufferedImage image;
		private static final long serialVersionUID = 1L;
		private Thread thread;
		private boolean isRunnable = true;
		private final int SCALE = 3;
		private int CUR_LEVEL = 1,MAX_LEVEL = 4;
		private boolean showMessageGameOver = true;
		private int framesGameOver = 0;
		private boolean restartGame = false;
	//
	public Game() {
		rand = new Random();
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos.
		
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0,16,16));
		entities.add(player);
		world = new World("/level1.png");
		menu = new Menu();
	}
	
	
	
	public void initFrame() {
		
		frame = new JFrame("Zelda");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunnable = true;
		thread.start();
	}
	public synchronized void stop() {
		isRunnable = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == "Normal") {
			this.restartGame = false;
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			 e.tick();
		}
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				//Avançar para o próximo level
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				//System.out.println(newWorld);
				World.restartGame(newWorld);
			 }
			}else if(gameState == "Game_Over") {
				this.framesGameOver++;
				if(this.framesGameOver == 30) {
					this.framesGameOver = 0;
					if(this.showMessageGameOver) {
						this.showMessageGameOver = false;
					}else {
						this.showMessageGameOver = true;
					}
				}
			}
					
					
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "Normal";
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
				
			}else if(gameState == "MENU") {
				menu.tick();
			}
	}		
			
	
	public void render() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//Renderizçao do  Jogo
		//Graphics2D g2 = (Graphics2D) g;
		
		world.render(g);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial",Font.BOLD,20));
		g.drawString("Munição: " + player.ammo, 595, 465);
		if(gameState == "Game_Over") {
			Graphics2D g2 = (Graphics2D) g; 
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial",Font.ITALIC,110));
			g.drawString("Game Over", 90, 270);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial",Font.ITALIC,35));
			if(showMessageGameOver) 
				g.drawString(">Pressione Enter para reiniciar<", 120, 350);
		}else if(gameState == "MENU") {
		 menu.render(g);
		}
		bs.show();
			
}
	
	
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunnable) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >=1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS"+frames);
				frames = 0;
				timer+=1000;
			}
		}
		
		stop();
		
	}

	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
		
				player.right = true;
			
	}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
			e.getKeyCode() == KeyEvent.VK_A) {
		
				player.left = true;
		
	}
		if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W){
		
				player.up = true;
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
			e.getKeyCode() == KeyEvent.VK_S) {
		
				player.down = true;
			
	}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
		}
		
	}	

	public void keyReleased(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
		
				player.right = false;
				
	}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
			e.getKeyCode() == KeyEvent.VK_A) {
		
				player.left = false;
				
	}
		if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W){
		
				player.up = false;
				
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
			e.getKeyCode() == KeyEvent.VK_S) {
		
				player.down = false;
			
	}
	}	
}
