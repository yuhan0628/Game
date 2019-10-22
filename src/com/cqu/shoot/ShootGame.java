package com.cqu.shoot;
/**游戏主界面*/
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Arrays;


public class ShootGame extends JPanel {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 654;
    
    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage gameover;
    public static BufferedImage pause;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero01;
    public static BufferedImage hero02;
    
    public Hero hero = new Hero();
    public Bullet[] bullets = {};
    public FlyingObject[] flyings = {}; 
    
    private int score = 0;
    
    private int state;                                      //状态
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER =3;
    
    static {       //加载图片资源
    	try {
			background =  ImageIO.read(ShootGame.class.getResource("background.png"));
			start =  ImageIO.read(ShootGame.class.getResource("start.png"));
			gameover =  ImageIO.read(ShootGame.class.getResource("gameover.png"));
			pause =  ImageIO.read(ShootGame.class.getResource("pause.png"));
			airplane =  ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee =  ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet =  ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero01 =  ImageIO.read(ShootGame.class.getResource("hero01.png"));
			hero02 =  ImageIO.read(ShootGame.class.getResource("hero02.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**重写绘制方法*/
    public void paint(Graphics g) {             //g为画笔
    	super.paint(g);
    	g.drawImage(background,0,0,null);
    	paintHero(g);                           //this.paintHero(g);  构造方法(非静态)相互调用，this.指代本类类名      
    	paintBullet(g);
    	paintFlyingObject(g);
    	paintScore(g);                         //画分数
    	paintState(g);                         //画游戏状态
    }
    public void paintState(Graphics g) {
    	switch(state) {
    	case START:
    		g.drawImage(start,0,0,null);
    		break;
    	case PAUSE:
    		g.drawImage(pause,0,0,null);
    		break;
    	case GAME_OVER:
    		g.drawImage(gameover,0,0,null);
    		break;
    	}
    }
    public void paintHero(Graphics g) {
    	g.drawImage(hero.image,hero.x,hero.y,null);
    }
    public void paintBullet(Graphics g) {
    	for(int i=0;i<bullets.length;i++) {
    		Bullet b = bullets[i];
    		g.drawImage(b.image,b.x,b.y,null);
    	}
    }
    public void paintFlyingObject(Graphics g) {
    	for(int i=0;i<flyings.length;i++) {
    		FlyingObject f = flyings[i];
    		g.drawImage(f.image,f.x,f.y,null);
    	}
    }
    public void paintScore(Graphics g) {
    	int x = 10;
    	int y = 20;
    	g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));          //设置字体、粗细、大小
    	g.setColor(new Color(0x000000));                            //设置字体颜色
    	g.drawString("Score:"+score,x,y);            //画得分
    	g.drawString("Life:"+hero.getLife(),x,y+20);
    }
    private Timer timer;                         //定时器
    private int interval = 4;                   //时间间隔（毫秒）
    
    /**启动执行动操作*/
    public void action() {
    	MouseAdapter l = new MouseAdapter() {    //鼠标事件适配器
      	    public void mouseMoved(MouseEvent e) {   //重写鼠标移动方法
    	    if(state == RUNNING) {
    	    int x = e.getX();                      //得到x坐标
    	    int y = e.getY();                  
    	    hero.moveTo(x, y);                     //英雄机移动
    	       }
    	    }
      	    public void mouseClicked(MouseEvent e) {
      	    switch(state) {
      	    case START:
      	    	state = RUNNING;
      	    	break;
      	    case GAME_OVER:                         //游戏结束归零
      	    	hero = new Hero();
      	    	flyings = new FlyingObject[0];
      	    	bullets = new Bullet[0];
      	    	score = 0;
      	    	state = START;
      	    	break;
      	       }	
      	    }
      	    public void mouseExited(MouseEvent e) {
      		  if(state!=GAME_OVER) {
      			  state = PAUSE;
      		  }
      	  }
      	    public void mouseEntered(MouseEvent e) {
      		  if(state == PAUSE) {
      			  state = RUNNING;
      		  }
      	  }
    	};
    	this.addMouseListener(l);                //鼠标点击监听
    	this.addMouseMotionListener(l);          //给当前面板添加鼠标滑动监听
    	
    	timer = new Timer();                     //创建定时器对象
    	timer.schedule(new TimerTask(){          //定时触发   创建 匿名内部类
    		public void run() {                  //重写run方法,run在TimerTask抽象类中
    			if(state ==RUNNING) {
    			enterAction();                   //飞行物入场
    			stepAction();                    //飞行物走步
    			shootAction();
    			bangAction();                    //子弹打击
    			repaint();                       //重绘(调用paint方法)
    			outOfBoundsAction();
    			checkGameOverAction();
    			}
    		}
    	},interval,interval);                    //schedule(执行的方法，时间间隔，时间间隔)            
    }	
    public void outOfBoundsAction() {            //删除越界飞行物
    	int index = 0;
    	FlyingObject[] flyingLives = new FlyingObject[flyings.length];
    	for(int i=0;i<flyings.length;i++) {
    		FlyingObject f = flyings[i];          //得到每一个飞行物
    		if(!f.outOfBounds()) {               //若不出界
    			flyingLives[index++] = f;
    		}
    	}
    	flyings = Arrays.copyOf(flyingLives,index);
    	
    	index = 0;
    	Bullet[] bulletLives = new Bullet[bullets.length];
    	for(int i=0;i<bullets.length;i++) {
    		Bullet b = bullets[i];
    		if(!b.outOfBounds()) {
    			bulletLives[index++] = b;
    		}
    	}
    	bullets = Arrays.copyOf(bulletLives,index);
    }
    public void checkGameOverAction() {
    	if(isGameOver()) {
    		state = GAME_OVER;
    	}
    }
    public boolean isGameOver() {
    	for(int i=0;i<flyings.length;i++) {
    		int index = -1;                      //记录撞上飞行物索引
    		FlyingObject obj = flyings[i];
    		if(hero.hit(obj)) {                  //撞上
    			hero.substractLife();            //减命
    			hero.setDoubleFire(0);           //单倍火力
    			index = i;                       //记录索引
    		}
    		if(index!=-1) {
    			FlyingObject t = flyings[index];       
        		flyings[index] = flyings[flyings.length-1];
        		flyings[flyings.length-1] = t;
        		flyings = Arrays.copyOf(flyings,flyings.length-1);
    		}
    	}
    	return hero.getLife()<=0;
    } 
    public void bangAction() {
    	for(int i=0;i<bullets.length;i++) {
    		Bullet b = bullets[i];
    		bang(b);
    	}
    }
    int shootIndex = 0;                          //控制射击频率
    public void shootAction() {
    	shootIndex++;
    	if(shootIndex % 35==0) {                   //300毫秒发射一次
    		Bullet[] bs = hero.shoot();
    		bullets = Arrays.copyOf(bullets,bullets.length+bs.length); 
    		System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length);    //追加子弹
    	}
    } 
    int flyEnterIndex = 0;                       //飞行物入场计数
    public void enterAction() {
    	flyEnterIndex++;
    	if(flyEnterIndex % 30==0) {                //每400毫秒生成一个飞行物
    		FlyingObject obj = nextOne();
    		flyings = Arrays.copyOf(flyings,flyings.length+1);      //扩容，初始为0
    		flyings[flyings.length-1] = obj;                        //将敌人赋给obj
    	}
    }
    public void stepAction() {
    	for(int i=0;i<flyings.length;i++) {
    		flyings[i].step();
    	}
    	for(int i=0;i<bullets.length;i++) {
    		bullets[i].step();
        }
    	hero.step();
    }
    public static FlyingObject nextOne() {      //工厂方法生产对象，用静态      FlyingObject[]数组为返回值
    	Random rand = new Random();
    	int type = rand.nextInt(20);
    	if(type==0) {                         
    		return new Bee();
    	}else {
    		return new Airplane();
    	}
    }
    public void bang(Bullet b) {
    	int index = -1;
    	for(int i=0;i<flyings.length;i++) {       //遍历所有敌人
    		FlyingObject obj = flyings[i];        //得到每一个敌人
    		if(obj.shootBy(b)) {                  //判断子弹是否击中
    			index = i;                        //记录被击中的飞机的下标
    		    break;
    		}
    	}
    	if(index != -1) {                          //有击中的飞机
    		FlyingObject one = flyings[index];
    		
    		/**删除被击中的飞行物*/
    		FlyingObject t = flyings[index];       
    		flyings[index] = flyings[flyings.length-1];
    		flyings[flyings.length-1] = t;
    		/**删除数组中的最后一个元素*/
    		flyings = Arrays.copyOf(flyings,flyings.length-1);      //数组的缩容
    		/**得分或者奖励*/
    		if(one instanceof Enemy) {             //判断是否为敌人
    			Enemy e = (Enemy)one;              //飞行物强转为敌人
    			score += e.getScore();
     		}else if(one instanceof Award) {       //判断是否为奖励
     			Award a = (Award)one;              //飞行物强转为奖励
     			int type = a.getType();
     			switch(type) {
     			case Award.DOUBLE_LIFE:
     				hero.addDoubleFire();
     				break;
     			case Award.LIFE:
     				hero.addLife();
     				break;
     			}
     		}
    	}
    }
    public static void main(String[] args) {
		JFrame frame = new JFrame("ShootGame");  //画框
		ShootGame game = new ShootGame();        //面板
		frame.add(game);                         //将面板加到画框上
		frame.setSize(WIDTH,HEIGHT);             //大小
		frame.setAlwaysOnTop(true);              //总在最上
		frame.setDefaultCloseOperation
		              (JFrame.EXIT_ON_CLOSE);    //默认关闭
		frame.setLocationRelativeTo(null);       //初始位置
		frame.setVisible(true);                  //显示-尽快调用paint方法
        
		game.action();                           //静态方法调用非静态方法，必须通过new对象
	    
	}
	
       
}
