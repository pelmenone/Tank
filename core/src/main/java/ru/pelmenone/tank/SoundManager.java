package ru.pelmenone.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    public Sound shootSound;
    public Sound wallHitSound;
    public Sound enemyHitSound;
    private long lastShootSoundId;
    private float soundsVolume = 0.7f;


    public void load() {
        shootSound = Gdx.audio.newSound(Gdx.files.internal("snd_shoot.mp3"));
        wallHitSound = Gdx.audio.newSound(Gdx.files.internal("snd_hit_wall.mp3"));
        enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("snd_hit_enemy.mp3"));
    }

    public void dispose() {
        shootSound.dispose();
        wallHitSound.dispose();
        enemyHitSound.dispose();
    }

    public void playShoot() {
        shootSound.stop(lastShootSoundId);
        lastShootSoundId = shootSound.play(soundsVolume);
    }
}
