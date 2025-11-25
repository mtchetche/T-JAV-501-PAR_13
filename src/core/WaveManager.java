package src.core;

import src.entities.Enemy01;
import src.entities.Enemy02;
import src.entities.Enemy03;
import src.entities.Player;
import src.world.Level;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Gère l’ensemble des vagues d’ennemis du jeu.
 * <p>
 * Le jeu comporte trois vagues successives :
 * </p>
 *
 * <ul>
 *     <li><b>Vague 1 :</b> Enemy01 uniquement</li>
 *     <li><b>Vague 2 :</b> Enemy01 + Enemy02</li>
 *     <li><b>Vague 3 :</b> Enemy01 + Enemy02 + Enemy03</li>
 * </ul>
 *
 * <p>
 * Chaque vague dure 120 secondes. Chaque type d’ennemi possède son propre
 * cooldown de spawn. Les ennemis apparaissent selon des points de spawn
 * définis dans {@code spawnPoints}.
 * </p>
 *
 * <p>
 * Le {@link TimeFreezeManager} gèle les ENNEMIS (déplacements, updates),
 * mais ne fige PAS :
 * </p>
 * <ul>
 *     <li>le timer des vagues</li>
 *     <li>les spawns</li>
 *     <li>le joueur</li>
 * </ul>
 *
 * <p>
 * Le WaveManager se charge de :
 * </p>
 * <ul>
 *     <li>gérer les timers des vagues</li>
 *     <li>faire spawner les ennemis</li>
 *     <li>mettre à jour leur déplacement</li>
 *     <li>passer automatiquement à la vague suivante</li>
 * </ul>
 */
public class WaveManager {

    /** Référence vers le level (collisions, décor). */
    private final Level level;

    /** Référence vers le joueur. */
    private final Player player;

    /** Gère les projectiles ennemis. */
    private final ProjectileManager projectileManager;

    /** Gestion du gel du temps. */
    private final TimeFreezeManager timeFreezeManager;

    /** Vagues successives de 120 secondes chacune. */
    private final Wave wave1;
    private final Wave wave2;
    private final Wave wave3;

    /** Index de la vague actuelle (1 → 3). */
    private int currentWaveIndex = 1;

    /** Listes d'ennemis actifs pour chaque type. */
    private final ArrayList<Enemy01> enemies01 = new ArrayList<>();
    private final ArrayList<Enemy02> enemies02 = new ArrayList<>();
    private final ArrayList<Enemy03> enemies03 = new ArrayList<>();

    /** Cooldowns de spawn pour chaque type d’ennemi. */
    private double cooldown01 = 0;
    private double cooldown02 = 0;
    private double cooldown03 = 0;

    /** Positions possibles de spawn des ennemis. */
    private final double[][] spawnPoints = {
            {20, 100}, {1220, 100},
            {20, 500}, {1220, 500},
            {50, 260}, {1100, 260},
            {550, 350}, {550, 600}
    };

    /**
     * Constructeur du WaveManager.
     *
     * @param level             niveau du jeu (collisions, décor)
     * @param player            joueur
     * @param projectileManager gestion des projectiles ennemis
     * @param timeFreezeManager gestion du gel du temps
     */
    public WaveManager(Level level, Player player,
                       ProjectileManager projectileManager,
                       TimeFreezeManager timeFreezeManager) {

        this.level = level;
        this.player = player;
        this.projectileManager = projectileManager;
        this.timeFreezeManager = timeFreezeManager;

        wave1 = new Wave(120);
        wave2 = new Wave(120);
        wave3 = new Wave(120);
    }

    /**
     * Met à jour la vague en cours, gère les spawns et transitions
     * entre les vagues.
     *
     * @param dt delta time réel
     */
    public void update(double dt) {

        if (currentWaveIndex == 1) {
            wave1.update(dt);
            updateWave1(dt);
            if (wave1.isFinished()) goToNextWave();
        }
        else if (currentWaveIndex == 2) {
            wave2.update(dt);
            updateWave2(dt);
            if (wave2.isFinished()) goToNextWave();
        }
        else if (currentWaveIndex == 3) {
            wave3.update(dt);
            updateWave3(dt);
        }

        // Nettoyage des ennemis morts et retrait du level
        java.util.Iterator<Enemy01> it01 = enemies01.iterator();
        while (it01.hasNext()) {
            Enemy01 e = it01.next();
            if (e == null || e.isDead()) {
                it01.remove();
                if (e != null) level.removeEntity(e);
            }
        }

        java.util.Iterator<Enemy02> it02 = enemies02.iterator();
        while (it02.hasNext()) {
            Enemy02 e = it02.next();
            if (e == null || e.isDead()) {
                it02.remove();
                if (e != null) level.removeEntity(e);
            }
        }

        java.util.Iterator<Enemy03> it03 = enemies03.iterator();
        while (it03.hasNext()) {
            Enemy03 e = it03.next();
            if (e == null || e.isDead()) {
                it03.remove();
                if (e != null) level.removeEntity(e);
            }
        }
    }

