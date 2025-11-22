package puppy.code.bosses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import puppy.code.ataques.AtaqueSimple;
import puppy.code.ataques.ProyectilMiniJefe;
import puppy.code.core.Enemigo;
import puppy.code.enemigos.Ball2;
import puppy.code.gestores.GestorAssets;
import puppy.code.jugador.Nave4;
import puppy.code.movimiento.MovimientoPatronCiudad;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BOSS FINAL - El temido Profe
 * Combina habilidades de todos los jefes + invocación de zombies + ataques de proyectiles
 */
public class BossProfe extends BossBase {
    // NUEVO CAMBIO: Aumentar salud base de 200 a 400
    private static final int SALUD_BASE = 400;
    private static final int DANIO_BASE = 5;
    
    private float cooldownHabilidad = 0f;
    private float cooldownInvocacion = 0f;
    // NUEVO CAMBIO: Cooldowns para diferentes tipos de ataques - AHORA MÁS LENTOS
    private float cooldownDisparoRapido = 0f;
    private float cooldownDisparoFuerte = 0f;
    private int faseAtaque = 0;
    private Random random;
    
    // Límites del mundo para mantener al boss dentro de la pantalla
    private static final float WORLD_WIDTH = 1200f;
    private static final float WORLD_HEIGHT = 800f;
    
    // Sistema de invocación de zombies
    private List<Enemigo> zombiesInvocados;
    private java.util.function.Consumer<Enemigo> onInvocarCallback;
    
    // Sistema de spawn automático de zombies durante la pelea
    private float temporizadorSpawnAutomatico = 0f;
    // NUEVO CAMBIO: Spawn automático más lento - de 8.0f a 12.0f segundos
    private static final float INTERVALO_SPAWN_AUTOMATICO = 12.0f; // Cada 12 segundos (antes 8)
    
    // NUEVO CAMBIO: Sistema de proyectiles del BossProfe
    private List<ProyectilMiniJefe> proyectiles;
    private java.util.function.Consumer<ProyectilMiniJefe> onDisparoCallback;
    private Texture texturaProyectilBosque;
    private Texture texturaProyectilCiudad;
    
    public BossProfe(float x, float y, Texture textura, Nave4 jugador) {
        super(x, y, textura, jugador, SALUD_BASE, DANIO_BASE, 40f);
        setEstrategiaMovimiento(new MovimientoPatronCiudad());
        setEstrategiaAtaque(new AtaqueSimple());
        
        this.random = new Random();
        this.zombiesInvocados = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        
        // NUEVO CAMBIO: Cargar texturas de proyectiles de ambos escenarios
        this.texturaProyectilBosque = GestorAssets.get().getTextura("proyectil-piedra");
        this.texturaProyectilCiudad = GestorAssets.get().getTextura("proyectil-auto");
    }
    
    @Override
    protected void mover(float delta) {
        super.mover(delta);
        mantenerDentroDeLimites();
        actualizarSpawnAutomatico(delta);
        activarHabilidadEspecial(delta);
    }
    
    // Método para mantener al boss dentro de los límites de la pantalla
    private void mantenerDentroDeLimites() {
        // Límites horizontales
        if (posicion.x < 50) {
            posicion.x = 50;
        } else if (posicion.x + area.width > WORLD_WIDTH - 50) {
            posicion.x = WORLD_WIDTH - area.width - 50;
        }
        
        // Límites verticales  
        if (posicion.y < 100) {
            posicion.y = 100;
        } else if (posicion.y + area.height > WORLD_HEIGHT - 50) {
            posicion.y = WORLD_HEIGHT - area.height - 50;
        }
        
        // Actualizar área de colisión
        area.setPosition(posicion.x, posicion.y);
    }
    
    // Método para spawn automático de zombies durante la pelea
    private void actualizarSpawnAutomatico(float delta) {
        temporizadorSpawnAutomatico += delta;
        
        if (temporizadorSpawnAutomatico >= INTERVALO_SPAWN_AUTOMATICO) {
            spawnearZombiesAutomaticos();
            temporizadorSpawnAutomatico = 0f;
        }
    }
    
    // Spawn automático de zombies desde los bordes
    private void spawnearZombiesAutomaticos() {
        if (onInvocarCallback != null && zombiesInvocados.size() < 6) { // Límite de 6 zombies
            // NUEVO CAMBIO: Menos zombies por spawn - de 2-3 a 1-2
            int cantidadZombies = 1 + random.nextInt(2); // 1-2 zombies por spawn (antes 2-3)
            
            for (int i = 0; i < cantidadZombies; i++) {
                // Spawn desde los bordes de la pantalla
                float spawnX, spawnY;
                int lado = random.nextInt(4);
                
                switch (lado) {
                    case 0: // arriba
                        spawnX = random.nextInt((int) WORLD_WIDTH);
                        spawnY = WORLD_HEIGHT + 30;
                        break;
                    case 1: // derecha
                        spawnX = WORLD_WIDTH + 30;
                        spawnY = random.nextInt((int) WORLD_HEIGHT);
                        break;
                    case 2: // abajo
                        spawnX = random.nextInt((int) WORLD_WIDTH);
                        spawnY = -30;
                        break;
                    case 3: // izquierda
                    default:
                        spawnX = -30;
                        spawnY = random.nextInt((int) WORLD_HEIGHT);
                }
                
                // Crear zombie básico
                Ball2 zombie = new Ball2(
                    (int)spawnX, (int)spawnY, 
                    60, 2, 2, 
                    GestorAssets.get().getTextura("zombie-bosque"), 
                    jugador
                );
                zombie.escalarPorRonda(6); // Zombies más fuertes durante el boss final
                
                zombiesInvocados.add(zombie);
                onInvocarCallback.accept(zombie);
            }
        }
    }
    
