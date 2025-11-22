package puppy.code.gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class GestorAssets implements Disposable {
    private static GestorAssets instancia;
    
    private Map<String, Texture> texturas;
    private Map<String, Sound> sonidos;
    private Map<String, Music> musicas;
    
    private GestorAssets() {
        texturas = new HashMap<>();
        sonidos = new HashMap<>();
        musicas = new HashMap<>();
        cargarTodosLosAssets();
    }
    
    public static GestorAssets get() {
        if (instancia == null) {
            instancia = new GestorAssets();
        }
        return instancia;
    }
    
    private void cargarTodosLosAssets() {
        // ==================== TEXTURAS ====================
        
        // Jugador y elementos base
        cargarTextura("survivor", "survivor.png");
        cargarTextura("bullet", "bullet.png");
        
        // Fondos de pantallas
        cargarTextura("fondo-menu", "fondo-menu.jpg");
        cargarTextura("fondo-gameover", "fondo-gameover.jpg");
        cargarTextura("fondo-instrucciones", "fondo-instrucciones.jpg");
        
        // NUEVO CAMBIO: Añadir fondo de victoria
        cargarTextura("fondo-victoria", "fondo-victoria.png");
        
        // Fondos temáticos para escenarios
        cargarTextura("fondo-bosque", "fondo-bosque.jpg");
        cargarTextura("fondo-ciudad", "fondo-ciudad.jpg");
        cargarTextura("fondo-ibc", "fondo-ibc.png");
        
        // Zombies temáticos
        cargarTextura("zombie-bosque", "zombie-bosque.png");
        cargarTextura("zombie-ciudad", "zombie-ciudad.png");
        
        cargarTextura("boss-bosque", "boss-bosque.png");
        cargarTextura("boss-ciudad", "boss-ciudad.png");
        cargarTextura("boss-profe", "boss-profe.png");
                
        // Proyectiles para mini jefes 
        cargarTextura("proyectil-piedra", "piedra.png");
        cargarTextura("proyectil-auto", "auto.png");
        cargarTextura("mini-jefe-base", "mini-jefe-base.png");
        
        // Texturas para ventajas
        cargarTextura("ventaja-disparo", "ventaja-disparo.png");
        cargarTextura("ventaja-disparo-gris", "ventaja-disparo-gris.png");
        cargarTextura("ventaja-inmortalidad", "ventaja-inmortalidad.png");
        cargarTextura("ventaja-inmortalidad-gris", "ventaja-inmortalidad-gris.png");
        cargarTextura("ventaja-vidas", "ventaja-vidas.png");
        cargarTextura("ventaja-vidas-gris", "ventaja-vidas-gris.png");
        
        // ==================== SONIDOS ====================
        cargarSonido("explosion", "zombie-death.ogg");
        cargarSonido("player-hurt", "player-hurt.ogg");
        cargarSonido("gun-shot", "gun-shot.ogg");
        
        // ==================== MÚSICA ====================
        cargarMusica("musica-bosque", "musica-bosque.wav");
        cargarMusica("musica-ciudad", "musica-ciudad.wav");
    }
    
    // Método mejorado para cargar texturas con mejor manejo de errores
    private void cargarTextura(String nombre, String ruta) {
        try {
            // Verificar si el archivo existe antes de intentar cargarlo
            if (Gdx.files.internal(ruta).exists()) {
                texturas.put(nombre, new Texture(Gdx.files.internal(ruta)));
                System.out.println("✓ Textura cargada: " + nombre);
            } else {
                System.err.println("✗ Archivo no encontrado: " + ruta + " - Usando fallback para: " + nombre);
                // Crear una textura de fallback simple
                crearTexturaFallback(nombre);
            }
        } catch (Exception e) {
            System.err.println("✗ Error cargando textura: " + ruta + " - " + e.getMessage());
            crearTexturaFallback(nombre);
        }
    }
    
    // Crear texturas de fallback cuando las originales no se cargan
    private void crearTexturaFallback(String nombre) {
        try {
            // Crear una textura simple de 32x32 píxeles como fallback
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            
            // Asignar colores según el tipo de textura
            if (nombre.contains("ventaja")) {
                if (nombre.contains("disparo")) {
                    pixmap.setColor(1, 0, 0, 1); // Rojo para disparo
                } else if (nombre.contains("inmortalidad")) {
                    pixmap.setColor(0, 1, 0, 1); // Verde para inmortalidad
                } else if (nombre.contains("vidas")) {
                    pixmap.setColor(0, 0, 1, 1); // Azul para vidas
                } else {
                    pixmap.setColor(1, 1, 0, 1); // Amarillo para otras ventajas
                }
            } else if (nombre.contains("proyectil")) {
                pixmap.setColor(0.5f, 0.5f, 0.5f, 1); // Gris para proyectiles
            } else {
                pixmap.setColor(1, 0, 1, 1); // Magenta para otros (fácil de identificar)
            }
            
            pixmap.fill();
            texturas.put(nombre, new Texture(pixmap));
            pixmap.dispose();
            System.out.println("✓ Textura fallback creada: " + nombre);
        } catch (Exception e) {
            System.err.println("✗ Error creando fallback para: " + nombre);
        }
    }
    
    private void cargarSonido(String nombre, String ruta) {
        try {
            sonidos.put(nombre, Gdx.audio.newSound(Gdx.files.internal(ruta)));
            System.out.println("✓ Sonido cargado: " + nombre);
        } catch (Exception e) {
            System.err.println("✗ Error cargando sonido: " + ruta);
        }
    }
    
    private void cargarMusica(String nombre, String ruta) {
        try {
            musicas.put(nombre, Gdx.audio.newMusic(Gdx.files.internal(ruta)));
            System.out.println("✓ Música cargada: " + nombre);
        } catch (Exception e) {
            System.err.println("✗ Error cargando música: " + ruta);
        }
    }
    
    // Getter mejorado con fallbacks más robustos
    public Texture getTextura(String nombre) { 
        Texture textura = texturas.get(nombre);
        if (textura == null) {
            System.err.println("Textura no encontrada en cache: " + nombre + ", creando fallback...");
            crearTexturaFallback(nombre);
            textura = texturas.get(nombre);
        }
        return textura;
    }
    
    public Sound getSonido(String nombre) { 
        Sound sonido = sonidos.get(nombre);
        if (sonido == null) {
            System.err.println("Sonido no encontrado: " + nombre);
        }
        return sonido;
    }
    
    public Music getMusica(String nombre) { 
        Music musica = musicas.get(nombre);
        if (musica == null) {
            System.err.println("Música no encontrada: " + nombre);
            // Intentar cualquier música disponible
            if (!musicas.isEmpty()) {
                musica = musicas.values().iterator().next();
            }
        }
        return musica;
    }
    
    // Método para verificar si una textura existe
    public boolean existeTextura(String nombre) {
        boolean existe = texturas.containsKey(nombre) && texturas.get(nombre) != null;
        System.out.println("Textura '" + nombre + "' existe: " + existe);
        return existe;
    }
    
    @Override
    public void dispose() {
        for (Texture textura : texturas.values()) {
            textura.dispose();
        }
        for (Sound sonido : sonidos.values()) {
            sonido.dispose();
        }
        for (Music musica : musicas.values()) {
            musica.dispose();
        }
    }
    
    // Método para debug: listar assets cargados
    public void listarAssets() {
        System.out.println("=== TEXTURAS CARGADAS ===");
        for (String nombre : texturas.keySet()) {
            System.out.println("  " + nombre);
        }
        System.out.println("=== MÚSICAS CARGADAS ===");
        for (String nombre : musicas.keySet()) {
            System.out.println("  " + nombre);
        }
    }
}
