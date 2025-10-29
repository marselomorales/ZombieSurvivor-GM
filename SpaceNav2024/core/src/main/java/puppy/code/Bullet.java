package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Bullet {

    // Cambiar a velocidad vectorial basada en ángulo
    private float vx;
    private float vy;
    private boolean destroyed = false;
    private Sprite spr;
    
    // Tiempo de vida para auto-remoción
    private float tiempoVida;
    private float tiempoVidaMax = 2.0f; 
    
    // MOD: Constantes del mundo lógico
    private static final float WORLD_WIDTH = 1200f;
    private static final float WORLD_HEIGHT = 800f;
    
    // Velocidad constante del proyectil aumentada
    private static final float VELOCIDAD_BALA = 500f; 
    
    // Constructor modificado para disparo orientado
    public Bullet(float x, float y, float anguloRadianes, Texture tx) {
        spr = new Sprite(tx);
        spr.setPosition(x, y);
        spr.setSize(20, 20);
        
        // Calcular componentes de velocidad basados en el ángulo
        this.vx = MathUtils.cos(anguloRadianes) * VELOCIDAD_BALA;
        this.vy = MathUtils.sin(anguloRadianes) * VELOCIDAD_BALA;
        
        this.tiempoVida = 0f;
    }
    
    // Actualizar con delta time para independencia del framerate
    public void update(float delta) {
        if (destroyed) return;
        
        // Actualizar posición basada en velocidad vectorial
        spr.setPosition(spr.getX() + vx * delta, spr.getY() + vy * delta);
        
        // Actualizar tiempo de vida
        tiempoVida += delta;
        if (tiempoVida >= tiempoVidaMax) {
            destroyed = true;
        }
        
        // MOD: Usar límites del mundo lógico en lugar de la ventana
        if (spr.getX() < 0 || spr.getX() + spr.getWidth() > WORLD_WIDTH) {
            destroyed = true;
        }
        if (spr.getY() < 0 || spr.getY() + spr.getHeight() > WORLD_HEIGHT) {
            destroyed = true;
        }
    }
    
    // Compatibilidad con código existente
    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }
    
    public void draw(SpriteBatch batch) {
        if (!destroyed) {
            spr.draw(batch);
        }
    }
    
    public boolean checkCollision(Ball2 b2) {
        if(!destroyed && spr.getBoundingRectangle().overlaps(b2.getArea())){
            // Se destruyen ambos (bala y zombi)
            this.destroyed = true;
            return true;
        }
        return false;
    }
    
    public boolean isDestroyed() {return destroyed;}
    public boolean isAlive(){ return !destroyed; }
    
    public com.badlogic.gdx.math.Rectangle getArea(){
        return spr.getBoundingRectangle();
    }
    
    // Permite marcar la bala como destruida
    public void destruir() {
        this.destroyed = true;
    }
}