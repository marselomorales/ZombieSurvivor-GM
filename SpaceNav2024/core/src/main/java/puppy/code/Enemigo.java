package puppy.code;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemigo extends EntidadJuego implements Daniable {
    protected int salud;
    protected int saludMaxima;
    protected int danio;
    protected boolean invulnerable = false;
    protected Nave4 jugador;
    
    // ZombieUpdate: Velocidad base aumentada para mayor dinamismo
    protected float velocidadBase = 60f; // 60f unidades/segundo
    
    public Enemigo(float x, float y, float ancho, float alto, int salud, int danio, Nave4 jugador) {
        super(x, y, ancho, alto);
        this.salud = salud;
        this.saludMaxima = salud;
        this.danio = danio;
        this.jugador = jugador;
    }
    
    @Override
    public void recibirDanio(int danio) {
        if (!invulnerable) {
            salud -= danio;
        }
    }
    
    @Override
    public boolean estaVivo() {
        return salud > 0;
    }
    
    @Override
    public int getSalud() {
        return salud;
    }
    
    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
    
    @Override
    public boolean estaInvulnerable() {
        return invulnerable;
    }
    
    public int getDanio() { 
        return danio; 
    }
    
    // =========================================
    // IA de persecución "Seek" mejorada
    // =========================================
    
    /**
     * Persigue al jugador usando comportamiento "Seek" suave
     * @param delta Tiempo transcurrido desde el último frame (en segundos)
     */
    protected void perseguirJugador(float delta) {
        if (jugador != null && jugador.estaVivo()) {
            // Obtener posición del jugador
            float jugadorX = jugador.getXFloat() + jugador.getSprite().getWidth() / 2;
            float jugadorY = jugador.getYFloat() + jugador.getSprite().getHeight() / 2;
            
            // Obtener posición actual del enemigo (centro)
            float enemigoX = posicion.x + area.width / 2;
            float enemigoY = posicion.y + area.height / 2;
            
            // Calcular dirección hacia el jugador
            Vector2 direccion = new Vector2(jugadorX - enemigoX, jugadorY - enemigoY);
            
            // Normalizar dirección (vector unitario)
            if (direccion.len() > 0.1f) {
                direccion.nor();
                
                // Aplicar movimiento con velocidad en unidades/segundo + variación orgánica
                float variacionVelocidad = 0.8f + (float) Math.random() * 0.4f; // 0.8 a 1.2
                float velocidadActual = velocidadBase * delta * variacionVelocidad;
                posicion.x += direccion.x * velocidadActual;
                posicion.y += direccion.y * velocidadActual;
                
                // Actualizar área de colisión
                area.setPosition(posicion.x, posicion.y);
            }
        }
    }
    
    // =========================================
    // ZombieUpdate: Escalado por ronda mejorado
    // =========================================
    @Override
    public void escalarPorRonda(int ronda) {
        float factor = 1.0f + (ronda - 1) * 0.3f; // Aumenta 30% por ronda
        
        // Escalar atributos de combate
        salud = (int)(saludMaxima * factor);
        danio = (int)(danio * factor);
        
        // Escalar velocidad (en unidades/segundo)
        velocidadBase *= factor;
    }
    
    // Getter para velocidad base (puede ser útil para debugging)
    public float getVelocidadBase() {
        return velocidadBase;
    }
}