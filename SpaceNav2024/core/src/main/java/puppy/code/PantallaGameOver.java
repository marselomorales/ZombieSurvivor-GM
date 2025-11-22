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
 * Pantalla de Game Over actualizada para usar GestorAssets
 */
public class PantallaGameOver implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Sprite spriteFondo;

	public PantallaGameOver(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1200, 800);
		
		// Cargar fondo usando GestorAssets
		Texture texturaFondo = GestorAssets.get().getTextura("fondo-gameover");
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
		
		// Textos de game over
		game.getFont().draw(game.getBatch(), "¡Has sido infectado!", 120, 400,400,1,true);
		// NUEVO CAMBIO: Texto actualizado para reflejar solo ENTER
		game.getFont().draw(game.getBatch(), "Presiona ENTER para reintentar", 100, 300);
	
		game.getBatch().end();

		// NUEVO CAMBIO: Solo responde a ENTER específicamente
		// Se eliminó Input.Keys.ANY_KEY e isTouched para evitar saltos accidentales
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			Screen ss = new PantallaJuego(game,1,3,0,1,1,10);
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
