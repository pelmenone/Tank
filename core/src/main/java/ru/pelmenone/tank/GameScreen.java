package ru.pelmenone.tank;

import static javax.management.Query.or;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;

public class GameScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private enum MovementDirection {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private MovementDirection currentDirection = MovementDirection.NONE;
    private MovementDirection lastPressedDirection = MovementDirection.NONE;

    // мир
    private static final float WORLD_WIDTH = 1280; // Логическая ширина мира
    private static final float WORLD_HEIGHT = 720; // Логическая высота

    // текстуры
    private Texture tankTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture wallTexture;
    private Texture kustTexture;
    private Texture backgroundTexture;
    private Texture backgroundTexture2;
    private Texture buttonTexture;
    private Texture whiteTexture;
    private Rectangle restartButton;

    // звуки
    private SoundManager sounds;

    // вращение текстуры
    private float tankRotation = 0;

    // объекты
    private Rectangle playerTank;
    private Array<Rectangle> enemies;
    private Array<Rectangle> walls;
    private Array<Rectangle> kusts;
    private Array<Bullet> bullets;

    // управление
    private boolean moveUp, moveDown, moveLeft, moveRight;
    private float playerSpeed = 100f;
    private float timeSinceLastShot = 0;
    private float shootDelay = 0.6f;

    // кнопка
    private Texture fireButtonTexture;
    private Texture fireButtonPressedTexture;

    private Rectangle fireButton;

    private boolean isFireButtonPressed = false;
    private boolean wasFireButtonJustPressed = false;


    private int enemiesRemaining;
    private boolean gameWon;
    private BitmapFont font;

    //время
    private float timer = 0;
    private final float INTERVAL = 2.0f;


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
        kustTexture = new Texture("kust.png");
        backgroundTexture2 = new Texture("background2.png");
        backgroundTexture = new Texture("background.png");
        fireButtonTexture = new Texture("fire_button.png");
        fireButtonPressedTexture = new Texture("fire_button_pressed.png");
        playerTank = new Rectangle(100, 100, 80, 80);

        // Инициализация игрока
        playerTank = new Rectangle();
        playerTank.x = 100;
        playerTank.y = 100;
        playerTank.width = 40;
        playerTank.height = 40;

        // Инициализация врагов
        enemies = new Array<>();
        spawnEnemies(MathUtils.random(1, 10));

        // Инициализация стен
        walls = new Array<>();
        createWalls();
        kusts = new Array<>();
        createKusts();

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

