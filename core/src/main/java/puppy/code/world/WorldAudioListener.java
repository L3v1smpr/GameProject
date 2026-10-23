package puppy.code.world;

/** Contrato desacoplado para escuchar eventos acústicos del mundo de juego. */
public interface WorldAudioListener {
    void onPlayerShoot();
    void onPlayerDash();
    void onPlayerHurt();
    void onAlienHurt();
    void onAlienDestroyed();
    void onAlienShoot();
    void onMeteoriteHit();
    void onMeteoriteDestroyed();
    void onLevelCompleted();
}