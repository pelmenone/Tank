package ru.pelmenone.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.awt.Rectangle;

public class VirtualButtons {
    private Rectangle upBtn, downBtn, leftBtn, rightBtn;
    private Texture btnTexture, btnPressedTexture;
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private float buttonSize;

    public VirtualButtons(float worldWidth, float worldHeight) {
        buttonSize = Math.min(worldWidth, worldHeight) * 0.15f;
        float padding = buttonSize * 0.5f;
        upBtn = new Rectangle(padding, padding*2 + buttonSize, buttonSize, buttonSize);
        downBtn = new Rectangle(padding, padding, buttonSize, buttonSize);
        leftBtn = new Rectangle(0, padding, buttonSize, buttonSize);
        rightBtn = new Rectangle(padding*2, padding, buttonSize, buttonSize);

        btnTexture = createButtonTexture(Color.GRAY, 0.7f);
        btnPressedTexture = createButtonTexture(Color.DARK_GRAY, 0.9f);
    }

    private Texture createButtonTexture(Color color, float alpha) {
        Pixmap pixmap = new Pixmap((int)buttonSize, (int)buttonSize, Pixmap.Format.RGBA8888);
        pixmap.setColor(color.r, color.g, color.b, alpha);
        pixmap.fillCircle((int)buttonSize/2, (int)buttonSize/2, (int)buttonSize/2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void update() {
        upPressed = downPressed = leftPressed = rightPressed = false;

        for (int i = 0; i < 5; i++) { // Поддержка мультитача
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                GameScreen.camera.unproject(touchPos);

                if (upBtn.contains(touchPos.x, touchPos.y)) upPressed = true;
                else if (downBtn.contains(touchPos.x, touchPos.y)) downPressed = true;
                else if (leftBtn.contains(touchPos.x, touchPos.y)) leftPressed = true;
                else if (rightBtn.contains(touchPos.x, touchPos.y)) rightPressed = true;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(upPressed ? btnPressedTexture : btnTexture, upBtn.x, upBtn.y, buttonSize, buttonSize);
        batch.draw(downPressed ? btnPressedTexture : btnTexture, downBtn.x, downBtn.y, buttonSize, buttonSize);
        batch.draw(leftPressed ? btnPressedTexture : btnTexture, leftBtn.x, leftBtn.y, buttonSize, buttonSize);
        batch.draw(rightPressed ? btnPressedTexture : btnTexture, rightBtn.x, rightBtn.y, buttonSize, buttonSize);

        // Рисуем подсказки
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);
        font.draw(batch, "↑", upBtn.x + buttonSize/3, upBtn.y + buttonSize/1.5f);
        font.draw(batch, "↓", downBtn.x + buttonSize/3, downBtn.y + buttonSize/3f);
        font.draw(batch, "←", leftBtn.x + buttonSize/4, leftBtn.y + buttonSize/2f);
        font.draw(batch, "→", rightBtn.x + buttonSize/3, rightBtn.y + buttonSize/2f);
        font.dispose();
    }

    // Геттеры для состояний кнопок
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }

    public void dispose() {
        btnTexture.dispose();
        btnPressedTexture.dispose();
    }

}