    // ===== VAGUE 1 ===========================================================
    /**
     * Met à jour les spawns et déplacements pour la vague 1.
     */
    private void updateWave1(double dt) {

        cooldown01 -= dt;
        if (cooldown01 <= 0) {
            cooldown01 = 8.0;
            spawnEnemy01();
        }

        double enemyDt = (timeFreezeManager != null && timeFreezeManager.isFrozen()) ? 0.0 : dt;

        for (Enemy01 e : enemies01) e.update(enemyDt);
    }

    // ===== VAGUE 2 ===========================================================
    /**
     * Met à jour les spawns et déplacements pour la vague 2.
     */
    private void updateWave2(double dt) {

        cooldown01 -= dt;
        cooldown02 -= dt;

        if (cooldown01 <= 0) {
            cooldown01 = 6.0;
            spawnEnemy01();
        }
        if (cooldown02 <= 0) {
            cooldown02 = 8.0;
            spawnEnemy02();
        }

        double enemyDt = (timeFreezeManager != null && timeFreezeManager.isFrozen()) ? 0.0 : dt;

        for (Enemy01 e : enemies01) e.update(enemyDt);
        for (Enemy02 e : enemies02) e.update(enemyDt);
    }

    // ===== VAGUE 3 ===========================================================
    /**
     * Met à jour les spawns et déplacements pour la vague 3.
     */
    private void updateWave3(double dt) {

        cooldown01 -= dt;
        cooldown02 -= dt;
        cooldown03 -= dt;

        if (cooldown01 <= 0) {
            cooldown01 = 10.0;
            spawnEnemy01();
        }
        if (cooldown02 <= 0) {
            cooldown02 = 12.0;
            spawnEnemy02();
        }
        if (cooldown03 <= 0) {
            cooldown03 = 14.0;
            spawnEnemy03();
        }

        double enemyDt = (timeFreezeManager != null && timeFreezeManager.isFrozen()) ? 0.0 : dt;

        for (Enemy01 e : enemies01) e.update(enemyDt);
        for (Enemy02 e : enemies02) e.update(enemyDt);
        for (Enemy03 e : enemies03) e.update(enemyDt);
    }

    // ===== SPAWNS ============================================================

    /**
     * Fait apparaître un ennemi de type 01 à un point de spawn aléatoire.
     */
    private void spawnEnemy01() {
        int i = (int)(Math.random() * spawnPoints.length);
        enemies01.add(new Enemy01(
                spawnPoints[i][0],
                spawnPoints[i][1],
                level,
                player
        ));
    }

    /**
     * Fait apparaître un ennemi de type 02 à un point de spawn aléatoire.
     */
    private void spawnEnemy02() {
        int i = (int)(Math.random() * spawnPoints.length);
        enemies02.add(new Enemy02(
                spawnPoints[i][0],
                spawnPoints[i][1],
                level,
                player,
                projectileManager
        ));
    }

    /**
     * Fait apparaître un ennemi de type 03 à un point de spawn aléatoire.
     */
    private void spawnEnemy03() {
        int i = (int)(Math.random() * spawnPoints.length);
        enemies03.add(new Enemy03(
                spawnPoints[i][0],
                spawnPoints[i][1],
                level,
                player,
                projectileManager
        ));
    }

    /**
     * Affiche tous les ennemis de la vague.
     *
     * @param g contexte graphique
     */
    public void render(Graphics2D g) {
        for (Enemy01 e : enemies01) e.render(g);
        for (Enemy02 e : enemies02) e.render(g);
        for (Enemy03 e : enemies03) e.render(g);
    }

    /**
     * Passe à la vague suivante, réinitialise les cooldowns.
     */
    public void goToNextWave() {
        currentWaveIndex++;
        if (currentWaveIndex > 3) currentWaveIndex = 3;
        cooldown01 = 0;
        cooldown02 = 0;
        cooldown03 = 0;
    }

    /**
     * @return le temps restant avant la fin de la vague actuelle
     */
    public double getRemainingTime() {

        if (currentWaveIndex == 1) return wave1.getRemainingTime();
        if (currentWaveIndex == 2) return wave2.getRemainingTime();
        return wave3.getRemainingTime();
    }

    /**
     * @return numéro de la vague actuelle (1 → 3)
     */
    public int getCurrentWaveNumber() {
        return currentWaveIndex;
    }

    /** @return liste des Enemy01 actifs */
    public ArrayList<Enemy01> getEnemies01() { return enemies01; }

    /** @return liste des Enemy02 actifs */
    public ArrayList<Enemy02> getEnemies02() { return enemies02; }

    /** @return liste des Enemy03 actifs */
    public ArrayList<Enemy03> getEnemies03() { return enemies03; }
}
