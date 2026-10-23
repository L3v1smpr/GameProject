package puppy.code.world;

/**
 * Contrato de observación para desacoplar el modelo físico de la reproducción de audio (DIP).
 */
public interface WorldAudioListener {
    void onPlayerShoot();
    void onPlayerDash();
    void onPlayerHurt();
    void onAlienHurt();
    void onAlienDestroyed();
    void onAlienShoot();
    void onMeteoriteDestroyed();
    void onLevelCompleted();
}