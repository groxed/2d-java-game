package com.groxed.game;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

import com.groxed.game.entities.Player;
import com.groxed.game.gfx.Colors;
import com.groxed.game.gfx.Screen;
import com.groxed.game.gfx.SpriteSheet;
import com.groxed.game.level.Level;
import com.groxed.game.gfx.Font;

public class Game extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	public boolean running = false;
	
	public int tickCount = 0;
	
	public final static int WIDTH = 160;
	public final static int HEIGHT = WIDTH / 12*9;
	public final static int SCALE = 5;
	public final static String NAME = "Game";
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); //returns said image data as pixels array
	private int[] colors = new int[6*6*6]; //contains colors (6 shades for 6 colors(r,g,b)
	
	
	private Screen screen;
	public InputHandler input;
	public Level level;
	public Player player;
	
	public JFrame frame;
	
	public Game() {
		setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		
		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void init() {
		int index = 0;
		for(int r=0; r<6; r++) {
			for(int g=0; g<6; g++) {
				for(int b=0; b<6; b++) {
					int rr = (r*(255/5));
					int gg = (g*(255/5));
					int bb = (b*(255/5));
					
					colors[index++] = rr<<16 | gg<<8 | bb;
				}
			}
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/spritesheet.png"));
		input = new InputHandler(this);
		level = new Level(64, 64);
		player = new Player(level, 0, 0, input);
		level.addEntity(player);
	}

	public synchronized void start() {
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop() {
		running = false;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000/60D; //how many time(nanoseconds) per tick
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while(running) {
			long now = System.nanoTime();
			delta += (now-lastTime)/nsPerTick; //if time needed for one tick has passed(>=1) then...
			lastTime = now;
			boolean shouldRender = true;
			
			while(delta>=1) {//if time needed for one tick has passed(>=1) then...
				ticks++;
				tick();
				delta-=1;
				shouldRender = true; //render and add frames
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(shouldRender) {
				frames++;
				render();
			}
			
			
			if((System.currentTimeMillis()-lastTimer)>=1000) { //if one second passed
				lastTimer +=1000;
				System.out.println(ticks+" ticks "+frames+" frames");
				frames = 0;
				ticks = 0;
			}
			
		}
	}
	

	
	public void tick() {
		tickCount++;
		level.tick();
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs==null) {
			createBufferStrategy(3);
			return;
		}
		
		int xOffset = player.x - (screen.width/2);
		int yOffset = player.y - (screen.height/2);
		level.renderTiles(screen, xOffset, yOffset);
		
		
		
		
		
		for(int x=0; x<level.width; x++) {
			int color = Colors.get(-1, -1, -1, 000);
			if(x%10==0 && x!= 0) {
				color = Colors.get(-1, -1, -1, 500);
			}
			Font.render((x%10) + "", screen, 0 + (x*8),0, color);
		}
		
		level.renderEntities(screen);
		
		for(int y =0; y<screen.height; y++) {
			for(int x =0; x<screen.width; x++) {
				int colorCode = screen.pixels[x + y*screen.width];
				if(colorCode < 255) {
					pixels[x+y*WIDTH] = colors[colorCode];
				}
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game().start();
	}



}
