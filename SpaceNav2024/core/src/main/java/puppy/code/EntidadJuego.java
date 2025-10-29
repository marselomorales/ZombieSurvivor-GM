package puppy.code;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class EntidadJuego {
    protected Vector2 posicion;
    protected Vector2 velocidad;
    protected Rectangle area;
    
    public EntidadJuego(float x, float y, float ancho, float alto) {
        this.posicion = new Vector2(x, y);
        this.velocidad = new Vector2();
        this.area = new Rectangle(x, y, ancho, alto);
    }
    
    // TemplateCompat: Métodos abstractos para el ciclo de juego
    public abstract void actualizar(float delta);
    public abstract void dibujar(SpriteBatch batch);
    
    // Getters y setters encapsulados
    public Vector2 getPosicion() { return posicion; }
    public void setPosicion(float x, float y) { 
        posicion.set(x, y); 
        area.setPosition(x, y);
    }
    
    public Vector2 getVelocidad() { return velocidad; }
    public void setVelocidad(float x, float y) { velocidad.set(x, y); }
    
    public Rectangle getArea() { return area; }
    
    // ZombieUpdate: Método para escalar por ronda
    public void escalarPorRonda(int ronda) {
        // Implementación base, las subclases pueden sobreescribir
        velocidad.x *= (1 + (ronda * 0.1f));
        velocidad.y *= (1 + (ronda * 0.1f));
    }
}