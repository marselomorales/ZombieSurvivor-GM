package puppy.code.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import puppy.code.PantallaGameOver;
import puppy.code.PantallaMenu;
// NUEVO CAMBIO: Importar la nueva pantalla de victoria
import puppy.code.PantallaVictoria;
import puppy.code.Puntaje;
import puppy.code.ataques.Bullet;
import puppy.code.ataques.ProyectilMiniJefe;
import puppy.code.bosses.BossProfe;
import puppy.code.core.Enemigo;
import puppy.code.core.EstadosJuego;
import puppy.code.enemigos.Ball2;
import puppy.code.enemigos.MiniJefe;
import puppy.code.escenarios.EscenarioFactory;
import puppy.code.escenarios.GestorEscenarios;
// NUEVO CAMBIO: Importar las factories de escenarios
import puppy.code.escenarios.EscenarioBosqueFactory;
import puppy.code.escenarios.EscenarioCiudadFactory;
import puppy.code.gestores.GestorAssets;
import puppy.code.gestores.GestorEntidades;
import puppy.code.gestores.GestorSpawn;
import puppy.code.gestores.GestorUI;
import puppy.code.gestores.GestorVentajas;
import puppy.code.jugador.Nave4;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * PantallaJuego delega responsabilidades en clases especializadas
 */
public class PantallaJuego implements Screen {
    private final SpaceNavigation game;
    private final SpriteBatch batch;
    
    // Cambiamos de List<Bullet> a List<ProyectilMiniJefe> para los proyectiles del mini jefe
    private List<ProyectilMiniJefe> proyectilesEnemigos;

    // Para el gestor de ventajas
    private GestorVentajas gestorVentajas;

    // Sistema de viewport para coordenadas lógicas
    public static final float WORLD_WIDTH = 1200f;
    public static final float WORLD_HEIGHT = 800f;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Entidad jugador
    private Nave4 nave;
    private int ronda;
    private int velXAsteroides;
    private int velYAsteroides;
    private int cantAsteroides;

    // Managers especializados
    private GestorAssets gestorAssets;
    private GestorEntidades gestorEntidades;
    private GestorSpawn gestorSpawn;
    private GestorUI gestorUI;

    // Elementos del juego
    private Enemigo miniJefe;
    private Music musicaFondo;
    private Sprite spriteFondo;
    private Enemigo bossFinal;
    private List<Enemigo> zombiesBossFinal;

    // Estados del juego 
    private int estadoActual = EstadosJuego.JUGANDO_ENEMIGOS;
    private int tiempoTransicion = 0;
    private static final int TIEMPO_TRANSICION_MAX = 120;

    // Abstract Factory
    private EscenarioFactory escenarioFactory;
    private GestorEscenarios gestorEscenarios;

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
		
		gestorAssets = GestorAssets.get();
		
		// Configuración de dificultad
		this.velXAsteroides = velXAsteroides + 1;
		this.velYAsteroides = velYAsteroides + 1; 
		this.cantAsteroides = cantAsteroides + 2;
		
		// Inicializar sistemas básicos
		inicializarSistemasBasicos();
		
		// Configurar puntuación
		Puntaje.get().reset();
		if (score > 0) Puntaje.get().sumar(score);
		
		// Inicializar jugador
		inicializarJugador(vidas);
		
		// Inicializar managers
		inicializarManagers();
		
		// Inicializar gestor de ventajas
		this.gestorVentajas = GestorVentajas.get();
		
