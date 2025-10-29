package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Pantalla de Game Over con fondo implementado.
 * Se muestra cuando el jugador pierde todas sus vidas y permite reiniciar el juego.
 */
public class PantallaGameOver implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Texture texturaFondo;
	private Sprite spriteFondo;

	public PantallaGameOver(SpaceNavigation game) {
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1200, 800);
		
		// Cargar fondo para pantalla de Game Over
		texturaFondo = new Texture(Gdx.files.internal("fondo-gameover.jpg"));
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
		
		// Dibujar fondo primero
		spriteFondo.draw(game.getBatch());
		
		// MOD: Texto cambiado a temática zombi
		game.getFont().draw(game.getBatch(), "¡Has sido infectado!", 120, 400,400,1,true);
		game.getFont().draw(game.getBatch(), "Pincha o presiona cualquier tecla para reintentar ...", 100, 300);
	
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
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
		// Liberar recursos del fondo
		if (texturaFondo != null) {
			texturaFondo.dispose();
		}
	}
}