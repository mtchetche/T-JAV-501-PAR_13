package src.entities;

import src.core.Constants;
import src.input.KeyboardInput;
import src.world.Level;
import src.world.Platform;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Représente le joueur principal du jeu.
 *
 * <p>Fonctionnalités :</p>
 * <ul>
 *     <li>Déplacement horizontal avec accélération et direction</li>
 *     <li>Saut + gestion de la gravité</li>
 *     <li>Collisions avec les plateformes</li>
 *     <li>Attaque de mêlée (désactivée si AK47 obtenue)</li>
 *     <li>Effets temporaires de bonus (bouclier / vitesse)</li>
 *     <li>Affichage des sprites (idle, run, jump)</li>
 *     <li>Orientation horizontale via flip du sprite</li>
 * </ul>
 */
public class Player extends LivingEntity {

    /** Entrées clavier utilisées pour contrôler le joueur. */
    private final KeyboardInput input;

    /** Référence au niveau contenant les plateformes pour les collisions. */
    private final Level level;

    /** Constante de gravité appliquée verticalement. */
    private static final double GRAVITY = 60.0;

    /** Indique si le joueur repose actuellement sur une plateforme. */
    private boolean onGround = false;

    // ---------------------------
    // Sprites du joueur
    // ---------------------------

    /** Sprite affiché lorsque le joueur est immobile. */
    private BufferedImage idleSprite;

    /** Sprite affiché lorsque le joueur saute. */
    private BufferedImage jumpSprite;

    /** Animations de marche (cycle). */
    private BufferedImage[] runSprites;

    /** Direction horizontale (-1 = gauche, +1 = droite). */
    private int facingDirection = 1;

    /** Index actuel de l’animation de marche. */
    private int animationIndex = 0;

    /** Timer contrôlant la vitesse de changement de sprite. */
    private double animationTimer = 0.0;

    // ---------------------------
    // Attaque de mêlée
    // ---------------------------

    /** True si une attaque de mêlée est en cours. */
    private boolean isAttacking = false;

    /** Durée totale d'une attaque de mêlée. */
    private final double attackDuration = 0.25;

    /** Temps écoulé depuis le début de l’attaque. */
    private double attackTimer = 0.0;

    /** True si le coup a déjà infligé des dégâts pendant cette attaque. */
    private boolean hitApplied = false;

    // ---------------------------
    // Bonus temporaires
    // ---------------------------

    /** Temps restant du bouclier. */
    private double shieldTime = 0.0;

    /** Durée max du bouclier (pour afficher la barre). */
    private double shieldMaxTime = 0.0;

    /** Temps restant du boost de vitesse. */
    private double speedBoostTime = 0.0;

    /** Durée max du speed boost. */
    private double speedBoostMaxTime = 0.0;

    // ---------------------------
    // Arme AK47
    // ---------------------------

    /** True si le joueur a obtenu l’AK47 (désactive la mêlée). */
    private boolean hasAk47 = false;

    /**
     * Crée un joueur avec position, gestion d’input et référence au niveau.
     *
     * @param x      position initiale X
     * @param y      position initiale Y
     * @param input  gestionnaire des entrées clavier
     * @param level  niveau contenant les plateformes
     */
    public Player(double x, double y, KeyboardInput input, Level level) {
        super(x, y, 50, 80, 5, 1, 8.0, -19.0);
        this.input = input;
        this.level = level;
        loadSprites();
    }

    /**
     * Charge les sprites du joueur (idle, jump, marche).
     */
    private void loadSprites() {
        idleSprite = loadSprite("assets/player/ranya-arret.png");
        jumpSprite = loadSprite("assets/player/ranya-jump1.png");

        runSprites = new BufferedImage[] {
                loadSprite("assets/player/ranya-marche1.png"),
                loadSprite("assets/player/ranya-marche2.png"),
                loadSprite("assets/player/ranya-marche3.png"),
                loadSprite("assets/player/ranya-marche4.png")
        };
    }

    /**
     * Charge un sprite depuis les ressources du projet.
     *
     * @param path chemin du fichier image
     * @return BufferedImage chargée ou null en cas d’erreur
     */
    private BufferedImage loadSprite(String path) {
        try {
            InputStream is = Player.class.getClassLoader().getResourceAsStream(path);
            if (is != null) return ImageIO.read(is);
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Erreur sprite : " + path);
            return null;
        }
    }