		// Inicializar lista de proyectiles enemigos
		this.proyectilesEnemigos = new ArrayList<>();
    }

    /**
     * Inicializa los sistemas básicos del juego
     */
    private void inicializarSistemasBasicos() {
        // Sistema de cámara y viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        
        // Fondo temporal - será reemplazado por la factory
        spriteFondo = new Sprite(gestorAssets.getTextura("fondo-bosque"));
        spriteFondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        spriteFondo.setPosition(0, 0);
    }

    /**
     * Inicializa la entidad jugador
     */
    private void inicializarJugador(int vidas) {
        Texture txNave = gestorAssets.getTextura("survivor");
        Texture txBala = gestorAssets.getTextura("bullet");
        Sound sonidoChoque = gestorAssets.getSonido("player-hurt");
        Sound sonidoBala = gestorAssets.getSonido("gun-shot");
        
        nave = new Nave4((int)(WORLD_WIDTH/2 - 22), 60, txNave, sonidoChoque, txBala, sonidoBala);
        nave.setOnDisparo(bala -> gestorEntidades.agregarBala(bala));
        nave.setVidas(vidas);
    }

    /**
     * Inicializa todos los managers especializados
     */
    private void inicializarManagers() {
        // Gestor de entidades (enemigos y balas)
        gestorEntidades = new GestorEntidades(nave);
        
        // NUEVO CAMBIO CORREGIDO: Lógica de escenarios según ronda
        if (ronda <= 2) {
            // Rondas 1-2: Escenario Bosque
            escenarioFactory = new EscenarioBosqueFactory();
        } else if (ronda <= 4) {
            // Rondas 3-4: Escenario Ciudad
            escenarioFactory = new EscenarioCiudadFactory();
        } else if (ronda == 5) {
            // Ronda 5: Boss Final - iniciar inmediatamente
            escenarioFactory = new EscenarioBosqueFactory(); // Fallback temporal
            iniciarBossFinal();
            return; // No inicializar spawn para el boss final
        } else {
            // Cualquier ronda superior (por si acaso)
            escenarioFactory = new EscenarioBosqueFactory();
        }
        
        // Gestor de spawn de enemigos (solo para rondas 1-4)
        gestorSpawn = new GestorSpawn(this, gestorEntidades);
        gestorSpawn.iniciarNuevaOleada(ronda);
        
        // Gestor de interfaz de usuario
        gestorUI = new GestorUI(game, nave, gestorSpawn);
        
        // Aplicar el escenario actual (solo para rondas 1-4)
        aplicarEscenarioActual();
        
        estadoActual = EstadosJuego.JUGANDO_ENEMIGOS;
    }

    /**
     * Aplica el escenario actual cambiando fondo, música, etc.
     */
    private void aplicarEscenarioActual() {
        // NUEVO CAMBIO CORREGIDO: No aplicar escenario normal si es ronda 5 (Boss Final)
        if (ronda == 5) {
            return; // El Boss Final tiene su propio fondo especial
        }
        
        // Cambiar fondo según la factory
        Texture fondo = escenarioFactory.crearFondo();
        spriteFondo = new Sprite(fondo);
        spriteFondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        spriteFondo.setPosition(0, 0);
        
        // Cambiar música
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
        musicaFondo = escenarioFactory.crearMusica();
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.7f);
        musicaFondo.play();
        
        // Actualizar UI con nombre del escenario
        gestorUI.setNombreEscenario(escenarioFactory.getNombreEscenario());
    }

    // ==================================================
    // Ciclo de renderizado principal
    // ==================================================
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Aplicar viewport para escalado consistente
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        
        // Dibujar fondo
        spriteFondo.draw(batch);
        
        // Actualizar y dibujar UI
        gestorUI.setRonda(ronda);
        gestorUI.setEstadoActual(estadoActual);
        gestorUI.dibujar(batch);

        // Dibujar ventajas
        gestorVentajas.dibujar(batch);

        // Ejecutar lógica según estado actual 
        if (estadoActual == EstadosJuego.JUGANDO_ENEMIGOS) {
            renderJugandoEnemigos(delta);
        } else if (estadoActual == EstadosJuego.MINIJEFE) {
            renderMiniJefe(delta);
        } else if (estadoActual == EstadosJuego.TRANSICION_RONDA) {
            renderTransicion();
        } else if (estadoActual == EstadosJuego.GAME_OVER) {
            // Manejo de game over...
        } else if (estadoActual == EstadosJuego.BOSS_FINAL) {
	        renderBossFinal(delta);
	    }

        batch.end();

        // Actualizar ventajas y manejar input
        gestorVentajas.actualizar(delta);
        manejarInputVentajas();

        verificarCambioEstado();
    }

    /**
     * Método para manejar input de ventajas
     */
    private void manejarInputVentajas() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            gestorVentajas.activarVentaja(0, nave); // Disparo automático
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            gestorVentajas.activarVentaja(1, nave); // Inmortalidad
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            gestorVentajas.activarVentaja(2, nave); // Vidas extra
        }
    }

    /**
     * Renderiza el estado normal de juego con enemigos
     */
    private void renderJugandoEnemigos(float delta) {
        // Actualizar rotación del jugador
        actualizarRotacionNave();
        
        // Actualizar jugador
        nave.draw(batch, this);
        
        // Actualizar sistemas de spawn y entidades
        gestorSpawn.actualizar(delta);
        gestorEntidades.actualizar(delta);
        gestorEntidades.dibujar(batch);
    }

    /**
     * Renderiza el estado de mini jefe
     */
    private void renderMiniJefe(float delta) {
        actualizarRotacionNave();
        
        // Actualizar las balas del jugador primero
        gestorEntidades.actualizar(delta);
        
        // Actualizar proyectiles enemigos (los que ya disparó el mini jefe)
        Iterator<ProyectilMiniJefe> iterProyectilesEnemigos = proyectilesEnemigos.iterator();
        while (iterProyectilesEnemigos.hasNext()) {
            ProyectilMiniJefe proyectil = iterProyectilesEnemigos.next();
            proyectil.actualizar(delta);
            
            // Verificar colisión con jugador
            if (!nave.estaInvulnerable() && proyectil.verificarColision(nave)) {
                nave.recibirDanio(proyectil.getDanio());
                iterProyectilesEnemigos.remove();
            } else if (proyectil.estaDestruido()) {
                iterProyectilesEnemigos.remove();
            }
        }
        
        if (miniJefe != null) {
            // Actualizar mini jefe
            miniJefe.actualizar(delta);
            miniJefe.dibujar(batch);

            // Verificar colisiones de balas del JUGADOR con mini jefe 
            // Usamos directamente la lista de balas del gestorEntidades
            Iterator<Bullet> iteradorBalas = gestorEntidades.getBalas().iterator();
            while (iteradorBalas.hasNext()) {
                Bullet bala = iteradorBalas.next();
                // Verificar que la bala no esté destruida y que colisione con el mini jefe
                if (!bala.isDestroyed() && bala.getArea().overlaps(miniJefe.getArea())) {
                    miniJefe.recibirDanio(1);
                    bala.destruir(); // Destruir la bala después del impacto
                    
                    if (!miniJefe.estaVivo()) {
                        Puntaje.get().sumar(100);
                        estadoActual = EstadosJuego.TRANSICION_RONDA;
                        tiempoTransicion = TIEMPO_TRANSICION_MAX;
                        
                        // Limpiar proyectiles enemigos cuando muere el mini jefe
                        proyectilesEnemigos.clear();
                        
                        // Limpiar referencia al boss cuando muere
                        gestorUI.setBossActual(null);
                        break;
                    }
                }
            }
            
            // Colisión mini jefe - jugador
            if (!nave.estaInvulnerable() && miniJefe.getArea().overlaps(nave.getArea())) {
                nave.recibirDanio(miniJefe.getDanio());
            }
        }
        
        // Dibujar proyectiles enemigos (FUERA del if miniJefe != null)
        for (ProyectilMiniJefe proyectil : proyectilesEnemigos) {
            if (!proyectil.estaDestruido()) {
                proyectil.dibujar(batch);
            }
        }
        
        // Dibujar balas del jugador (IMPORTANTE)
        gestorEntidades.dibujar(batch);
        
        // Dibujar jugador
        nave.draw(batch, this);
    }

    /**
     * Renderiza la transición entre rondas con efecto de parpadeo
     */
    private void renderTransicion() {
        // Efecto de parpadeo: visible durante 10 frames, invisible durante 10 frames
        if (tiempoTransicion % 20 < 10) {
            game.getFont().draw(batch,
                    "¡Ronda " + (ronda + 1) + " completada!",
                    WORLD_WIDTH / 2f - 100,
                    WORLD_HEIGHT / 2f
            );
        }
        tiempoTransicion--;
    }

    /**
     * Actualiza la rotación de la nave basándose en el mouse
     */
    private void actualizarRotacionNave() {
        com.badlogic.gdx.math.Vector3 posicionMouse = new com.badlogic.gdx.math.Vector3(
            Gdx.input.getX(), 
            Gdx.input.getY(), 
            0
        );
        
        camera.unproject(posicionMouse);
        nave.actualizarRotacionMouse(posicionMouse.x, posicionMouse.y);
    }
    
    private void iniciarBossFinal() {
        estadoActual = EstadosJuego.BOSS_FINAL;
        zombiesBossFinal = new ArrayList<>();
        
        // Crear boss final
        bossFinal = new BossProfe(
            WORLD_WIDTH / 2f,
            WORLD_HEIGHT - 150f,
            GestorAssets.get().getTextura("boss-profe"),
            nave
        );
        
        // Configurar callback para invocación de zombies
        if (bossFinal instanceof BossProfe) {
            ((BossProfe) bossFinal).setOnInvocarCallback(zombie -> {
                zombiesBossFinal.add(zombie);
                gestorEntidades.agregarEnemigo((Ball2) zombie);
            });
            
            // NUEVO CAMBIO: Configurar callback para proyectiles del BossProfe
            ((BossProfe) bossFinal).setOnDisparoCallback(proyectil -> {
                proyectilesEnemigos.add(proyectil);
            });
        }
        
        // Cambiar a fondo especial del IBC
        Texture fondoIBC = GestorAssets.get().getTextura("fondo-ibc");
        spriteFondo = new Sprite(fondoIBC);
        spriteFondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        spriteFondo.setPosition(0, 0);
        
        // Musica especial opcional
        if (musicaFondo != null) {
            musicaFondo.stop();
        }
        // Podrías añadir música épica aquí
        
        gestorUI.setBossActual(bossFinal);
        gestorUI.setNombreEscenario("¡BOSS FINAL: EL PROFE!");
    }
    
    private void renderBossFinal(float delta) {
        actualizarRotacionNave();
        
        // NUEVO CAMBIO: Permitir que el gestor de entidades actualice zombies normales también
        gestorEntidades.actualizar(delta);
        
        // Actualizar proyectiles enemigos (los que dispara el BossProfe)
        Iterator<ProyectilMiniJefe> iterProyectilesEnemigos = proyectilesEnemigos.iterator();
        while (iterProyectilesEnemigos.hasNext()) {
            ProyectilMiniJefe proyectil = iterProyectilesEnemigos.next();
            proyectil.actualizar(delta);
            
            // Verificar colisión con jugador
            if (!nave.estaInvulnerable() && proyectil.verificarColision(nave)) {
                nave.recibirDanio(proyectil.getDanio());
                iterProyectilesEnemigos.remove();
            } else if (proyectil.estaDestruido()) {
                iterProyectilesEnemigos.remove();
            }
        }
        
        // Actualizar boss final
        if (bossFinal != null) {
            bossFinal.actualizar(delta);
            bossFinal.dibujar(batch);
            
            // Verificar colisiones balas del jugador con boss final
            Iterator<Bullet> iteradorBalas = gestorEntidades.getBalas().iterator();
            while (iteradorBalas.hasNext()) {
                Bullet bala = iteradorBalas.next();
                if (!bala.isDestroyed() && bala.getArea().overlaps(bossFinal.getArea())) {
                    bossFinal.recibirDanio(1);
                    bala.destruir();
                    
                    if (!bossFinal.estaVivo()) {
                        Puntaje.get().sumar(500); // Gran recompensa
                        // NUEVO CAMBIO: Reemplazado por la nueva pantalla de victoria
                        mostrarPantallaVictoria();
                        return;
                    }
                }
            }
            
            // Colisión boss final - jugador
            if (!nave.estaInvulnerable() && bossFinal.getArea().overlaps(nave.getArea())) {
                nave.recibirDanio(bossFinal.getDanio());
            }
            
            // Gestionar zombies invocados
            if (bossFinal instanceof BossProfe) {
                BossProfe profe = (BossProfe) bossFinal;
                
                // Remover zombies muertos
                Iterator<Enemigo> iterZombies = zombiesBossFinal.iterator();
                while (iterZombies.hasNext()) {
                    Enemigo zombie = iterZombies.next();
                    if (!zombie.estaVivo()) {
                        iterZombies.remove();
                        profe.removerZombieInvocado(zombie);
                    }
                }
            }
        }
        
        // Dibujar proyectiles enemigos (los que dispara el BossProfe)
        for (ProyectilMiniJefe proyectil : proyectilesEnemigos) {
            if (!proyectil.estaDestruido()) {
                proyectil.dibujar(batch);
            }
        }
        
        // Dibujar todo
        gestorEntidades.dibujar(batch);
        nave.draw(batch, this);
    }
    
    // NUEVO CAMBIO: Método para mostrar la pantalla de victoria
    private void mostrarPantallaVictoria() {
        Screen pantallaVictoria = new PantallaVictoria(game);
        pantallaVictoria.resize(1200, 800);
        game.setScreen(pantallaVictoria);
        dispose();
    }

    /**
     * Verifica y realiza cambios de estado del juego
     */
    private void verificarCambioEstado() {
        // Transición de oleada normal a mini jefe
        if (estadoActual == EstadosJuego.JUGANDO_ENEMIGOS && 
            gestorSpawn.getEnemigosSpawneados() >= gestorSpawn.getTotalEnemigosRonda() && 
            gestorEntidades.getCantidadEnemigos() == 0) {
            
            // Crear mini jefe con proyectiles del escenario actual
            Texture texturaMiniJefe;
            String tipoBoss;

            // Determinar tipo de minijefe según el escenario actual
            if (ronda <= 2) {
                // Rondas 1-2: Minijefe Bosque
                texturaMiniJefe = GestorAssets.get().getTextura("boss-bosque");
                tipoBoss = "BOSQUE";
            } else {
                // Rondas 3-4: Minijefe Ciudad  
                texturaMiniJefe = GestorAssets.get().getTextura("boss-ciudad");
                tipoBoss = "CIUDAD";
            }
            
            Texture texturaProyectil = escenarioFactory.getTexturaProyectilMiniJefe();
            float escalaProyectil = escenarioFactory.getEscalaProyectilMiniJefe();
            int danioProyectil = escenarioFactory.getDanioProyectilMiniJefe();
            
            miniJefe = new MiniJefe(
                    WORLD_WIDTH / 2f,
                    WORLD_HEIGHT - 100f,
                    texturaMiniJefe,
                    nave,
                    texturaProyectil,
                    escalaProyectil,
                    danioProyectil,
                    tipoBoss 
            );
            miniJefe.escalarPorRonda(ronda);
            estadoActual = EstadosJuego.MINIJEFE;
            
            // Configurar callback para proyectiles del mini jefe
            if (miniJefe instanceof MiniJefe) {
                ((MiniJefe) miniJefe).setOnDisparoCallback(proyectil -> {
                    proyectilesEnemigos.add(proyectil);
                });
            }
            
            // Actualizar UI con referencia al boss
            gestorUI.setBossActual(miniJefe);
        }

        // Transición a siguiente ronda
        if (estadoActual == EstadosJuego.TRANSICION_RONDA && tiempoTransicion <= 0) {
            // NUEVO CAMBIO CORREGIDO: Lógica de progresión según ronda actual
            if (ronda == 4) {
                // Después de derrotar al minijefe Ciudad en ronda 4, ir DIRECTAMENTE al Boss Final
                System.out.println("¡Transición al BOSS FINAL!");
                iniciarBossFinal();
            } else if (ronda == 5) {
                // Esto no debería ocurrir normalmente, pero por seguridad
                mostrarPantallaVictoria();
            } else {
                // Rondas normales 1-3: Pasar a la siguiente ronda con cambio de escenario
                EscenarioFactory nuevoEscenario;
                
                // NUEVO CAMBIO CORREGIDO: Determinar el próximo escenario correctamente
                if (ronda + 1 <= 2) {
                    // Rondas 1-2: Bosque
                    nuevoEscenario = new EscenarioBosqueFactory();
                } else {
                    // Rondas 3-4: Ciudad
                    nuevoEscenario = new EscenarioCiudadFactory();
                }
                
                Screen siguientePantalla = new PantallaJuego(
                    game, ronda + 1, nave.getVidas(), Puntaje.get().getScore(),
                    velXAsteroides + 1, velYAsteroides + 1, cantAsteroides + 2
                );
                
                // Forzar el cambio de escenario en la siguiente pantalla
                ((PantallaJuego) siguientePantalla).establecerEscenarioFactory(nuevoEscenario);
                siguientePantalla.resize(1200, 800);
                game.setScreen(siguientePantalla);
                dispose();
            }
        }

        // Game over
        if (!nave.estaVivo()) {
            if (musicaFondo != null) {
                musicaFondo.stop();
            }
            
            if (Puntaje.get().getScore() > Puntaje.get().getHighScore())
                Puntaje.get().setHighScore(Puntaje.get().getScore());
                
            Screen pantallaGameOver = new PantallaGameOver(game);
            pantallaGameOver.resize(1200, 800);
            game.setScreen(pantallaGameOver);
            dispose();
        }
    }

    // ==================================================
    // Métodos de compatibilidad
    // ==================================================
    
    /** Permite a Nave4 registrar balas */
    public void agregarBala(Bullet b) {
        gestorEntidades.agregarBala(b);
    }

    // Getters para los managers
    public int getRonda() { return ronda; }
    public int getVelXAsteroides() { return velXAsteroides; }
    public int getVelYAsteroides() { return velYAsteroides; }
    public Nave4 getJugador() { return nave; }

    // Getter para la factory (necesario para GestorSpawn)
    public EscenarioFactory getEscenarioFactory() {
        return escenarioFactory;
    }

    // NUEVO CAMBIO: Método para forzar un escenario específico
    public void establecerEscenarioFactory(EscenarioFactory factory) {
        this.escenarioFactory = factory;
        aplicarEscenarioActual();
    }

    // ==================================================
    // Métodos del ciclo de vida Screen
    // ==================================================
    @Override
    public void show() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    @Override
    public void resume() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }

    @Override
    public void hide() {
        if (musicaFondo != null) {
            musicaFondo.pause();
        }
    }

    @Override
    public void dispose() {
        // Los assets son gestionados por GestorAssets
        // No es necesario liberarlos aquí individualmente
    }
}
