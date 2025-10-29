package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PantallaJuego con sistema de rondas, mini jefe, Puntaje Singleton, fondo implementado
 * y sistema de spawn mejorado con control de frecuencia y progresión por oleadas.
 * 
 * MOD: Sistema de viewport para escalado consistente independiente de la resolución.
 * MOD: Spawn dinámico de enemigos con frecuencia controlada y límites inferiores.
 * MOD: Música de fondo de supervivencia para ambientación.
 * 
 * Conserva el estilo/flujo original y añade estados, transición y escalado por ronda.
 */
public class PantallaJuego implements Screen {
    private final SpaceNavigation game;
    private final SpriteBatch batch;

    // MOD: Sistema de viewport para coordenadas lógicas
    private static final float WORLD_WIDTH = 1200f;
    private static final float WORLD_HEIGHT = 800f;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Nave4 nave;
    private int ronda;
    private int velXAsteroides;
    private int velYAsteroides;
    private int cantAsteroides;

    private List<Bullet> balas = new ArrayList<>();
    private List<Ball2> balls1 = new ArrayList<>();
    private List<Ball2> balls2 = new ArrayList<>();

    private Sound explosionSound;
    
    // Fondo del juego
    private Texture texturaFondo;
    private Sprite spriteFondo;

    // =========================================
    // MOD: Música de fondo para ambientación de supervivencia
    // =========================================
    private Music musicaFondo;

    // =========================================
    // ZombieUpdate: Nuevos campos para el sistema de rondas
    // =========================================
    private MiniJefe miniJefe;
    private boolean enEstadoMiniJefe = false;
    private int tiempoTransicion = 0;
    private static final int TIEMPO_TRANSICION_MAX = 120;

    // =========================================
    // ZombieUpdate: Sistema de spawn mejorado con control de frecuencia
    // =========================================
    private float spawnTimer = 0f;
    private int enemigosRestantesOleada;
    private int enemigosSpawneados = 0;
    
    // Parámetros configurables del sistema de spawn
    private static final float SPAWN_INTERVALO_BASE = 1.5f; // segundos entre spawns
    private static final float SPAWN_INTERVALO_MINIMO = 0.4f; // límite inferior (cap)
    private static final float ACELERACION_POR_RONDA = 0.15f; // reducción de intervalo por ronda
    private static final int ENEMIGOS_BASE_POR_RONDA = 8;
    private static final int INCREMENTO_ENEMIGOS_POR_RONDA = 3;

    // Estados del juego
    private enum EstadoJuego { JUGANDO_ENEMIGOS, MINIJEFE, TRANSICION_RONDA, GAME_OVER }
    private EstadoJuego estadoActual = EstadoJuego.JUGANDO_ENEMIGOS;

    public PantallaJuego(SpaceNavigation game,
            int ronda,
            int vidas,
            int score,
            int velXAsteroides,
            int velYAsteroides,
            int cantAsteroides) {
    	
		this.game = game;
		this.batch = game.getBatch(); 
		this.ronda = ronda;
		
		// MOD: Aumentar velocidades base para mayor acción desde el inicio
		this.velXAsteroides = velXAsteroides + 1;
		this.velYAsteroides = velYAsteroides + 1; 
		this.cantAsteroides = cantAsteroides + 2;
		
		// MOD: Sistema de viewport para escalado consistente
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		
		// Cargar recursos existentes 
		this.explosionSound = Gdx.audio.newSound(Gdx.files.internal("zombie-death.ogg"));
		
		// =========================================
		// MOD: Cargar música de fondo para ambientación
		// =========================================
		musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("survival-theme.wav"));
		musicaFondo.setLooping(true);
		musicaFondo.setVolume(0.7f);
		musicaFondo.play();
		
		// Cargar fondo del juego
		texturaFondo = new Texture(Gdx.files.internal("fondo-juego.jpg"));
		spriteFondo = new Sprite(texturaFondo);
		spriteFondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
		spriteFondo.setPosition(0, 0);
		
		// ZombieUpdate: Usar Singleton Puntaje
		Puntaje.get().reset();
		if (score > 0) Puntaje.get().sumar(score);
		
		// ZombieUpdate: Inicializar sistema de spawn mejorado
		this.enemigosRestantesOleada = getTotalEnemigosRonda();
		this.enemigosSpawneados = 0;
		
