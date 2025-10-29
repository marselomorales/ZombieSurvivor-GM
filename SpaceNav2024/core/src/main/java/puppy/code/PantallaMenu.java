package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Pantalla de menú principal con fondo implementado.
 * Muestra las instrucciones del juego y espera la interacción del usuario para comenzar.
 * MOD: Sistema de viewport para escalado consistente independiente de la resolución.
 */
public class PantallaMenu implements Screen {

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Texture texturaFondo;
	private Sprite spriteFondo;

	// MOD: Constantes del mundo lógico
	private static final float WORLD_WIDTH = 1200f;
	private static final float WORLD_HEIGHT = 800f;

	public PantallaMenu(SpaceNavigation game) {
		this.game = game;
        
		// MOD: Sistema de viewport para escalado consistente
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		
		// Cargar textura de fondo para el menú
		texturaFondo = new Texture(Gdx.files.internal("fondo-menu.jpg"));
		spriteFondo = new Sprite(texturaFondo);
		spriteFondo.setSize(WORLD_WIDTH, WORLD_HEIGHT);
		spriteFondo.setPosition(0, 0);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		// MOD: Aplicar viewport para escalado consistente
		viewport.apply();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		
		// Dibujar fondo primero
		spriteFondo.draw(game.getBatch());
		
		// MOD: Texto cambiado a temática de supervivencia zombi
		game.getFont().draw(game.getBatch(), "Bienvenido a Zombie Survival!", 140, 400);
		game.getFont().draw(game.getBatch(), "Pincha o presiona cualquier tecla para comenzar ...", 100, 300);
		game.getFont().draw(game.getBatch(), "Controles: WASD/Flechas - Movimiento | ESPACIO/CLIC - Disparar", 100, 200);
	
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
	public void resize(int width, int height) {
		// MOD: Actualizar viewport al cambiar tamaño
		viewport.update(width, height);
	}
	
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