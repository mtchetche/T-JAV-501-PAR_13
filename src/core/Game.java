package src.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import src.entities.Enemy02;
import src.entities.Enemy03;
import src.entities.Ak47Bullet;
import src.entities.Enemy01;
import src.world.Level;
import src.entities.Player;
import src.ui.HUD;
import src.input.KeyboardInput;
import src.items.ItemPickupManager;

/**
 * Classe principale du jeu Syntax Error 2D.
 *
 * <p>Elle assure la gestion globale du gameplay, des états d’écran et de tous
 * les gestionnaires secondaires (vagues, projectiles, bonus, inventaire, etc.).
 *
 * <h2>Responsabilités principales :</h2>
 * <ul>
 *     <li>Gérer les différents états d'écran :
 *         <ul>
 *             <li>Menu principal</li>
 *             <li>Partie en cours</li>
 *             <li>Pause</li>
 *             <li>Game Over</li>
 *         </ul>
 *     </li>
 *     <li>Mettre à jour le joueur, les ennemis, les projectiles et les bonus</li>
 *     <li>Gérer le système de vagues d'ennemis</li>
 *     <li>Gestion de l'inventaire (Timers, AK47)</li>
 *     <li>Gestion du gel temporel (Time Freeze)</li>
 *     <li>Affichage du HUD (kills, inventaire, infos vagues)</li>
 * </ul>
 *
 * <p>La classe centralise et orchestre également tous les managers :
 * {@link WaveManager}, {@link ProjectileManager}, {@link PickupManager},
 * {@link ItemPickupManager}, {@link TimeFreezeManager}.
 *
 * <p>Elle gère aussi la logique d'input "one-shot" pour éviter les répétitions
 * de commandes (pause, Timer).
 *
 * @author VotreNom
 * @version 1.0
 */
public class Game {

    private enum ScreenState {
        MAIN_MENU,
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    private ScreenState screenState = ScreenState.MAIN_MENU;

    private final KeyboardInput keyboardInput;

    private double elapsedTime;

    private Player player;
    private Level level;
    private WaveManager waveManager;
    private ProjectileManager projectileManager;
    private PickupManager pickupManager;
    private ItemPickupManager itemPickupManager;
    private HUD hud;

    private Inventory inventory;
    private TimeFreezeManager timeFreezeManager;

    private int killCount = 0;

    private final Random random = new Random();

    private boolean pauseKeyWasDown = false;
    private boolean timerKeyWasDown = false;

    private double akFireCooldown = 0.0;
    private final ArrayList<Ak47Bullet> akBullets = new ArrayList<>();

    public Game(KeyboardInput keyboardInput) {
        this.keyboardInput = keyboardInput;
        this.elapsedTime = 0.0;
        // Lancer la musique de fond au démarrage du jeu (menu principal)
        SoundManager.playMusic("game-music-loop.mp3");
        initGame();
    }

    /**
     * Réinitialise totalement une partie :
     * <ul>
     *     <li>Réinitialise le niveau, le joueur et l'inventaire</li>
     *     <li>Remet les compteurs et timers à zéro</li>
     *     <li>Reconstruit tous les gestionnaires de gameplay</li>
     * </ul>
     */
        private void initGame() {

        elapsedTime = 0.0;
        killCount = 0;
        akFireCooldown = 0.0;
        akBullets.clear();

        level = new Level();

        double startX = Constants.WINDOW_WIDTH / 2.0 - 25;
        double startY = 200;

        inventory = new Inventory();
        timeFreezeManager = new TimeFreezeManager();

        player = new Player(startX, startY, keyboardInput, level);

        projectileManager = new ProjectileManager(level, player, timeFreezeManager);
        pickupManager = new PickupManager(player);
        waveManager = new WaveManager(level, player, projectileManager, timeFreezeManager);

        itemPickupManager = new ItemPickupManager(player, inventory);

        hud = new HUD();
    }

    /**
     * Détecte un appui unique (one-shot) sur P ou ESC pour mettre en pause
     * ou reprendre la partie.
     *
     * @return true si la touche a été pressée pour la première fois
     */
        private boolean isPauseTogglePressed() {
        boolean now = keyboardInput.isKeyDown(KeyEvent.VK_P)
                || keyboardInput.isKeyDown(KeyEvent.VK_ESCAPE);

        if (now && !pauseKeyWasDown) {
            pauseKeyWasDown = true;
            return true;
        }

        if (!now)
            pauseKeyWasDown = false;

        return false;
    }

    /**
     * Détecte un appui unique (one-shot) sur la touche T pour l’utilisation
     * d’un Timer dans l'inventaire.
     *
     * @return true si la touche T vient d'être pressée
     */
        private boolean isTimerKeyPressedOnce() {
        boolean now = keyboardInput.isKeyDown(KeyEvent.VK_T);

        if (now && !timerKeyWasDown) {
            timerKeyWasDown = true;
            return true;
        }

        if (!now)
            timerKeyWasDown = false;

        return false;
    }

