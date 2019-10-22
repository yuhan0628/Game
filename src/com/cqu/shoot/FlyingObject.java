package com.cqu.shoot;
import java.awt.image.BufferedImage;
public abstract class FlyingObject {
  protected int width;
  protected int height;
  protected int x;
  protected int y;
  protected BufferedImage image;              //图片
  
  public abstract void step();                //四个类的方法体不同，用抽象类
  public boolean shootBy(Bullet b) {
	  int x = b.x;
	  int y = b.y;
	  return x>this.x && x<this.x+width && y>this.y && y<this.y+height;
  }
  public abstract boolean outOfBounds();
}
