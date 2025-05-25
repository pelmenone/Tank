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

        camera = new OrthographicCamera();
    }

    @Override
    public void resize(int width, int height) {
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
