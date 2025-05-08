package ru.pelmenone.tank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.swing.SwingWorker;

public class Main extends Game {
    public SpriteBatch batch;
    public SwingWorker<Object, Object> assets;
    private Viewport viewport;
    private OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));

        float virtualWidth = 1280;
        float virtualHeight = 720;

        camera = new OrthographicCamera();
        // Используем FitViewport для автоматического масштабирования
        viewport = new FitViewport(virtualWidth, virtualHeight, camera);
        viewport.apply();

        setScreen(new GameScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
