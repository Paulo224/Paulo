package com.UltraUI.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.UltraUI.com.main.Game;
import com.UltraUI.graficos.Spritesheet;
import com.UltraUI.world.Camera;
import com.UltraUI.world.World;

public class Player extends Entity{
	//Public
		public boolean right,up,left,down;
		public int right_dir = 0,left_dir = 1;
		public int dir = right_dir;
		public double speed = 1;
		public double life = 100,maxlife = 100;
		public int ammo = 0;
		public boolean isDamage = false;
		public boolean shoot = false;
	//Private
		private int frames = 0,maxframes = 5,index = 0,maxindex = 3;
		private boolean moved = false;
		private BufferedImage[] rightPlayer;
		private BufferedImage[] leftPlayer;
		private BufferedImage playerDamage;
		private int damageFrames = 0; 
		private boolean Arma = false;
		

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 +(i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 +(i*16), 16, 16, 16);
		}
	}

	public void tick() {
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			x+=speed;
			dir = right_dir;
		}
		else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			x-=speed;
			dir = left_dir;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
			
		}
		else if(down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
			
		}
		
		if(moved) {
			frames++;
			if(frames == maxframes) {
				frames = 0;
				index++;
				if(index > maxindex) {
					index = 0;
				}
			}
		}
			checkCollisionLifePack();
			checkCollisionAmmo();
			checkCollisionGun();
			
			if(isDamage) {
				this.damageFrames++;
				if(this.damageFrames == 5) {
					this.damageFrames = 0;
					isDamage = false;
				}
			}
			
			if(shoot) {
				//Criar bala e atirar.
				shoot = false;
				if(Arma && ammo > 0) {
				ammo--;
				int dx = 0;
				int px = 0;
				int py = 6;
				if(dir == right_dir) {
					 px = 18;
					 dx = 1;
				}else {
					 px = -8;
					 dx = -1;
				}
			
				BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,0);
				Game.bullets.add(bullet);
				}
		        }		
			
			if(life < 0.1) {
				//Game Over
				Game.gameState = "Game_Over";
				
			}
			
			
			Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
			Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
			
			
			
				
	}
	public void checkCollisionGun(){
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof Weapon) {
					if(Entity.isColidding(this, atual)) {
						//System.out.println("Pegar arma");
						Arma = true;
						Game.entities.remove(atual);
					
				}
			}
		}
	}
	
	public void checkCollisionAmmo(){
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof Bullet) {
					if(Entity.isColidding(this, atual)) {
						ammo+=10;
						Game.entities.remove(atual);
					}
				}
			}
		}
	

	private void checkCollisionLifePack() {
		if(life < 100) {
			for(int i = 0; i < Game.entities.size(); i++) {
				Entity atual = Game.entities.get(i);
				if(atual instanceof Lifepack) {
					if(Entity.isColidding(this, atual)) {
						life+=25;
						if(life > 100)
							life = 100;
						Game.entities.remove(atual);
					}
				}
			}
		}
		}

	public void render(Graphics g) {
		if(!isDamage) {
		
		if(dir == right_dir) {
			g.drawImage(rightPlayer[index],this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(Arma) {
				//Arma direita.
				g.drawImage(Entity.GUN_RIGHT, this.getX() + 6- Camera.x, this.getY() -2 - Camera.y,null);
			}
		}else if(dir == left_dir) {
			g.drawImage(leftPlayer[index],this.getX() - Camera.x,this.getY() - Camera.y,null);
			if(Arma) {
				//Arma Esquerda.
				g.drawImage(Entity.GUN_LEFT, this.getX() - 5 - Camera.x, this.getY() -2 - Camera.y,null);
			}
		}
		
	}else {
		shoot = false;
		g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y,null);
		
	
	}
	}
}
