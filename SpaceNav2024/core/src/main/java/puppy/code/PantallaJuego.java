package puppy.code;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class PantallaJuego implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;	
	private SpriteBatch batch;
	private Sound explosionSound;
	private Music gameMusic;
	private int score;
	private int ronda;
	private int velXAsteroides; 
	private int velYAsteroides; 
	private int cantAsteroides;
	
	private Nave4 nave;
	private  ArrayList<Ball2> balls1 = new ArrayList<>();
	private  ArrayList<Ball2> balls2 = new ArrayList<>();
	private  ArrayList<Bullet> balas = new ArrayList<>();

	// NEW: Referencia al jugador para que los enemigos lo persigan
	private Nave4 jugador;

	public PantallaJuego(SpaceNavigation game, int ronda, int vidas, int score,  
			int velXAsteroides, int velYAsteroides, int cantAsteroides) {
		this.game = game;
		this.ronda = ronda;
		this.score = score;
		this.velXAsteroides = velXAsteroides;
		this.velYAsteroides = velYAsteroides;
		this.cantAsteroides = cantAsteroides;
		
		batch = game.getBatch();
		camera = new OrthographicCamera();	
		camera.setToOrtho(false, 800, 640);
		
		// MOD: Cambiar sonidos y música por temática zombi
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("zombie-death.ogg"));
		explosionSound.setVolume(1,0.5f);
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("survival-theme.wav"));
		
		gameMusic.setLooping(true);
		gameMusic.setVolume(0.5f);
		gameMusic.play();
		
	    // MOD: Cambiar textura de nave por superviviente
	    nave = new Nave4(Gdx.graphics.getWidth()/2-50,30,
	    				new Texture(Gdx.files.internal("survivor.png")), // NEW: Textura de superviviente
	    				Gdx.audio.newSound(Gdx.files.internal("player-hurt.ogg")), // NEW: Sonido de daño
	    				new Texture(Gdx.files.internal("bullet.png")), // NEW: Textura de bala
	    				Gdx.audio.newSound(Gdx.files.internal("gun-shot.ogg"))); // NEW: Sonido de disparo
	    
	    this.jugador = nave; // NEW: Guardar referencia para los enemigos
        nave.setVidas(vidas);
        
        // MOD: Crear zombis en lugar de asteroides
        Random r = new Random();
	    for (int i = 0; i < cantAsteroides; i++) {
	        Ball2 bb = new Ball2(r.nextInt((int)Gdx.graphics.getWidth()),
	  	            50+r.nextInt((int)Gdx.graphics.getHeight()-50),
	  	            20+r.nextInt(10), velXAsteroides+r.nextInt(4), velYAsteroides+r.nextInt(4), 
	  	            new Texture(Gdx.files.internal("zombie.png")), // NEW: Textura de zombi
	  	            jugador); // NEW: Pasar referencia al jugador para persecución
	  	    balls1.add(bb);
	  	    balls2.add(bb);
	  	}
	}
    
	public void dibujaEncabezado() {
		// MOD: Cambiar texto por temática zombi
		CharSequence str = "Vidas: "+nave.getVidas()+" Oleada: "+ronda;
		game.getFont().getData().setScale(2f);		
		game.getFont().draw(batch, str, 10, 30);
		game.getFont().draw(batch, "Score:"+this.score, Gdx.graphics.getWidth()-150, 30);
		game.getFont().draw(batch, "HighScore:"+game.getHighScore(), Gdx.graphics.getWidth()/2-100, 30);
	}
	
	@Override
	public void render(float delta) {
		  Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
          batch.begin();
		  dibujaEncabezado();
	      if (!nave.estaHerido()) {
		      // MOD: Colisiones entre balas y zombis
	    	  for (int i = 0; i < balas.size(); i++) {
		            Bullet b = balas.get(i);
		            b.update();
		            for (int j = 0; j < balls1.size(); j++) {    
		              if (b.checkCollision(balls1.get(j))) {          
		            	 explosionSound.play();
		            	 balls1.remove(j);
		            	 balls2.remove(j);
		            	 j--;
		            	 score +=10;
		              }   	  
		  	        }
		                
		            if (b.isDestroyed()) {
		                balas.remove(b);
		                i--;
		            }
		      }
		      
		      // MOD: Actualizar movimiento de zombis (persiguen al jugador)
		      for (Ball2 ball : balls1) {
		          ball.update();
		      }
		      
		      // MOD: Eliminar colisiones entre zombis (ya no rebotan entre sí)
		      /* Comentado: los zombis no deben rebotar entre sí
		      for (int i=0;i<balls1.size();i++) {
		    	Ball2 ball1 = balls1.get(i);   
		        for (int j=0;j<balls2.size();j++) {
		          Ball2 ball2 = balls2.get(j); 
		          if (i<j) {
		        	  ball1.checkCollision(ball2);
		          }
		        }
		      }*/
	      }
	      
	      //dibujar balas
	     for (Bullet b : balas) {       
	          b.draw(batch);
	      }
	      nave.draw(batch, this);
	      
	      // MOD: Dibujar zombis y manejar colisión con jugador
	      for (int i = 0; i < balls1.size(); i++) {
	    	    Ball2 b=balls1.get(i);
	    	    b.draw(batch);
		          // MOD: El jugador pierde vida al ser tocado por zombi
	              if (nave.checkCollision(b)) {
		            // El zombi se destruye al tocar al jugador             
	            	 balls1.remove(i);
	            	 balls2.remove(i);
	            	 i--;
              }   	  
  	        }
	      
	      if (nave.estaDestruido()) {
  			if (score > game.getHighScore())
  				game.setHighScore(score);
	    	Screen ss = new PantallaGameOver(game);
  			ss.resize(1200, 800);
  			game.setScreen(ss);
  			dispose();
  		  }
	      batch.end();
	      
	      // MOD: Nueva oleada cuando se eliminan todos los zombis
	      if (balls1.size()==0) {
			Screen ss = new PantallaJuego(game,ronda+1, nave.getVidas(), score, 
					velXAsteroides+1, velYAsteroides+1, cantAsteroides+5); // NEW: Aumento más gradual
			ss.resize(1200, 800);
			game.setScreen(ss);
			dispose();
		  }
	}
    
    public boolean agregarBala(Bullet bb) {
    	return balas.add(bb);
    }
	
	@Override
	public void show() {
		gameMusic.play();
	}

	@Override
	public void resize(int width, int height) {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	@Override
	public void hide() {}

	@Override
	public void dispose() {
		this.explosionSound.dispose();
		this.gameMusic.dispose();
	}
}