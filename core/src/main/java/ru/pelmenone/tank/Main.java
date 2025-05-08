package ru.pelmenone.tank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.swing.SwingWorker;

public class Main extends Game {
    public SpriteBatch batch;
    public SwingWorker<Object, Object> assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
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
