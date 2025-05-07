package ru.pelmenone.tank;

import static javax.management.Query.or;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.swing.JButton;

public class GameScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;

    // Текстуры
    private Texture tankTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture wallTexture;
    private Texture backgroundTexture;
    private Texture backgroundTexture2;
    private Texture buttonTexture;
    private Texture whiteTexture;
    private Rectangle restartButton;

    // Игровые объекты
    private Rectangle playerTank;
    private Array<Rectangle> enemies;
    private Array<Rectangle> walls;
    private Array<Bullet> bullets;

    // Управление
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private float playerSpeed = 100f;
    private float timeSinceLastShot = 0;
    private float shootDelay = 0.6f;

    private int enemiesRemaining;
    private boolean gameWon;
    private BitmapFont font;

    public GameScreen(final Main game) {
        this.game = game;

        // Настройка камеры
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Загрузка текстур
        tankTexture = new Texture("tank.png");
        enemyTexture = new Texture("enemy.png");
        bulletTexture = new Texture("bullet.png");
        wallTexture = new Texture("wall.png");
        backgroundTexture2 = new Texture("background2.png");
        backgroundTexture = new Texture("background.png");

        // Инициализация игрока
        playerTank = new Rectangle();
        playerTank.x = 100;
        playerTank.y = 100;
        playerTank.width = 40;
        playerTank.height = 40;

        // Инициализация врагов
        enemies = new Array<>();
        spawnEnemies(2);

        // Инициализация стен
        walls = new Array<>();
        createWalls();

        // Инициализация пуль
        bullets = new Array<>();

        enemiesRemaining = enemies.size;
        gameWon = false;

        font = new BitmapFont();
        font.getData().setScale(3);

        Pixmap buttonPixmap = new Pixmap(160, 80, Pixmap.Format.RGBA8888);
        buttonPixmap.setColor(0.2f, 0.8f, 0.2f, 1); // Зеленый цвет
        buttonPixmap.fillRectangle(0, 0, 160, 80);
        buttonPixmap.setColor(Color.WHITE);
        buttonPixmap.drawRectangle(0, 0, 160, 80); // Белая рамка
        buttonTexture = new Texture(buttonPixmap);
        buttonPixmap.dispose();

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();

        restartButton = new Rectangle(
            camera.viewportWidth/2 - 80,  // x
            camera.viewportHeight/2 - 50, // y
            160,                          // width
            80                            // height
        );
    }


    private void spawnEnemies(int count) {
        for (int i = 0; i < count; i++) {
            Rectangle enemy = new Rectangle();
            enemy.x = 600 + i * 100;
            enemy.y = 100 + i * 100;
            enemy.width = 40;
            enemy.height = 40;
            enemies.add(enemy);
        }
    }

    private void createWalls() {
        // Горизонтальные стены
        for (int i = 0; i < 10; i++) {
            Rectangle wall = new Rectangle();
            wall.x = 200 + i * 40;
            wall.y = 200;
            wall.width = 40;
            wall.height = 40;
            walls.add(wall);
        }

        // Вертикальные стены
        for (int i = 0; i < 5; i++) {
            Rectangle wall = new Rectangle();
            wall.x = 400;
            wall.y = 300 + i * 40;
            wall.width = 40;
            wall.height = 40;
            walls.add(wall);
        }
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        ScreenUtils.clear(0, 0, 0, 1);

        // Обновление
        update(delta);

        // Установка камеры
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Отрисовка
        game.batch.begin();
        font.draw(game.batch, "enemies: " + enemiesRemaining, 20, 450);


        // Фон
        game.batch.draw(backgroundTexture2, 0, 0, 800, 480);
        game.batch.draw(backgroundTexture, 0, 0, 800, 480);

        // Стены
        for (Rectangle wall : walls) {
            game.batch.draw(wallTexture, wall.x, wall.y, wall.width, wall.height);
        }

        // Игрок
        game.batch.draw(tankTexture, playerTank.x, playerTank.y, playerTank.width, playerTank.height);

        // Враги
        for (Rectangle enemy : enemies) {
            game.batch.draw(enemyTexture, enemy.x, enemy.y, enemy.width, enemy.height);
        }

        // Пули
        for (Bullet bullet : bullets) {
            game.batch.draw(bulletTexture, bullet.rect.x, bullet.rect.y, bullet.rect.width, bullet.rect.height);
        }

        if (gameWon) {
            font.draw(game.batch, "WIN",
                camera.viewportWidth/2 - 30,
                camera.viewportHeight/2);

            font.draw(game.batch, "Tap to continue",
                camera.viewportWidth/2 - 120,
                camera.viewportHeight/2 - 50);

            game.batch.setColor(1, 1, 1, 0.7f); // Прозрачность 70%
            game.batch.draw(whiteTexture,
                camera.viewportWidth/2,
                camera.viewportHeight/2 - 200,
                200, 100);
            game.batch.setColor(Color.WHITE); // Возвращаем обычный цвет

            // Рисуем кнопку
            game.batch.draw(buttonTexture,
                camera.viewportWidth/2 - 80,
                camera.viewportHeight/2 - 100,
                160, 80);

            // Текст на кнопке
            font.draw(game.batch, "AGAIN",
                camera.viewportWidth/2 - 40,
                camera.viewportHeight/2 - 50);

        }

        game.batch.end();
    }



    private void update(float delta) {
        if (gameWon) {
            // Проверка для мобильных устройств (касание экрана)
            boolean mobileTouch = false;
            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                mobileTouch = restartButton.contains(touchPos.x, touchPos.y);
            }

            // Проверка для ПК (клавиша E)
            boolean pcInput = Gdx.input.isKeyJustPressed(Input.Keys.E);

            // Если любое из условий выполнено - перезапускаем игру
            if (mobileTouch || pcInput) {
                restartGame();
            }
            return;
        }

        timeSinceLastShot += delta;

        // Управление танком
        handleInput(delta);

        // Обновление пуль
        updateBullets(delta);

        // ИИ врагов
        updateEnemies(delta);

        // Проверка столкновений
        checkCollisions();
    }
    public void restartGame() {
        enemies.clear();
        bullets.clear();
        spawnEnemies(1);
        enemiesRemaining = enemies.size;
        gameWon = false;

        playerTank.x = 100;
        playerTank.y = 100;
    }

    private void handleInput(float delta) {
        // Движение
        if (moveUp) playerTank.y += playerSpeed * delta;
        if (moveDown) playerTank.y -= playerSpeed * delta;
        if (moveLeft) playerTank.x -= playerSpeed * delta;
        if (moveRight) playerTank.x += playerSpeed * delta;

        // Ограничение движения в пределах экрана
        if (playerTank.x < 0) playerTank.x = 0;
        if (playerTank.x > 800 - playerTank.width) playerTank.x = 800 - playerTank.width;
        if (playerTank.y < 0) playerTank.y = 0;
        if (playerTank.y > 480 - playerTank.height) playerTank.y = 480 - playerTank.height;

        // Стрельба
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && timeSinceLastShot >= shootDelay) {
            shoot();
            timeSinceLastShot = 0;
        }

        // Обработка касаний для мобильных устройств
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (touchPos.y > playerTank.y + playerTank.height/2) {
                playerTank.y += playerSpeed * delta;
            } else if (touchPos.y < playerTank.y + playerTank.height/2) {
                playerTank.y -= playerSpeed * delta;
            }

            if (touchPos.x > playerTank.x + playerTank.width/2) {
                playerTank.x += playerSpeed * delta;
            } else if (touchPos.x < playerTank.x + playerTank.width/2) {
                playerTank.x -= playerSpeed * delta;
            }
        }
    }

    private void shoot() {
        Bullet bullet = new Bullet();
        bullet.rect = new Rectangle();
        bullet.rect.x = playerTank.x + playerTank.width/2 - 5;
        bullet.rect.y = playerTank.y + playerTank.height/2 - 5;
        bullet.rect.width = 20;
        bullet.rect.height = 20;
        bullet.direction = new Vector2(1, 0);
        bullet.speed = 400f;
        bullets.add(bullet);
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.rect.x += bullet.direction.x * bullet.speed * delta;
            bullet.rect.y += bullet.direction.y * bullet.speed * delta;

            // Удаление пуль за пределами экрана
            if (bullet.rect.x < 0 || bullet.rect.x > 800 ||
                bullet.rect.y < 0 || bullet.rect.y > 480) {
                bullets.removeIndex(i);
            }
        }
    }

    private void updateEnemies(float delta) {
        // Простое ИИ: двигаться к игроку
        for (Rectangle enemy : enemies) {
            if (enemy.x < playerTank.x) enemy.x += 50 * delta;
            if (enemy.x > playerTank.x) enemy.x -= 50 * delta;
            if (enemy.y < playerTank.y) enemy.y += 50 * delta;
            if (enemy.y > playerTank.y) enemy.y -= 50 * delta;
        }
    }

    private void checkCollisions() {
        // Проверка столкновений пуль с врагами
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = enemies.size - 1; j >= 0; j--) {
                if (bullet.rect.overlaps(enemies.get(j))) {
                    bullets.removeIndex(i);
                    enemies.removeIndex(j);
                    enemiesRemaining--;

                    if (enemiesRemaining <= 0) {
                        gameWon = true;
                    }
                    break;
                }
            }

            // Проверка столкновений пуль со стенами
            for (Rectangle wall : walls) {
                if (bullet.rect.overlaps(wall)) {
                    bullets.removeIndex(i);
                    break;
                }
            }
        }

        // Проверка столкновений игрока со стенами
        for (Rectangle wall : walls) {
            if (playerTank.overlaps(wall)) {
                // Простое "отталкивание" от стен
                if (moveUp) playerTank.y -= 5;
                if (moveDown) playerTank.y += 5;
                if (moveLeft) playerTank.x += 5;
                if (moveRight) playerTank.x -= 5;
            }
        }
    }

    @Override
    public void show() {
        // Обработка ввода
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.UP) moveUp = true;
                if (keycode == Input.Keys.DOWN) moveDown = true;
                if (keycode == Input.Keys.LEFT) moveLeft = true;
                if (keycode == Input.Keys.RIGHT) moveRight = true;
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.UP) moveUp = false;
                if (keycode == Input.Keys.DOWN) moveDown = false;
                if (keycode == Input.Keys.LEFT) moveLeft = false;
                if (keycode == Input.Keys.RIGHT) moveRight = false;
                return true;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        tankTexture.dispose();
        enemyTexture.dispose();
        bulletTexture.dispose();
        wallTexture.dispose();
        backgroundTexture.dispose();
        font.dispose();
        buttonTexture.dispose();
        whiteTexture.dispose();

    }

    static class Bullet {
        Rectangle rect;
        Vector2 direction;
        float speed;
    }
    }


