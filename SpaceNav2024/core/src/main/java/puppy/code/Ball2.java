package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Ball2 {
	private int x;
    private int y;
    private int xSpeed;
    private int ySpeed;
    private Sprite spr;
    
    // NEW: Referencia al jugador para persecución
    private Nave4 jugador;
    private final float VELOCIDAD_PERSECUCION = 1.5f;

    public Ball2(int x, int y, int size, int xSpeed, int ySpeed, Texture tx, Nave4 jugador) { // NEW: Parámetro jugador
    	spr = new Sprite(tx);
    	this.jugador = jugador; // NEW: Guardar referencia al jugador
    	this.x = x; 
 	
        //validar que borde de esfera no quede fuera
    	if (x-size < 0) this.x = x+size;
    	if (x+size > Gdx.graphics.getWidth())this.x = x-size;
         
        this.y = y;
        //validar que borde de esfera no quede fuera
    	if (y-size < 0) this.y = y+size;
    	if (y+size > Gdx.graphics.getHeight())this.y = y-size;
    	
        spr.setPosition(x, y);
        this.setXSpeed(xSpeed);
        this.setySpeed(ySpeed);
    }
    
    public void update() {
        // MOD: Comportamiento de persecución al jugador
        if (jugador != null) {
            Vector2 direccion = new Vector2(
                jugador.getXFloat() - x,
                jugador.getYFloat() - y
            );
            
            // Normalizar la dirección y aplicar velocidad
            direccion.nor();
            x += direccion.x * VELOCIDAD_PERSECUCION;
            y += direccion.y * VELOCIDAD_PERSECUCION;
        } else {
            // Comportamiento original como fallback
            x += getXSpeed();
            y += getySpeed();

            if (x+getXSpeed() < 0 || x+getXSpeed()+spr.getWidth() > Gdx.graphics.getWidth())
                setXSpeed(getXSpeed() * -1);
            if (y+getySpeed() < 0 || y+getySpeed()+spr.getHeight() > Gdx.graphics.getHeight())
                setySpeed(getySpeed() * -1);
        }
        
        spr.setPosition(x, y);
    }
    
    public Rectangle getArea() {
    	return spr.getBoundingRectangle();
    }
    
    public void draw(SpriteBatch batch) {
    	spr.draw(batch);
    }
    
    // MOD: Eliminar colisiones entre enemigos (zombis no rebotan)
    public void checkCollision(Ball2 b2) {
        // Comportamiento eliminado - los zombis no rebotan entre sí
    }
    
	public int getXSpeed() {
		return xSpeed;
	}
	public void setXSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}
	public int getySpeed() {
		return ySpeed;
	}
	public void setySpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}
}