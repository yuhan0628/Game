package com.cqu.shoot;
import java.util.Random;
public class Bee extends FlyingObject implements Award {
	private int xspeed = 1;
	private int yspeed = 2;
	private int awardType;
	
	public Bee() {
		image = ShootGame.bee;       //图片
		width = image.getWidth();    //图片的宽，自动获取
		height = image.getHeight();  //图片的高，自动获取
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-width);
		awardType = rand.nextInt(2);
	}
	public void step() {
		x += xspeed;
		y += yspeed;
		if(x<0) {
			xspeed = 1;            //往右
		}
		if(x>ShootGame.WIDTH-width) {
			xspeed = -1;           //往左
		}
	}
	
	 public int getType() {
		 return awardType;
	 }
	 public boolean outOfBounds() {
			return y>ShootGame.HEIGHT;
		}
}