    @Override
    protected void atacar(float delta) {
        // Ciclos de ataque más complejos
        cooldownHabilidad -= delta;
        cooldownInvocacion -= delta;
        // NUEVO CAMBIO: Actualizar cooldowns de disparo
        cooldownDisparoRapido -= delta;
        cooldownDisparoFuerte -= delta;
        
        if (cooldownHabilidad <= 0) {
            faseAtaque = (faseAtaque + 1) % 4;
            cooldownHabilidad = 2.5f; // Cambia cada 2.5 segundos
        }
        
        // NUEVO CAMBIO: Invocación de zombies más lenta - de 8.0f a 12.0f segundos
        if (cooldownInvocacion <= 0 && zombiesInvocados.size() < 4) {
            invocarZombies();
            cooldownInvocacion = 12.0f; // Cada 12 segundos (antes 8)
        }
        
        // NUEVO CAMBIO: Sistema de ataques de proyectiles - AHORA MÁS LENTOS
        if (cooldownDisparoRapido <= 0) {
            realizarAtaqueBosque(); // Ataque rápido en abanico (como Boss Bosque)
            // NUEVO CAMBIO: Disparo más lento - de 2.0f a 3.5f segundos
            cooldownDisparoRapido = 3.5f; // Disparo rápido cada 3.5 segundos (antes 2.0)
        }
        
        if (cooldownDisparoFuerte <= 0) {
            realizarAtaqueCiudad(); // Ataque fuerte directo (como Boss Ciudad)
            // NUEVO CAMBIO: Disparo más lento - de 4.0f a 7.0f segundos
            cooldownDisparoFuerte = 7.0f; // Disparo fuerte cada 7 segundos (antes 4.0)
        }
        
        // Actualizar proyectiles existentes
        actualizarProyectiles(delta);
    }
    
    // NUEVO CAMBIO: Ataque estilo Boss Bosque (rápido, en abanico)
    private void realizarAtaqueBosque() {
        if (onDisparoCallback != null && jugador != null && jugador.estaVivo()) {
            float anguloBase = calcularAnguloHaciaJugador();
            
            // Disparar 3 proyectiles en abanico (como el Boss Bosque)
            crearProyectil(anguloBase, texturaProyectilBosque, 0.3f, 1, false);
            crearProyectil(anguloBase - 0.6f, texturaProyectilBosque, 0.3f, 1, false);
            crearProyectil(anguloBase + 0.6f, texturaProyectilBosque, 0.3f, 1, false);
        }
    }
    
    // NUEVO CAMBIO: Ataque estilo Boss Ciudad (fuerte, directo)
    private void realizarAtaqueCiudad() {
        if (onDisparoCallback != null && jugador != null && jugador.estaVivo()) {
            float angulo = calcularAnguloHaciaJugador();
            
            // Disparar 1 proyectil grande y poderoso (como el Boss Ciudad)
            crearProyectil(angulo, texturaProyectilCiudad, 0.6f, 3, true);
        }
    }
    
    // NUEVO CAMBIO: Método para crear proyectiles
    private void crearProyectil(float angulo, Texture textura, float escala, int danio, boolean esGrande) {
        ProyectilMiniJefe proyectil = new ProyectilMiniJefe(
            posicion.x + area.width / 2 - 20,
            posicion.y + area.height / 2 - 20,
            angulo,
            textura,
            danio,
            escala,
            esGrande
        );
        
        if (onDisparoCallback != null) {
            onDisparoCallback.accept(proyectil);
        }
    }
    
    // NUEVO CAMBIO: Método para calcular el ángulo hacia el jugador
    private float calcularAnguloHaciaJugador() {
        float jugadorX = jugador.getXFloat() + jugador.getSprite().getWidth() / 2;
        float jugadorY = jugador.getYFloat() + jugador.getSprite().getHeight() / 2;
        float bossX = posicion.x + area.width / 2;
        float bossY = posicion.y + area.height / 2;
        
        return (float) Math.atan2(jugadorY - bossY, jugadorX - bossX);
    }
    
