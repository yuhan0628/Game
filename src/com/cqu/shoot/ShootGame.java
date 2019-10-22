package com.cqu.shoot;
/**��Ϸ������*/
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
    
    private int state;                                      //״̬
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER =3;
    
    static {       //����ͼƬ��Դ
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
    /**��д���Ʒ���*/
    public void paint(Graphics g) {             //gΪ����
    	super.paint(g);
    	g.drawImage(background,0,0,null);
    	paintHero(g);                           //this.paintHero(g);  ���췽��(�Ǿ�̬)�໥���ã�this.ָ����������      
    	paintBullet(g);
    	paintFlyingObject(g);
    	paintScore(g);                         //������
    	paintState(g);                         //����Ϸ״̬
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
    	g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,20));          //�������塢��ϸ����С
    	g.setColor(new Color(0x000000));                            //����������ɫ
    	g.drawString("Score:"+score,x,y);            //���÷�
    	g.drawString("Life:"+hero.getLife(),x,y+20);
    }
    private Timer timer;                         //��ʱ��
    private int interval = 4;                   //ʱ���������룩
    
    /**����ִ�ж�����*/
    public void action() {
    	MouseAdapter l = new MouseAdapter() {    //����¼�������
      	    public void mouseMoved(MouseEvent e) {   //��д����ƶ�����
    	    if(state == RUNNING) {
    	    int x = e.getX();                      //�õ�x����
    	    int y = e.getY();                  
    	    hero.moveTo(x, y);                     //Ӣ�ۻ��ƶ�
    	       }
    	    }
      	    public void mouseClicked(MouseEvent e) {
      	    switch(state) {
      	    case START:
      	    	state = RUNNING;
      	    	break;
      	    case GAME_OVER:                         //��Ϸ��������
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
    	this.addMouseListener(l);                //���������
    	this.addMouseMotionListener(l);          //����ǰ��������껬������
    	
    	timer = new Timer();                     //������ʱ������
    	timer.schedule(new TimerTask(){          //��ʱ����   ���� �����ڲ���
    		public void run() {                  //��дrun����,run��TimerTask��������
    			if(state ==RUNNING) {
    			enterAction();                   //�������볡
    			stepAction();                    //�������߲�
    			shootAction();
    			bangAction();                    //�ӵ����
    			repaint();                       //�ػ�(����paint����)
    			outOfBoundsAction();
    			checkGameOverAction();
    			}
    		}
    	},interval,interval);                    //schedule(ִ�еķ�����ʱ������ʱ����)            
    }	
    public void outOfBoundsAction() {            //ɾ��Խ�������
    	int index = 0;
    	FlyingObject[] flyingLives = new FlyingObject[flyings.length];
    	for(int i=0;i<flyings.length;i++) {
    		FlyingObject f = flyings[i];          //�õ�ÿһ��������
    		if(!f.outOfBounds()) {               //��������
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
    		int index = -1;                      //��¼ײ�Ϸ���������
    		FlyingObject obj = flyings[i];
    		if(hero.hit(obj)) {                  //ײ��
    			hero.substractLife();            //����
    			hero.setDoubleFire(0);           //��������
    			index = i;                       //��¼����
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
    int shootIndex = 0;                          //�������Ƶ��
    public void shootAction() {
    	shootIndex++;
    	if(shootIndex % 35==0) {                   //300���뷢��һ��
    		Bullet[] bs = hero.shoot();
    		bullets = Arrays.copyOf(bullets,bullets.length+bs.length); 
    		System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length);    //׷���ӵ�
    	}
    } 
    int flyEnterIndex = 0;                       //�������볡����
    public void enterAction() {
    	flyEnterIndex++;
    	if(flyEnterIndex % 30==0) {                //ÿ400��������һ��������
    		FlyingObject obj = nextOne();
    		flyings = Arrays.copyOf(flyings,flyings.length+1);      //���ݣ���ʼΪ0
    		flyings[flyings.length-1] = obj;                        //�����˸���obj
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
    public static FlyingObject nextOne() {      //�����������������þ�̬      FlyingObject[]����Ϊ����ֵ
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
    	for(int i=0;i<flyings.length;i++) {       //�������е���
    		FlyingObject obj = flyings[i];        //�õ�ÿһ������
    		if(obj.shootBy(b)) {                  //�ж��ӵ��Ƿ����
    			index = i;                        //��¼�����еķɻ����±�
    		    break;
    		}
    	}
    	if(index != -1) {                          //�л��еķɻ�
    		FlyingObject one = flyings[index];
    		
    		/**ɾ�������еķ�����*/
    		FlyingObject t = flyings[index];       
    		flyings[index] = flyings[flyings.length-1];
    		flyings[flyings.length-1] = t;
    		/**ɾ�������е����һ��Ԫ��*/
    		flyings = Arrays.copyOf(flyings,flyings.length-1);      //���������
    		/**�÷ֻ��߽���*/
    		if(one instanceof Enemy) {             //�ж��Ƿ�Ϊ����
    			Enemy e = (Enemy)one;              //������ǿתΪ����
    			score += e.getScore();
     		}else if(one instanceof Award) {       //�ж��Ƿ�Ϊ����
     			Award a = (Award)one;              //������ǿתΪ����
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
		JFrame frame = new JFrame("ShootGame");  //����
		ShootGame game = new ShootGame();        //���
		frame.add(game);                         //�����ӵ�������
		frame.setSize(WIDTH,HEIGHT);             //��С
		frame.setAlwaysOnTop(true);              //��������
		frame.setDefaultCloseOperation
		              (JFrame.EXIT_ON_CLOSE);    //Ĭ�Ϲر�
		frame.setLocationRelativeTo(null);       //��ʼλ��
		frame.setVisible(true);                  //��ʾ-�������paint����
        
		game.action();                           //��̬�������÷Ǿ�̬����������ͨ��new����
	    
	}
	
       
}