    /**
     * Gère les entrées clavier :
     * <ul>
     *     <li>déplacement gauche/droite</li>
     *     <li>saut si au sol</li>
     *     <li>attaque melee si disponible</li>
     * </ul>
     */
    private void handleInput() {

        double speedFactor = (speedBoostTime > 0) ? 1.7 : 1.0;
        double currentSpeed = moveSpeed * speedFactor;

        if (input.isKeyDown(KeyEvent.VK_LEFT) || input.isKeyDown(KeyEvent.VK_Q)) {
            vx = -currentSpeed * 60;
            facingDirection = -1;
        }
        else if (input.isKeyDown(KeyEvent.VK_RIGHT) || input.isKeyDown(KeyEvent.VK_D)) {
            vx = currentSpeed * 60;
            facingDirection = 1;
        }
        else {
            vx = 0;
        }

        boolean jumpPressed = input.isKeyDown(KeyEvent.VK_UP) || input.isKeyDown(KeyEvent.VK_Z);
        if (jumpPressed && onGround) {
            vy = jumpForce * 60;
            onGround = false;
        }

        // Attaque melee seulement si PAS d'AK47
        if (!hasAk47 && input.isKeyDown(KeyEvent.VK_SPACE) && !isAttacking) {
            isAttacking = true;
            attackTimer = 0.0;
            hitApplied = false;
        }
    }

    /**
     * Met à jour tous les comportements du joueur :
     * <ul>
     *     <li>input</li>
     *     <li>attaque melee</li>
     *     <li>bonus</li>
     *     <li>animation</li>
     *     <li>physique (gravité)</li>
     *     <li>collisions</li>
     * </ul>
     *
     * @param dt delta time en secondes
     */
    @Override
    public void update(double dt) {

        handleInput();
        updateAttack(dt);
        updateBonuses(dt);
        updateAnimation(dt);

        vy += GRAVITY * 60 * dt;

        x += vx * dt;
        resolveHorizontalCollision();

        y += vy * dt;
        resolveVerticalCollision();

        clampToScreen();
    }

    /**
     * Met à jour l’attaque melee (timer + fin d’attaque).
     *
     * @param dt delta time
     */
    private void updateAttack(double dt) {
        if (!isAttacking) return;

        attackTimer += dt;
        if (attackTimer >= attackDuration) {
            isAttacking = false;
            hitApplied = false;
        }
    }

    /**
     * Décrémente les timers des bonus (bouclier / vitesse).
     *
     * @param dt delta time
     */
    private void updateBonuses(double dt) {

        if (shieldTime > 0) {
            shieldTime -= dt;
            if (shieldTime < 0) shieldTime = 0;
        }

        if (speedBoostTime > 0) {
            speedBoostTime -= dt;
            if (speedBoostTime < 0) speedBoostTime = 0;
        }
    }

    /**
     * Met à jour l’animation du joueur selon son mouvement.
     *
     * @param dt delta time
     */
    private void updateAnimation(double dt) {

        boolean isMoving = Math.abs(vx) > 0.1;
        boolean isJumping = !onGround;

        if (isJumping || !isMoving) {
            animationIndex = 0;
            animationTimer = 0;
            return;
        }

        animationTimer += dt;
        if (animationTimer > 0.10) {
            animationTimer = 0;
            if (runSprites != null && runSprites.length > 0) {
                animationIndex = (animationIndex + 1) % runSprites.length;
            }
        }
    }

    /**
     * Résout les collisions horizontales avec les plateformes.
     */
    private void resolveHorizontalCollision() {
        for (Platform p : level.getPlatforms()) {
            if (p.intersects(x, y, width, height)) {
                if (vx > 0) x = p.x - width;
                else if (vx < 0) x = p.x + p.width;
                vx = 0;
            }
        }
    }

    /**
     * Résout les collisions verticales avec le sol ou les plateformes.
     */
    private void resolveVerticalCollision() {
        onGround = false;
        for (Platform p : level.getPlatforms()) {
            if (p.intersects(x, y, width, height)) {
                if (vy > 0) {
                    y = p.y - height;
                    vy = 0;
                    onGround = true;
                } else if (vy < 0) {
                    y = p.y + p.height;
                    vy = 0;
                }
            }
        }
    }

