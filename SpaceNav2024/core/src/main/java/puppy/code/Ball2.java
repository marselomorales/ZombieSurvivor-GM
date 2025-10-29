package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Ball2: enemigo básico que persigue al jugador.
 * Compatible con Enemigo (salud/daño/movimiento base) y EntidadJuego (pos/vel).
 */
public class Ball2 extends Enemigo {
    private Sprite spr;

    // =========================
    // TemplateCompat: Constructor modificado para usar Enemigo
    // =========================
    public Ball2(int x, int y, int size, int xSpeed, int ySpeed, Texture tx, Nave4 jugador) {
        // super(x, y, ancho, alto, salud, daño, objetivo)
        super(x, y, 60, 60, 1, 1, jugador); // 1 de salud, 1 de daño base
        this.spr = new Sprite(tx);
        this.spr.setSize(60, 60);

        // Convertir velocidades legacy a velocidad base en unidades/segundo mejorada
        float velocidadMagnitud = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
        float variacion = 0.7f + (float) Math.random() * 0.6f; // 0.7 a 1.3 de variación
        this.velocidadBase = velocidadMagnitud * 25f * variacion; 

        // Validaciones de posición 
        this.spr.setPosition(posicion.x, posicion.y);
    }

    // =========================
    // Implementación delta-friendly de EntidadJuego
    // =========================
    @Override
    public void actualizar(float delta) {
        // IA de persecución mejorada con movimiento delta-friendly
        perseguirJugador(delta);

        // Actualizar sprite según posición del Enemigo / EntidadJuego
        spr.setPosition(posicion.x, posicion.y);
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        spr.draw(batch);
    }

    // =========================
    // TemplateCompat: Métodos preservados para compatibilidad
    // =========================
    public void update() {
        actualizar(Gdx.graphics.getDeltaTime());
    }

    public void draw(SpriteBatch batch) {
        dibujar(batch);
    }

    /**
     * Comportamiento eliminado - los zombis no rebotan entre sí.
     * Se mantiene el método por compatibilidad con el proyecto original.
     */
    public void checkCollision(Ball2 b2) {
        // Comportamiento eliminado - los zombis no rebotan entre sí
    }

    // =========================
    // Compatibilidad y utilidades
    // =========================
    /** Rectángulo de colisión para integrarse con chequeos existentes*/
    public Rectangle getArea() {
        return spr.getBoundingRectangle();
    }

    // Getters para compatibilidad (legacy)
    public int getXSpeed() { 
        // Estos getters ahora son legacy, mantener compatibilidad
        return (int) (velocidadBase / 35f); // Convertir de vuelta para compatibilidad
    }
    
    public void setXSpeed(int xSpeed) { 
        // Actualizar velocidad base basada en el cambio
        float nuevaVelocidad = (float) Math.sqrt(xSpeed * xSpeed + getySpeed() * getySpeed());
        this.velocidadBase = nuevaVelocidad * 35f;
    }
    
    public int getySpeed() { 
        // Estos getters ahora son legacy, mantener compatibilidad
        return (int) (velocidadBase / 35f); // Convertir de vuelta para compatibilidad
    }
    
    public void setySpeed(int ySpeed) { 
        // Actualizar velocidad base basada en el cambio
        float nuevaVelocidad = (float) Math.sqrt(getXSpeed() * getXSpeed() + ySpeed * ySpeed);
        this.velocidadBase = nuevaVelocidad * 35f;
    }
}