package com.cqu.shoot;

import java.util.Random;

/**�л������������*/
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 2;
	
	/**��ʼ��ʵ������*/
	public Airplane() {
		image = ShootGame.airplane;       
		width = image.getWidth();    
		height = image.getHeight();  
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-width);
	}
    public void step() {
    	y += speed;
    }
	public int getScore() {
		return 1;
	}
	public boolean outOfBounds() {
		return y>ShootGame.HEIGHT;
	}
}