    /**
     * Empêche le joueur de sortir de la fenêtre du jeu.
     */
    private void clampToScreen() {
        if (x < 0) x = 0;
        if (x + width > Constants.WINDOW_WIDTH) x = Constants.WINDOW_WIDTH - width;

        if (y < 0) {
            y = 0;
            vy = 0;
        }
        if (y + height > Constants.WINDOW_HEIGHT) {
            y = Constants.WINDOW_HEIGHT - height;
            vy = 0;
            onGround = true;
        }
    }

    /**
     * Dessine le joueur + barres de vie / bouclier / vitesse.
     *
     * @param g contexte graphique
     */
    @Override
    public void render(Graphics2D g) {

        BufferedImage sprite = getCurrentSprite();

        if (sprite != null) {
            if (facingDirection == -1) sprite = flipImageHorizontally(sprite);
            g.drawImage(sprite, (int)x, (int)y, (int)width, (int)height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect((int)x, (int)y, (int)width, (int)height);
        }

        renderHealthBar(g);

        int barX = (int)x;
        int baseY = (int)(y - 10);

        if (shieldTime > 0 && shieldMaxTime > 0) {
            double ratio = shieldTime / shieldMaxTime;
            g.setColor(new Color(0, 120, 255));
            g.fillRect(barX, baseY - 6, (int)(width * ratio), 4);
        }

        if (speedBoostTime > 0 && speedBoostMaxTime > 0) {
            double ratio = speedBoostTime / speedBoostMaxTime;
            int offset = (shieldTime > 0) ? 12 : 6;
            g.setColor(new Color(255, 105, 180));
            g.fillRect(barX, baseY - offset, (int)(width * ratio), 4);
        }
    }

    /**
     * Retourne le sprite correspondant à l'état actuel du joueur.
     *
     * @return sprite à afficher
     */
    private BufferedImage getCurrentSprite() {

        boolean isMoving = Math.abs(vx) > 0.1;
        boolean isJumping = !onGround;

        if (isJumping && jumpSprite != null) return jumpSprite;
        if (isMoving && runSprites != null && runSprites.length > 0) {
            return runSprites[animationIndex];
        }
        return idleSprite;
    }

    /**
     * Retourne une copie horizontale inversée de l’image (flip).
     *
     * @param img image à retourner
     * @return image retournée horizontalement
     */
    private BufferedImage flipImageHorizontally(BufferedImage img) {
        if (img == null) return null;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        AffineTransformOp op =
                new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

    /**
     * Redéfinition : le bouclier empêche de perdre des PV.
     *
     * @param dmg dégâts reçus
     */
    @Override
    public void takeDamage(int dmg) {
        if (shieldTime > 0) return;
        super.takeDamage(dmg);
    }

    /**
     * Ajoute un bouclier d'une durée donnée.
     *
     * @param duration durée en secondes
     */
    public void addShield(double duration) {
        if (duration <= 0) return;
        shieldTime = duration;
        shieldMaxTime = duration;
    }

    /**
     * Ajoute un bonus de vitesse d'une durée donnée.
     *
     * @param duration durée en secondes
     */
    public void addSpeedBoost(double duration) {
        if (duration <= 0) return;
        speedBoostTime = duration;
        speedBoostMaxTime = duration;
    }

    // -------------------------------------------------------------------------
    //   AK47
    // -------------------------------------------------------------------------

    /**
     * Donne définitivement l'AK47 au joueur.
     * Désactive immédiatement l'attaque de mêlée.
     */
    public void giveAk47() {
        this.hasAk47 = true;
        this.isAttacking = false;
        this.hitApplied = false;
    }

    /**
     * @return true si le joueur possède l’AK47
     */
    public boolean hasAk47() {
        return hasAk47;
    }

    // -------------------------------------------------------------------------
    //   Infos diverses
    // -------------------------------------------------------------------------

    /**
     * @return true si une attaque melee est en cours
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * @return true si cette attaque a déjà infligé des dégâts
     */
    public boolean hasHitThisAttack() {
        return hitApplied;
    }

    /**
     * Marque que l’attaque actuelle a appliqué son coup.
     */
    public void markHitApplied() {
        this.hitApplied = true;
    }

    /**
     * @return -1 si le joueur regarde à gauche, +1 à droite
     */
    public int getFacingDirection() {
        return facingDirection;
    }

    /**
     * @return coordonnée X du centre du joueur
     */
    public double getCenterX() {
        return x + width / 2.0;
    }

    /**
     * @return coordonnée Y du centre du joueur
     */
    public double getCenterY() {
        return y + height / 2.0;
    }
}
