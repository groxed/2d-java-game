package com.groxed.game.entities;

import com.groxed.game.level.Level;

public abstract class Mob extends Entity {
	
	protected String name;
	protected int speed;
	protected boolean isMoving;
	protected int movingDir = 1;
	protected int scale = 1;

	public Mob(Level level, String name, int x, int y, int speed) {
		super(level);
		this.name = name;
		this.speed = speed;
		this.x = x;
		this.y = y;
		// TODO Auto-generated constructor stub
	}
	
	public void move(int xa, int ya) {
		if(xa!=0 && ya!=0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		
		if(!hasCollided(xa, ya)) {
			if(ya<0) {
				movingDir = 0;
			}
			if(ya>0) {
				movingDir = 1;
			}
			if(xa<0) {
				movingDir = 2;
			}
			if(xa>0) {
				movingDir = 3;
			}
			x+=xa*speed;
			y+=ya*speed;
		}
	}
	
	public abstract boolean hasCollided(int xa, int ya);
	
	public String getName() {
		return name;
	}

	
}