		Texture txNave = new Texture(Gdx.files.internal("survivor.png"));
		Texture txBala = new Texture(Gdx.files.internal("bullet.png"));
		Sound sonidoChoque = Gdx.audio.newSound(Gdx.files.internal("zombie-death.ogg"));
		Sound sonidoBala = Gdx.audio.newSound(Gdx.files.internal("gun-shot.ogg"));
		
		// Cast a int para mantener compatibilidad con constructor de Nave4
		nave = new Nave4((int)(WORLD_WIDTH/2 - 22), 60, txNave, sonidoChoque, txBala, sonidoBala);
		nave.setVidas(vidas);
		
		// Eliminar la creación inicial de enemigos - ahora se spawnearán dinámicamente
		// durante la oleada mediante el sistema de spawn mejorado
	}

    // =========================================
    // ZombieUpdate: Métodos del sistema de spawn mejorado
    // =========================================
    
    /**
     * Calcula el intervalo actual de spawn considerando la ronda y el límite mínimo
     * @return Intervalo de spawn en segundos, nunca por debajo del límite mínimo
     */
    private float getSpawnIntervaloActual() {
        float intervalo = SPAWN_INTERVALO_BASE - (ronda - 1) * ACELERACION_POR_RONDA;
        return Math.max(SPAWN_INTERVALO_MINIMO, intervalo);
    }
    
    /**
     * Calcula el total de enemigos para la ronda actual
     * @return Número total de enemigos que deben aparecer en esta ronda
     */
    private int getTotalEnemigosRonda() {
        return ENEMIGOS_BASE_POR_RONDA + (ronda - 1) * INCREMENTO_ENEMIGOS_POR_RONDA;
    }
    
    /**
     * Spawnea un nuevo enemigo en una posición de borde aleatoria
     * con variaciones de velocidad para mayor diversidad
     */
    private void spawnEnemigo() {
        Random r = new Random();
        
        // Determinar posición de spawn (bordes de la pantalla)
        float spawnX, spawnY;
        int lado = r.nextInt(4); // 0: arriba, 1: derecha, 2: abajo, 3: izquierda
        
        switch (lado) {
            case 0: // arriba
                spawnX = r.nextInt((int) WORLD_WIDTH);
                spawnY = WORLD_HEIGHT + 50;
                break;
            case 1: // derecha
                spawnX = WORLD_WIDTH + 50;
                spawnY = r.nextInt((int) WORLD_HEIGHT);
                break;
            case 2: // abajo
                spawnX = r.nextInt((int) WORLD_WIDTH);
                spawnY = -50;
                break;
            case 3: // izquierda
                spawnX = -50;
                spawnY = r.nextInt((int) WORLD_HEIGHT);
                break;
            default:
                spawnX = r.nextInt((int) WORLD_WIDTH);
                spawnY = WORLD_HEIGHT + 50;
        }
        
        // Variación en velocidad para mayor diversidad
        int variacionVel = r.nextInt(3) - 1; // -1, 0, o +1
        
        Ball2 nuevoEnemigo = new Ball2(
            (int) spawnX,
            (int) spawnY,
            60 + r.nextInt(10),
            velXAsteroides + variacionVel,
            velYAsteroides + variacionVel,
            new Texture(Gdx.files.internal("zombie.png")),
            nave
        );
        
        // Aplicar escalado por ronda
        nuevoEnemigo.escalarPorRonda(ronda);
        
        balls1.add(nuevoEnemigo);
        balls2.add(nuevoEnemigo);
    }

    // ==================================================
    // Ciclo de renderizado
    // ==================================================
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // MOD: Aplicar viewport para escalado consistente
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Dibujar fondo primero
        spriteFondo.draw(batch);
        
        dibujaEncabezado();

        switch (estadoActual) {
            case JUGANDO_ENEMIGOS:
                renderJugandoEnemigos();
                break;
            case MINIJEFE:
                renderMiniJefe();
                break;
            case TRANSICION_RONDA:
                renderTransicion();
                break;
            case GAME_OVER:
                // Manejar game over...
                break;
        }

        batch.end();

        verificarCambioEstado();
    }

    private void renderJugandoEnemigos() {
        // Actualizar rotación del jugador con mouse
        actualizarRotacionNave();
        
        // Lógica existente de balas y enemigos
        nave.draw(batch, this);

        // =========================================
        // ZombieUpdate: Sistema de spawn dinámico mejorado
        // =========================================
        float delta = Gdx.graphics.getDeltaTime();
        
        // Spawn de enemigos controlado por tiempo y límites
        if (enemigosSpawneados < getTotalEnemigosRonda()) {
            spawnTimer += delta;
            float intervaloActual = getSpawnIntervaloActual();
            
            if (spawnTimer >= intervaloActual) {
                spawnEnemigo();
                spawnTimer = 0f;
                enemigosSpawneados++;
            }
        }

        // Actualizar balas con delta time para consistencia framerate
        for (int i = 0; i < balas.size(); i++) {
            Bullet b = balas.get(i);
            b.update(delta); // Pasar delta time

            // Colisión bala-enemigo
            for (int j = 0; j < balls1.size(); j++) {
                if (b.checkCollision(balls1.get(j))) {
                    if (explosionSound != null) explosionSound.play();
                    balls1.remove(j);
                    balls2.remove(j);
                    j--;
                    Puntaje.get().sumar(10);
                    enemigosRestantesOleada--;
                }
            }
            
            // Eliminar balas destruidas (por TTL o colisión)
            if (b.isDestroyed()) { 
                balas.remove(i);
                i--;
            }
        }

        // DIBUJAR BALAS: Llamar al método que dibuja las balas
        dibujarBalas();

        // Actualizar y dibujar enemigos...
        for (Ball2 ball : balls1) {
            ball.actualizar(delta);
            ball.dibujar(batch);

            // === COLISIÓN MEJORADA: RESPETA I-FRAMES ===
            if (!nave.estaInvulnerable() && ball.getArea().overlaps(nave.getArea())) {
                nave.recibirDanio(ball.getDanio());
            }
        }
    }

    private void renderMiniJefe() {
        actualizarRotacionNave();
        
        if (miniJefe != null) {
            enEstadoMiniJefe = true;
            miniJefe.actualizar(Gdx.graphics.getDeltaTime());
            miniJefe.dibujar(batch);

            // Actualizar y verificar colisiones de balas con mini jefe
            for (int i = 0; i < balas.size(); i++) {
                Bullet b = balas.get(i);
                b.update(Gdx.graphics.getDeltaTime());
                
                if (!b.isDestroyed() && b.getArea().overlaps(miniJefe.getArea())) {
                    miniJefe.recibirDanio(1);
                    // MARCA la bala como destruida en lugar de eliminarla inmediatamente
                    b.destruir();
                    
                    if (!miniJefe.estaVivo()) {
                        Puntaje.get().sumar(100);
                        estadoActual = EstadoJuego.TRANSICION_RONDA;
                        tiempoTransicion = TIEMPO_TRANSICION_MAX;
                        enEstadoMiniJefe = false;
                        break; // Salir del bucle si el mini jefe fue derrotado
                    }
                }
            }
            
            // Limpiar balas destruidas (incluyendo las que impactaron al mini jefe)
            for (int i = 0; i < balas.size(); i++) {
                if (balas.get(i).isDestroyed()) {
                    balas.remove(i);
                    i--;
                }
            }

            dibujarBalas();

            if (!nave.estaInvulnerable() && miniJefe.getArea().overlaps(nave.getArea())) {
                nave.recibirDanio(miniJefe.getDanio());
            }

            nave.draw(batch, this);
        }
    }

    private void renderTransicion() {
        // Mostrar mensaje de transición
        game.getFont().draw(batch,
                "¡Ronda " + (ronda + 1) + " completada!",
                WORLD_WIDTH / 2f - 100, // MOD: Usar WORLD_WIDTH
                WORLD_HEIGHT / 2f // MOD: Usar WORLD_HEIGHT
        );
        tiempoTransicion--;
    }

    /**
     * Actualiza la rotación de la nave basándose en la posición del mouse
     * Convierte coordenadas de pantalla a coordenadas del mundo para precisión
     */
    private void actualizarRotacionNave() {
        // Obtener posición del mouse en coordenadas de pantalla
        com.badlogic.gdx.math.Vector3 mousePos = new com.badlogic.gdx.math.Vector3(
            Gdx.input.getX(), 
            Gdx.input.getY(), 
            0
        );
        
        // Convertir a coordenadas del mundo usando la cámara del viewport
        camera.unproject(mousePos);
        
        // Pasar coordenadas convertidas a la nave para rotación precisa
        nave.actualizarRotacionMouse(mousePos.x, mousePos.y);
    }
    
    /**
     * Dibuja todas las balas activas en el juego
     * Se llama desde los estados JUGANDO_ENEMIGOS y MINIJEFE
     */
    private void dibujarBalas() {
        for (Bullet bala : balas) {
            if (!bala.isDestroyed()) {
                bala.draw(batch);
            }
        }
    }

    private void verificarCambioEstado() {
        // =========================================
        // ZombieUpdate: Criterio claro para cambio de estado
        // =========================================
        if (estadoActual == EstadoJuego.JUGANDO_ENEMIGOS && 
            enemigosSpawneados >= getTotalEnemigosRonda() && 
            balls1.size() == 0) {
            
            // Todos los enemigos de la oleada fueron spawneados y eliminados
            // Generar mini jefe
            miniJefe = new MiniJefe(
                    WORLD_WIDTH / 2f,
                    WORLD_HEIGHT - 100f,
                    new Texture(Gdx.files.internal("mini-jefe.png")),
                    nave
            );
            miniJefe.escalarPorRonda(ronda);
            estadoActual = EstadoJuego.MINIJEFE;
        }

        if (estadoActual == EstadoJuego.TRANSICION_RONDA && tiempoTransicion <= 0) {
            // Pasar a siguiente ronda
            Screen ss = new PantallaJuego(
                    game,
                    ronda + 1,
                    nave.getVidas(),
                    Puntaje.get().getScore(),
                    velXAsteroides + 1,
                    velYAsteroides + 1,
                    cantAsteroides + 2 // Incremento más conservador
            );
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }

        if (!nave.estaVivo()) {
            // =========================================
            // MOD: Detener música cuando el juego termina
            // =========================================
            if (musicaFondo != null) {
                musicaFondo.stop();
            }
            
            if (Puntaje.get().getScore() > Puntaje.get().getHighScore())
                Puntaje.get().setHighScore(Puntaje.get().getScore());
            Screen ss = new PantallaGameOver(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }

    public void dibujaEncabezado() {
        // ZombieUpdate: Usar Singleton Puntaje + mostrar progreso de oleada
        CharSequence str = "Vidas: " + nave.getVidas() + " Oleada: " + ronda;
        game.getFont().getData().setScale(2f);
        game.getFont().draw(batch, str, 10, 30);
        game.getFont().draw(batch, "Score:" + Puntaje.get().getScore(),
                WORLD_WIDTH - 150, 30); // MOD: Usar WORLD_WIDTH
        game.getFont().draw(batch, "HighScore:" + Puntaje.get().getHighScore(),
                WORLD_WIDTH / 2f - 100, 30); // MOD: Usar WORLD_WIDTH

        // =========================================
        // ZombieUpdate: Mostrar progreso de la oleada
        // =========================================
        int enemigosTotales = getTotalEnemigosRonda();
        String progresoOleada = "Zombis: " + (enemigosTotales - enemigosRestantesOleada) + "/" + enemigosTotales;
        game.getFont().draw(batch, progresoOleada, WORLD_WIDTH / 2f - 100, 60);

        // Mostrar estado actual
        if (estadoActual == EstadoJuego.MINIJEFE) {
            game.getFont().draw(batch, "¡MINI JEFE!",
                    WORLD_WIDTH / 2f - 50, 90); // MOD: Usar WORLD_WIDTH
        } else if (estadoActual == EstadoJuego.TRANSICION_RONDA) {
            game.getFont().draw(batch, "¡Oleada " + ronda + " completada!",
                    WORLD_WIDTH / 2f - 120, 90); // MOD: Usar WORLD_WIDTH
        }
    }

    // ==================================================
    // Compatibilidad / utilidades mínimas
    // ==================================================
    /** Permite a Nave4 registrar balas sin acoplarse a la implementación. */
    public void agregarBala(Bullet b) {
        if (b != null) balas.add(b);
    }

    // ==================================================
    // Métodos del ciclo de vida Screen (estándar LibGDX)
    // ==================================================
    @Override
    public void show() {
        // Reiniciar música si es necesario
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        // MOD: Actualizar viewport al cambiar tamaño
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        // Pausar música cuando el juego se pausa
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    @Override
    public void resume() {
        // Reanudar música cuando el juego se reanuda
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    public void hide() {
        // Pausar música cuando la pantalla se oculta
        if (musicaFondo != null) {
            musicaFondo.pause();
        }
    }

    @Override
    public void dispose() {
        // Liberar recursos 
        if (explosionSound != null) explosionSound.dispose();
        if (texturaFondo != null) texturaFondo.dispose();
        
        // =========================================
        // MOD: Liberar recurso de música de fondo
        // =========================================
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
    }
}