    // NUEVO CAMBIO: Actualizar proyectiles existentes
    private void actualizarProyectiles(float delta) {
        proyectiles.removeIf(proyectil -> {
            proyectil.actualizar(delta);
            return proyectil.estaDestruido();
        });
    }
    
    private void invocarZombies() {
        if (onInvocarCallback != null) {
            // NUEVO CAMBIO: Menos zombies por invocación - de 2 a 1
            for (int i = 0; i < 1; i++) { // Invoca 1 zombie (antes 2)
                // Asegurar que los zombies aparezcan cerca pero dentro de los límites
                float spawnX = Math.max(50, Math.min(WORLD_WIDTH - 110, 
                    posicion.x + random.nextFloat() * 200 - 100));
                float spawnY = Math.max(50, Math.min(WORLD_HEIGHT - 110, 
                    posicion.y + random.nextFloat() * 200 - 100));
                
                // Crear zombie básico con textura especial
                Ball2 zombie = new Ball2(
                    (int)spawnX, (int)spawnY, 
                    60, 2, 2, 
                    GestorAssets.get().getTextura("zombie-bosque"), 
                    jugador
                );
                zombie.escalarPorRonda(6); // Zombies más fuertes durante el boss final
                
                zombiesInvocados.add(zombie);
                onInvocarCallback.accept(zombie);
            }
        }
    }
    
    @Override
    protected void verificarMuerte(float delta) {
        if (!estaVivo()) {
            efectosMuerte();
            // Limpiar zombies invocados
            zombiesInvocados.clear();
            // NUEVO CAMBIO: Limpiar proyectiles
            proyectiles.clear();
        }
    }
    
    @Override
    public void activarHabilidadEspecial(float delta) {
        // Habilidad especial: Cambia entre patrones de los jefes anteriores
        switch (faseAtaque) {
            case 0:
                // Patrón Boss Bosque: aumento de velocidad ocasional
                if (random.nextFloat() < 0.02f) {
                    velocidadBase = Math.min(velocidadBase * 1.3f, 80f); // Límite máximo de velocidad
                }
                break;
            case 1:
                // Patrón Boss Ciudad: movimientos defensivos
                // (ya implementado en MovimientoPatronCiudad)
                break;
            case 2:
                // Teletransporte breve PERO DENTRO DE LÍMITES
                if (random.nextFloat() < 0.01f) {
                    float newX = 100 + random.nextFloat() * (WORLD_WIDTH - 200);
                    float newY = 400 + random.nextFloat() * 200; // Zona superior de la pantalla
                    posicion.x = newX;
                    posicion.y = newY;
                    area.setPosition(posicion.x, posicion.y);
                }
                break;
            case 3:
                // NUEVO CAMBIO: Patrón especial - ataque masivo (más raro)
                if (random.nextFloat() < 0.008f) { // Más raro - de 0.015f a 0.008f
                    realizarAtaqueMasivo();
                }
                break;
        }
    }
    
    // NUEVO CAMBIO: Ataque masivo especial del BossProfe
    private void realizarAtaqueMasivo() {
        if (onDisparoCallback != null) {
            // Disparar 8 proyectiles en todas direcciones
            for (int i = 0; i < 8; i++) {
                float angulo = (float) (i * Math.PI / 4); // 45 grados entre cada proyectil
                crearProyectil(angulo, texturaProyectilCiudad, 0.4f, 2, false);
            }
        }
    }
    
    @Override
    protected void efectosMuerte() {
        // Efecto especial: el profe se transforma en una A+ :)
        // Podría añadir explosión de partículas o sonido especial
        System.out.println("¡BOSS FINAL DERROTADO!");
    }
    
    public void setOnInvocarCallback(java.util.function.Consumer<Enemigo> callback) {
        this.onInvocarCallback = callback;
    }
    
    // NUEVO CAMBIO: Setter para el callback de disparo
    public void setOnDisparoCallback(java.util.function.Consumer<ProyectilMiniJefe> callback) {
        this.onDisparoCallback = callback;
    }
    
    public List<Enemigo> getZombiesInvocados() {
        return zombiesInvocados;
    }
    
    public void removerZombieInvocado(Enemigo zombie) {
        zombiesInvocados.remove(zombie);
    }
    
    @Override
    public void dibujar(SpriteBatch batch) {
        spr.draw(batch);
        
        // NUEVO CAMBIO: Dibujar proyectiles activos
        for (ProyectilMiniJefe proyectil : proyectiles) {
            proyectil.dibujar(batch);
        }
    }
    
    @Override
    public void escalarPorRonda(int ronda) {
        // El boss final no escala por rondas, ya es el más fuerte
        // Pero podemos ajustar un poco si queremos
        float factor = 1.0f + (ronda - 5) * 0.1f; // Ajustar para ronda 5
        
        salud = (int)(SALUD_BASE * factor);
        danio = (int)(DANIO_BASE * factor);
        velocidadBase = 40f * factor;
    }
    
    // Getter para compatibilidad con GestorUI
    public int getSaludMaxima() {
        return SALUD_BASE;
    }
}
