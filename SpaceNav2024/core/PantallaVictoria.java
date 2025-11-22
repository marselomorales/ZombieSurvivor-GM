package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;

import puppy.code.gestores.GestorAssets;
import puppy.code.juego.PantallaJuego;
import puppy.code.juego.SpaceNavigation;

/**
 * Pantalla de Victoria al derrotar al BossProfe
 * Muestra opciones para continuar jugando o volver al menú
 */
public class PantallaVictoria implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;
    private Sprite spriteFondo;

    public PantallaVictoria(SpaceNavigation game) {
        this.game = game;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
        
        // Cargar fondo usando GestorAssets
        Texture texturaFondo = GestorAssets.get().getTextura("fondo-victoria");
        spriteFondo = new Sprite(texturaFondo);
        spriteFondo.setSize(1200, 800);
        spriteFondo.setPosition(0, 0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        
        // Dibujar fondo
        spriteFondo.draw(game.getBatch());
        
        // Textos de victoria
        game.getFont().draw(game.getBatch(), "¡HAS DERROTADO AL BOSS FINAL!", 120, 400,400,1,true);
        game.getFont().draw(game.getBatch(), "Presiona ENTER para seguir jugando", 100, 300);
        game.getFont().draw(game.getBatch(), "Presiona ESC para volver al menú principal", 100, 250);
    
        game.getBatch().end();

        // NUEVO CAMBIO: Opciones de la pantalla de victoria
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Continuar jugando - simplemente volvemos al juego actual
            // No hacemos nada, ya que el juego debe continuar normalmente
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Volver al menú principal
            Screen ss = new PantallaMenu(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }
 
    @Override
    public void show() {}
    
    @Override
    public void resize(int width, int height) {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        // Los assets son gestionados por GestorAssets
        // No es necesario liberarlos aquí
    }
}
