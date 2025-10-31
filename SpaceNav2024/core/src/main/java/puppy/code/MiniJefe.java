package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MiniJefe extends Enemigo {
    private Sprite spr;
    private static final int SALUD_BASE = 150;
    private static final int DANIO_BASE = 3;
    
    public MiniJefe(float x, float y, Texture textura, Nave4 jugador) {
        super(x, y, 120, 120, SALUD_BASE, DANIO_BASE, jugador);
        this.spr = new Sprite(textura);
        this.spr.setSize(120, 120);
        
        // Velocidad base aumentada para mayor desafío
        this.velocidadBase = 45f;
    }
    
    @Override
    public void actualizar(float delta) {
        // Usar IA de persecución delta-friendly
        perseguirJugador(delta);
        spr.setPosition(posicion.x, posicion.y);
    }
    
    @Override
    public void dibujar(SpriteBatch batch) {
        spr.draw(batch);
    }
    
    @Override
    public void escalarPorRonda(int ronda) {
        float factor = 1.0f + (ronda - 1) * 0.5f; 
        
        // Escalar atributos de combate
        salud = (int)(SALUD_BASE * factor);
        danio = (int)(DANIO_BASE * factor);
        
        // Escalar velocidad base (en unidades/segundo)
        velocidadBase *= factor;
    }
}