    /**
     * Met à jour la logique du jeu en fonction de l’état actuel de l’écran.
     *
     * @param dt Temps écoulé depuis la dernière frame (delta time)
     */
    public void update(double dt) {
        switch (screenState) {
            case MAIN_MENU:
                updateMainMenu(dt);
                break;
            case RUNNING:
                updateRunning(dt);
                break;
            case PAUSED:
                updatePaused(dt);
                break;
            case GAME_OVER:
                updateGameOver(dt);
                break;
        }
    }

    /**
     * Met à jour la logique du menu principal :
     * <ul>
     *     <li>ENTER -> lancer une nouvelle partie</li>
     *     <li>ESC -> quitter le jeu</li>
     * </ul>
     *
     * @param dt delta time
     */
    private void updateMainMenu(double dt) {
        elapsedTime += dt;

        if (keyboardInput.isKeyDown(KeyEvent.VK_ENTER)) {
            SoundManager.stopMusic();
            initGame();
            screenState = ScreenState.RUNNING;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
    }

    /**
     * Logique principale de la partie :
     * <ul>
     *     <li>Détection du mode Pause</li>
     *     <li>Mise à jour du gel temporel</li>
     *     <li>Joueur, ennemis et vagues</li>
     *     <li>Projectiles ennemis et objets ramassables</li>
     *     <li>Gestion tir AK47</li>
     *     <li>Gestion des dégâts de mêlée</li>
     * </ul>
     *
     * @param dt delta time
     */
    private void updateRunning(double dt) {
        elapsedTime += dt;

        if (isPauseTogglePressed()) {
            screenState = ScreenState.PAUSED;
            return;
        }

        timeFreezeManager.update(dt);

        player.update(dt);

        if (player.isDead()) {
            // Arrêter la musique de fond puis jouer le son de mort suivi du son Game Over
            SoundManager.stopMusic();
            SoundManager.playSound("dead.wav");
            // Joue le son 'game-over' après un court délai pour laisser le 'dead' se jouer
            new Thread(() -> {
                try {
                    Thread.sleep(700);
                    SoundManager.playSound("game-over.mp3");
                } catch (InterruptedException ignored) { }
            }).start();
            screenState = ScreenState.GAME_OVER;
            return;
        }

        waveManager.update(dt);

        projectileManager.update(dt);

        pickupManager.update(dt, waveManager.getCurrentWaveNumber());

        itemPickupManager.update(dt, waveManager.getCurrentWaveNumber());

        handleTimerUsage();

        updateAk47Shooting(dt);

        handleMeleeDamage();
    }

    /**
     * Consomme un item Timer depuis l’inventaire lorsque la touche T est pressée.
     * Active alors 10 secondes de gel des ennemis via {@link TimeFreezeManager}.
     */
    private void handleTimerUsage() {
        if (!isTimerKeyPressedOnce())
            return;

        var consumed = inventory.consumeFirstOfType(ItemType.TIMER);
        if (consumed != null) {
            timeFreezeManager.activate(10.0);
        }
    }

    /**
     * Met à jour le tir automatique de l'AK47 :
     * <ul>
     *     <li>1 tir / seconde (cooldown interne)</li>
     *     <li>uniquement si le joueur possède l'arme</li>
     *     <li>tir maintenu via la barre d’espace</li>
     * </ul>
     *
     * @param dt delta time
     */
    private void updateAk47Shooting(double dt) {

        if (player.hasAk47()) {
            akFireCooldown -= dt;
            boolean firePressed = keyboardInput.isKeyDown(KeyEvent.VK_SPACE);

            if (firePressed && akFireCooldown <= 0.0) {
                akFireCooldown = 1.0; // 1 balle / seconde
                spawnAk47Bullet();
            }
        }

        // Update + collisions des balles AK47
        for (Ak47Bullet b : akBullets) {
            b.update(dt);

            if (!b.isAlive())
                continue;

            // Enemy01
            for (Enemy01 e : waveManager.getEnemies01()) {
                if (!e.isDead()
                        && b.intersects((int) e.getX(), (int) e.getY(), (int) e.getWidth(), (int) e.getHeight())) {
                    e.takeDamage(b.getDamage());
                    // Son quand le joueur touche l'ennemi
                    SoundManager.playSound("punch.mp3");
                    b.kill();
                    break;
                }
            }
            if (!b.isAlive())
                continue;

            // Enemy02
            for (Enemy02 e : waveManager.getEnemies02()) {
                if (!e.isDead()
                        && b.intersects((int) e.getX(), (int) e.getY(), (int) e.getWidth(), (int) e.getHeight())) {
                    e.takeDamage(b.getDamage());
                    // Son quand le joueur touche l'ennemi
                    SoundManager.playSound("punch.mp3");
                    b.kill();
                    break;
                }
            }
            if (!b.isAlive())
                continue;

            // Enemy03
            for (Enemy03 e : waveManager.getEnemies03()) {
                if (!e.isDead()
                        && b.intersects((int) e.getX(), (int) e.getY(), (int) e.getWidth(), (int) e.getHeight())) {
                    e.takeDamage(b.getDamage());
                    // Son quand le joueur touche l'ennemi
                    SoundManager.playSound("punch.mp3");
                    b.kill();
                    break;
                }
            }
        }

        akBullets.removeIf(b -> !b.isAlive());
    }

    /**
     * Crée une balle d'AK47 à partir du centre du joueur, dans la direction
     * vers laquelle il fait face.
     */
    private void spawnAk47Bullet() {
        double px = player.getCenterX();
        double py = player.getCenterY();
        int dir = player.getFacingDirection();

        double speed = 600.0;

        // Une seule balle, alignée sur le centre
        akBullets.add(new Ak47Bullet(px, py, dir * speed, 0.0, 2)); // dégâts = 2
    }

    /**
     * Gère les dégâts de mêlée infligés par le joueur.
     * La logique est automatiquement désactivée si le joueur possède l'AK47.
     *
     * <p>Détecte la première collision entre l'attaque du joueur et un ennemi,
     * applique les dégâts, gère le killCount et les probabilités de bonus.
     */
    private void handleMeleeDamage() {

        if (!player.isAttacking() || player.hasHitThisAttack()) {
            return;
        }

        ArrayList<Enemy01> enemies01 = waveManager.getEnemies01();
        for (Enemy01 e : enemies01) {
            if (!e.isDead() && player.getRect().intersects(e.getRect())) {

                e.takeDamage(player.getDamage());
                player.markHitApplied();
                // Son quand le joueur frappe un ennemi
                SoundManager.playSound("punch.mp3");

                if (e.isDead()) {
                    killCount++;
                    if (random.nextDouble() < 0.15) {
                        pickupManager.spawnShield(e.getX(), e.getY(), 15.0);
                    }
                }
                return; // un seul ennemi touché
            }
        }

        if (!player.hasHitThisAttack()) {
            ArrayList<Enemy02> enemies02 = waveManager.getEnemies02();
            for (Enemy02 e : enemies02) {
                if (!e.isDead() && player.getRect().intersects(e.getRect())) {

                    e.takeDamage(player.getDamage());
                    player.markHitApplied();
                    // Son quand le joueur frappe un ennemi
                    SoundManager.playSound("punch.mp3");

                    if (e.isDead()) {
                        killCount++;
                        if (random.nextDouble() < 0.40) {
                            pickupManager.spawnSpeed(e.getX(), e.getY(), 15.0);
                        }
                    }
                    return;
                }
            }
        }

        if (!player.hasHitThisAttack()) {
            ArrayList<Enemy03> enemies03 = waveManager.getEnemies03();
            for (Enemy03 e : enemies03) {
                if (!e.isDead() && player.getRect().intersects(e.getRect())) {

                    e.takeDamage(player.getDamage());
                    player.markHitApplied();
                    // Son quand le joueur frappe un ennemi
                    SoundManager.playSound("punch.mp3");

                    if (e.isDead()) {
                        killCount++;
                    }
                    return;
                }
            }
        }
    }

    // =========================================================================
    // LOGIQUE : PAUSE
    // =========================================================================

    /**
     * Logique de l'écran Pause :
     * <ul>
     *     <li>P / ESC -> reprendre</li>
     *     <li>N -> vague suivante</li>
     *     <li>R -> recommencer</li>
     *     <li>M -> retour menu</li>
     * </ul>
     *
     * @param dt delta time
     */
    private void updatePaused(double dt) {

        elapsedTime += dt;

        if (isPauseTogglePressed()) {
            screenState = ScreenState.RUNNING;
            return;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_N)) {
            waveManager.goToNextWave();
            screenState = ScreenState.RUNNING;
            return;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_R)) {
            initGame();
            screenState = ScreenState.RUNNING;
            return;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_M)) {
            screenState = ScreenState.MAIN_MENU;
        }
    }

    // =========================================================================
    // LOGIQUE : GAME OVER
    // =========================================================================

    /**
     * Logique de l’écran Game Over :
     * <ul>
     *     <li>R -> recommencer une partie</li>
     *     <li>M -> retourner au menu principal</li>
     *     <li>ESC -> quitter le jeu</li>
     * </ul>
     *
     * @param dt delta time
     */
    private void updateGameOver(double dt) {

        elapsedTime += dt;

        if (keyboardInput.isKeyDown(KeyEvent.VK_R)) {
            initGame();
            screenState = ScreenState.RUNNING;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_M)) {
            screenState = ScreenState.MAIN_MENU;
        }

        if (keyboardInput.isKeyDown(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
    }

    // =========================================================================
    // RENDER GLOBAL
    // =========================================================================

    /**
     * Dessine à l'écran l’état actuel du jeu.
     *
     * @param g Contexte graphique 2D
     */
    public void render(Graphics2D g) {

        g.setColor(new Color(90, 90, 90));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        switch (screenState) {
            case MAIN_MENU:
                renderMainMenu(g);
                break;
            case RUNNING:
                renderRunning(g);
                break;
            case PAUSED:
                renderRunning(g);
                renderPauseOverlay(g);
                break;
            case GAME_OVER:
                renderRunning(g);
                renderGameOverOverlay(g);
                break;
        }
    }

    private void renderMainMenu(Graphics2D g) {

        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 48));
        g.drawString("SYNTAX ERROR 2D",
                Constants.WINDOW_WIDTH / 2 - 260,
                Constants.WINDOW_HEIGHT / 2 - 100);

        g.setFont(new Font("Consolas", Font.PLAIN, 20));
        g.drawString("Prototype Vagues 1, 2 & 3",
                Constants.WINDOW_WIDTH / 2 - 120,
                Constants.WINDOW_HEIGHT / 2 - 60);

        g.setFont(new Font("Consolas", Font.PLAIN, 22));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("ENTER : Jouer",
                Constants.WINDOW_WIDTH / 2 - 80,
                Constants.WINDOW_HEIGHT / 2 + 10);
        g.drawString("ESC   : Quitter",
                Constants.WINDOW_WIDTH / 2 - 85,
                Constants.WINDOW_HEIGHT / 2 + 45);
    }

    private void renderRunning(Graphics2D g) {

        g.setColor(new Color(90, 90, 90));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Titre + vague actuelle
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 24));
        g.drawString("SYNTAX ERROR 2D – Vague " + waveManager.getCurrentWaveNumber(), 40, 60);

        // Temps total
        int sec = (int) elapsedTime;
        int minutes = sec / 60;
        int seconds = sec % 60;

        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        g.drawString(String.format("Temps total : %02d:%02d", minutes, seconds), 40, 100);

        // Temps restant vague
        double remain = waveManager.getRemainingTime();
        int rSec = (int) remain;
        int rMin = rSec / 60;
        int rS = rSec % 60;

        g.drawString(
                String.format("Vague %d – Temps restant : %02d:%02d",
                        waveManager.getCurrentWaveNumber(), rMin, rS),
                40, 130);

        // Monde & entités
        level.render(g);
        waveManager.render(g);
        projectileManager.render(g);
        pickupManager.render(g);
        itemPickupManager.render(g);

        for (Ak47Bullet b : akBullets) {
            b.render(g);
        }

        player.render(g);

        // HUD complet (kills + inventaire texte)
        hud.render(g, killCount, inventory, player.hasAk47());
    }

    private void renderPauseOverlay(Graphics2D g) {

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Consolas", Font.BOLD, 48));
        g.drawString("PAUSE",
                Constants.WINDOW_WIDTH / 2 - 80,
                Constants.WINDOW_HEIGHT / 2 - 60);

        g.setFont(new Font("Consolas", Font.PLAIN, 22));
        g.setColor(Color.WHITE);
        g.drawString("P / ESC : Reprendre",
                Constants.WINDOW_WIDTH / 2 - 130,
                Constants.WINDOW_HEIGHT / 2);
        g.drawString("N       : Passer à la vague suivante",
                Constants.WINDOW_WIDTH / 2 - 210,
                Constants.WINDOW_HEIGHT / 2 + 35);
        g.drawString("R       : Recommencer",
                Constants.WINDOW_WIDTH / 2 - 130,
                Constants.WINDOW_HEIGHT / 2 + 70);
        g.drawString("M       : Retour menu principal",
                Constants.WINDOW_WIDTH / 2 - 180,
                Constants.WINDOW_HEIGHT / 2 + 105);
    }

    private void renderGameOverOverlay(Graphics2D g) {

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Consolas", Font.BOLD, 64));
        g.drawString("GAME OVER",
                Constants.WINDOW_WIDTH / 2 - 180,
                Constants.WINDOW_HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 22));
        g.drawString("R : Recommencer",
                Constants.WINDOW_WIDTH / 2 - 110,
                Constants.WINDOW_HEIGHT / 2 + 10);
        g.drawString("M : Retour menu principal",
                Constants.WINDOW_WIDTH / 2 - 170,
                Constants.WINDOW_HEIGHT / 2 + 45);
        g.drawString("ESC : Quitter",
                Constants.WINDOW_WIDTH / 2 - 90,
                Constants.WINDOW_HEIGHT / 2 + 80);
    }
}
