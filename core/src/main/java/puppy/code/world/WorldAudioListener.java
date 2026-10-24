package puppy.code.world;

/** Contrato desacoplado para escuchar eventos del mundo de juego. */
public interface WorldAudioListener {
    void onPlayerShoot();
    void onPlayerDash();
    void onPlayerHurt(float centerX, float centerY);
    void onAlienHurt();
    void onAlienDestroyed(float centerX, float centerY);
    void onAlienShoot();
    void onMeteoriteHit();
    void onMeteoriteDestroyed(float centerX, float centerY, boolean isMajor);
    void onLevelCompleted();
}