        int buttonSize = 150; // Размер кнопки
        fireButton = new Rectangle(
            Gdx.graphics.getWidth() - buttonSize - 20,
            20,
            buttonSize,
            buttonSize
        );
    }


    private void spawnEnemies(int count) {
        float counter;

        for (int i = 0; i < count; i++) {
            Rectangle enemy = new Rectangle();
            enemy.x = MathUtils.random(250, 500) + i * 100;
            enemy.y = MathUtils.random(100, 400) + i * 100;
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
        for (int i = 0; i < 10; i++) {
            Rectangle wall = new Rectangle();
            wall.x = 490 + i * 40;
            wall.y = 300;
            wall.width = 40;
            wall.height = 40;
            walls.add(wall);
        }
        // Горизонтальные стены
        for (int i = 0; i < 10; i++) {
            Rectangle wall = new Rectangle();
            wall.x = 490 + i * 40;
            wall.y = 300;
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
        for (int i = 0; i < 2; i++) {
            Rectangle wall = new Rectangle();
            wall.x = 200;
            wall.y = 400 + i * 40;
            wall.width = 40;
            wall.height = 40;
            walls.add(wall);
        }
    }

    private void createKusts() {
        // kusts горизонт
        for (int i = 0; i < 3; i++) {
            Rectangle kust = new Rectangle();
            kust.x = 0 + i * 40;
            kust.y = 60;
            kust.width = 40;
            kust.height = 40;
            kusts.add(kust);
        }

        // kusts вертик
        for (int i = 0; i < 5; i++) {
            Rectangle kust = new Rectangle();
            kust.x = 40;
            kust.y = 60 + i * 40;
            kust.width = 40;
            kust.height = 40;
            kusts.add(kust);
        }
    }

    @Override
    public void render(float delta) {
        timer += delta; // Увеличиваем таймер на время, прошедшее с прошлого кадра

        if (timer >= INTERVAL) {
            timer = 0; // Сбрасываем таймер
            shoot();
        }
        // Очистка экрана
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление
        update(delta);

        // Установка камеры
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Отрисовка
        game.batch.begin();

        // Фон
        game.batch.draw(backgroundTexture, 0, 0, 800, 480);

        // Стены
        for (Rectangle wall : walls) {
            game.batch.draw(wallTexture, wall.x, wall.y, wall.width, wall.height);
        }
        // Игрок
        float textureBaseRotation = 90;;
        game.batch.draw(tankTexture,
            playerTank.x, playerTank.y,          // Позиция
            playerTank.width/2, playerTank.height/2, // Точка вращения (центр)
            playerTank.width, playerTank.height,  // Размер
            1, 1,                                // Масштаб
            tankRotation,                        // Угол поворота
            0, 0,                                // Область текстуры (srcX, srcY)
            tankTexture.getWidth(), tankTexture.getHeight(), // Размер текстуры
            false, false);                       // Отражать по X/Y?

        // Враги
        for (Rectangle enemy : enemies) {
            game.batch.draw(enemyTexture, enemy.x, enemy.y, enemy.width, enemy.height);
        }
        // Пули
        for (Bullet bullet : bullets) {
            game.batch.draw(bulletTexture, bullet.rect.x, bullet.rect.y, bullet.rect.width, bullet.rect.height);
        }
        // KUSTS
        for (Rectangle kust : kusts) {
            game.batch.draw(kustTexture, kust.x, kust.y, kust.width, kust.height);
        }
        font.draw(game.batch, "enemies: " + enemiesRemaining, 10, 480);
        font.draw(game.batch, "FIRE", 650, 70);

        if (gameWon) {
            font.draw(game.batch, "WIN", camera.viewportWidth/2 - 30, camera.viewportHeight/2);

            font.draw(game.batch, "AGAIN",
                camera.viewportWidth/2 - 70,
                camera.viewportHeight/2 - 140);

            game.batch.setColor(1, 1, 1, 0.1f); // Прозрачность 70%
            game.batch.draw(whiteTexture,
                camera.viewportWidth/2 -100,
                camera.viewportHeight/2 -200,
                200, 100);
            game.batch.setColor(Color.WHITE); // Возвращаем обычный цвет

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

        // рулить танком
        handleInput(delta);

        // состояние кнопки
        wasFireButtonJustPressed = false;
        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                camera.unproject(touchPos); // Конвертируем в игровые координаты

                if (fireButton.contains(touchPos.x, touchPos.y)) {
                    if (!isFireButtonPressed) {
                        wasFireButtonJustPressed = true; // Только что нажата
                    }
                    isFireButtonPressed = true;
                    break;
                }
            }
        }

        if (!Gdx.input.isTouched()) {
            isFireButtonPressed = false;
        }

        // Стрельба при нажатии
        if (wasFireButtonJustPressed && timeSinceLastShot >= shootDelay) {
            shoot();
            timeSinceLastShot = 0;
        }

        timeSinceLastShot += delta;


        // обнова пуль
        updateBullets(delta);

        // враги муваются
        updateEnemies(delta);

        // проверка столкновений
        checkCollisions();


    }
    public void restartGame() {
        enemies.clear();
        bullets.clear();
        spawnEnemies(MathUtils.random(1, 10)   );
        enemiesRemaining = enemies.size;
        gameWon = false;

        playerTank.x = 100;
        playerTank.y = 100;
    }

    private void handleInput(float delta) {
        boolean moved = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            lastPressedDirection = MovementDirection.UP;
            tankRotation = 90; // Поворот вверх (0 градусов)
            moved = true;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            lastPressedDirection = MovementDirection.DOWN;
            tankRotation = 270; // Поворот вверх (0 градусов)
            moved = true;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            lastPressedDirection = MovementDirection.LEFT;
            tankRotation = 180; // Поворот вверх (0 градусов)
            moved = true;
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            lastPressedDirection = MovementDirection.RIGHT;
            tankRotation = 0; // Поворот вверх (0 градусов)
            moved = true;
        }

        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (!upPressed && !downPressed && !leftPressed && !rightPressed) {
            currentDirection = MovementDirection.NONE;
        }
        else if ((upPressed ? 1 : 0) + (downPressed ? 1 : 0) +
            (leftPressed ? 1 : 0) + (rightPressed ? 1 : 0) == 1) {
            if (upPressed) currentDirection = MovementDirection.UP;
            if (downPressed) currentDirection = MovementDirection.DOWN;
            if (leftPressed) currentDirection = MovementDirection.LEFT;
            if (rightPressed) currentDirection = MovementDirection.RIGHT;
        }
        else {
            currentDirection = lastPressedDirection;
        }

        switch (currentDirection) {
            case UP:
                playerTank.y += playerSpeed * delta;
                break;
            case DOWN:
                playerTank.y -= playerSpeed * delta;
                break;
            case LEFT:
                playerTank.x -= playerSpeed * delta;
                break;
            case RIGHT:
                playerTank.x += playerSpeed * delta;
                break;
            case NONE:
                break;
        }

        playerTank.x = Math.max(0, Math.min(800 - playerTank.width, playerTank.x));
        playerTank.y = Math.max(0, Math.min(480 - playerTank.height, playerTank.y));

        if (playerTank.x < 0) playerTank.x = 0;
        if (playerTank.x > 800 - playerTank.width) playerTank.x = 800 - playerTank.width;
        if (playerTank.y < 0) playerTank.y = 0;
        if (playerTank.y > 480 - playerTank.height) playerTank.y = 480 - playerTank.height;

        // стрельба
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && timeSinceLastShot >= shootDelay) {
            shoot();
            timeSinceLastShot = 0;
        }

        // для мобилы
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
        bullet.rect.x = playerTank.x + playerTank.width/2 - 8;
        bullet.rect.y = playerTank.y + playerTank.height/2 - 9;

        bullet.rect.width = 15;
        bullet.rect.height = 15;
        float angleRad = (float)Math.toRadians(tankRotation);

        // Направление выстрела (единичный вектор)
        if (lastPressedDirection == MovementDirection.RIGHT) {
            bullet.direction = new Vector2(1, 0);
        }
        if (lastPressedDirection == MovementDirection.UP) {
            bullet.direction = new Vector2(0, 1);
        }
        if (lastPressedDirection == MovementDirection.LEFT) {
            bullet.direction = new Vector2(-1, 0);
        }
        if (lastPressedDirection == MovementDirection.DOWN) {
            bullet.direction = new Vector2(0, -1);
        }
        if (lastPressedDirection == MovementDirection.NONE) {
            bullet.direction = new Vector2(1, 0);
        }
        bullet.speed = 500f;
        bullets.add(bullet);
        // звук выстрела
        sounds.shootSound.play(0.5f, MathUtils.random(0.8f, 1.1f), 0f);
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
        for (Rectangle enemy : enemies) {
            if (enemy.x < playerTank.x) enemy.x += 50 * delta;
            if (enemy.x > playerTank.x) enemy.x -= 50 * delta;
            if (enemy.y < playerTank.y) enemy.y += 50 * delta;
            if (enemy.y > playerTank.y) enemy.y -= 50 * delta;
        }

        for (Rectangle enemy : enemies) {
            // Рассчитываем направление к игроку
            Vector2 direction = new Vector2(
                playerTank.x - enemy.x,
                playerTank.y - enemy.y
            ).nor(); // Нормализуем вектор

            // Сохраняем старую позицию
            float oldX = enemy.x;
            float oldY = enemy.y;

            // Двигаем врага
            float enemySpeed = 0;
            enemy.x += direction.x * enemySpeed * delta;
            enemy.y += direction.y * enemySpeed * delta;

            // Проверка столкновений со стенами
            for (Rectangle wall : walls) {
                if (enemy.overlaps(wall)) {
                    // Вычисляем вектор отталкивания
                    Vector2 push = new Vector2();

                    // Определяем сторону столкновения
                    float overlapX = Math.min(
                        enemy.x + enemy.width - wall.x,
                        wall.x + wall.width - enemy.x
                    );

                    float overlapY = Math.min(
                        enemy.y + enemy.height - wall.y,
                        wall.y + wall.height - enemy.y
                    );

                    // Отталкиваем по меньшему пересечению
                    if (overlapX < overlapY) {
                        push.x = (enemy.x < wall.x) ? -overlapX : overlapX;
                    } else {
                        push.y = (enemy.y < wall.y) ? -overlapY : overlapY;
                    }

                    // Применяем отталкивание
                    enemy.x += push.x * 1f;
                    enemy.y += push.y * 1f;
                }
            }
        }
    }

    private void checkCollisions() {
        // Проверка столкновений пуль с врагами
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = enemies.size - 1; j >= 0; j--) {
                if (bullet.rect.overlaps(enemies.get(j))) {
                    sounds.enemyHitSound.play(0.5f, MathUtils.random(0.1f, 0.3f), 0f);
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
                    sounds.wallHitSound.play(0.5f, MathUtils.random(0.4f, 0.7f), 0f);
                    bullets.removeIndex(i);
                    break;
                }
            }
        }

        // Проверка столкновений игрока со стенами
        for (Rectangle wall : walls) {
            if (playerTank.overlaps(wall)) {
                if (moveUp) playerTank.y -= 1;
                if (moveDown) playerTank.y += 1;
                if (moveLeft) playerTank.x += 1;
                if (moveRight) playerTank.x -= 1;
            }
        }
        for (Rectangle enemies: enemies){
            if (playerTank.overlaps(enemies)) {
                if (moveUp) playerTank.y -= 1;
                if (moveDown) playerTank.y += 1;
                if (moveLeft) playerTank.x += 1;
                if (moveRight) playerTank.x -= 1;
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

        // звуки
        sounds = new SoundManager();
        sounds.load();
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
        kustTexture.dispose();
        sounds.dispose();
        backgroundTexture.dispose();
        font.dispose();
        buttonTexture.dispose();
        whiteTexture.dispose();
        fireButtonTexture.dispose();
        fireButtonPressedTexture.dispose();

    }

    static class Bullet {
        public float directionX;
        public float directionY;
        Rectangle rect;
        Vector2 direction;
        float speed;
    }
    }




