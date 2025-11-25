package src.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import src.world.Level;

import java.io.File;
import javax.imageio.ImageIO;

import src.world.Platform;

/**
 * Enemy01 est un type d’ennemi basique mais agile,
 * capable de :
 * <ul>
 *     <li>marcher vers le joueur,</li>
 *     <li>charger après un court délai,</li>
 *     <li>effectuer des sauts "intelligents" selon l’environnement,</li>
 *     <li>rendre différentes animations (idle, marche, saut).</li>
 * </ul>
 *
 * <p>Il utilise une IA simple basée sur quatre états :</p>
 * <ul>
 *     <li>{@code IDLE} : déplacement tranquille vers le joueur ;</li>
 *     <li>{@code CHARGING} : immobilisation avant la charge ;</li>
 *     <li>{@code CHASING} : charge agressive à grande vitesse ;</li>
 *     <li>{@code COOLDOWN} : courte phase après une attaque.</li>
 * </ul>
 *
 * <p>L’ennemi est également capable de détecter les obstacles
 * devant lui et d’adapter ses sauts en conséquence.</p>
 */
public class Enemy01 extends Enemy {

    /* ----------------------------
     *         SPRITES
     * ---------------------------- */

    /** Sprite affiché lorsque l'ennemi est immobile. */
    private BufferedImage idleSprite;

    /** Sprite affiché lorsque l’ennemi saute. */
    private BufferedImage jumpSprite;

    /** Sprites pour l’animation de marche. */
    private BufferedImage run1Sprite, run2Sprite, run3Sprite;

    /** Tableau des sprites de course, utilisé par l'animation. */
    private BufferedImage[] runSprites;

    /* ----------------------------
     *         IA & MOUVEMENT
     * ---------------------------- */

    /** Portée à partir de laquelle l'ennemi déclenche la phase de charge. */
    private double chargeRange = 200;

    /** Accumulateur temporel utilisé pendant la phase CHARGING. */
    private double chargeTimer = 0;

    /** Durée totale de la phase CHARGING avant de foncer. */
    private final double chargeDuration = 1.5;

    /** Vitesse horizontale pendant la charge (supérieure à moveSpeed). */
    private double chargeSpeed;

    /** Temps avant qu’un nouveau saut soit autorisé. */
    private double jumpCooldown = 0;

    /** Direction actuelle du sprite (1 = droite, -1 = gauche). */
    private int facingDirection = 1;

    /** Index actuel du sprite dans l’animation de marche. */
    private int animationIndex = 0;

    /** Timer qui détermine le passage au sprite suivant. */
    private double animationTimer = 0;

    /**
     * Constructeur d’un ennemi Enemy01.
     *
     * @param x      position X initiale.
     * @param y      position Y initiale.
     * @param level  niveau contenant plateformes et entités.
     * @param target joueur poursuivi par l’ennemi.
     */
    public Enemy01(double x, double y, Level level, Player target) {
        super(x, y, 3, 1, 4.0, level, target);

        this.health = 3;
        this.maxHealth = 3;

        this.width = 60;
        this.height = 80;

        this.detectionRange = chargeRange;

        this.chargeSpeed = moveSpeed * 2;

        this.jumpForce = -15.0;

        loadSprites();
    }

    /**
     * Charge l’ensemble des sprites de l’ennemi (idle, marche, saut, attaque).
     */
    private void loadSprites() {
        idleSprite = loadSprite("assets/enemy01/fadel-arret.png");
        jumpSprite = loadSprite("assets/enemy01/fadel-jump.png");
        run1Sprite = loadSprite("assets/enemy01/fadel-marche1.png");
        run2Sprite = loadSprite("assets/enemy01/fadel-marche2.png");
        run3Sprite = loadSprite("assets/enemy01/fadel-marche3.png");
        loadSprite("assets/enemy01/fadel-attaque.png");

        runSprites = new BufferedImage[] { run1Sprite, run2Sprite, run3Sprite };
    }

    /**
     * Charge un sprite depuis un chemin d’accès (classpath puis fichier).
     *
     * @param path chemin relatif vers l’image.
     * @return l’image chargée, ou null en cas d’erreur.
     */
    private BufferedImage loadSprite(String path) {
        try {
            InputStream is = Enemy01.class.getClassLoader().getResourceAsStream(path);
            if (is != null) return ImageIO.read(is);
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Erreur sprite : " + path);
            return null;
        }
    }

    /**
     * Calcule quel sprite doit être utilisé :
     * <ul>
     *     <li>saut → sprite de saut</li>
     *     <li>marche → animation de course</li>
     *     <li>immobile → sprite idle</li>
     * </ul>
     *
     * @return le sprite approprié
     */
    private BufferedImage getCurrentSprite() {
        boolean isMoving = Math.abs(vx) > 0.1;
        boolean isJumping = !onGround;

        if (isJumping && jumpSprite != null)
            return jumpSprite;

        if (isMoving && runSprites != null && runSprites.length > 0) {
            BufferedImage r = runSprites[animationIndex];
            if (r != null) return r;
        }

        return idleSprite;
    }

    /**
     * Met à jour l’animation de marche (cycle entre les sprites).
     *
     * @param dt delta time
     */
    private void updateAnimation(double dt) {

        boolean isMoving = Math.abs(vx) > 0.1;
        boolean isJumping = !onGround;

        if (isJumping || !isMoving || runSprites == null || runSprites.length == 0) {
            animationIndex = 0;
            animationTimer = 0;
            return;
        }

        animationTimer += dt;
        if (animationTimer > 0.10) {
            animationTimer = 0;
            animationIndex = (animationIndex + 1) % runSprites.length;
        }
    }

    /**
     * IA principale de l’ennemi.
     *
     * @param dt temps écoulé depuis la dernière frame
     */
    @Override
    public void updateAI(double dt) {
        lookAtPlayer();
        facingDirection = direction;   // FIX: flip dépend de direction
        updateAnimation(dt);           // FIX: animation marche

        if (jumpCooldown > 0) jumpCooldown -= dt;

        switch (state) {

            case IDLE:
                moveTowardsPlayer(dt);
                attemptSmartJump();

                if (isPlayerInRange()) {
                    state = EnemyState.CHARGING;
                    chargeTimer = 0;
                    vx = 0;
                }
                break;

            case CHARGING:
                vx = 0;
                chargeTimer += dt;

                if (chargeTimer >= chargeDuration)
                    state = EnemyState.CHASING;

                break;

            case CHASING:
                vx = direction * chargeSpeed * 60;
                attemptSmartJump();

                if (touchesPlayer()) {
                    target.takeDamage(damage);
                    state = EnemyState.COOLDOWN;
                    vx = 0;
                }

                if (!isPlayerInRange())
                    state = EnemyState.IDLE;

                break;

            case COOLDOWN:
                vx = 0;
                state = EnemyState.IDLE;
                break;
            default:
                break;
        }
    }

    /**
     * Vérifie si l’ennemi touche le joueur via leurs hitbox respectives.
     *
     * @return {@code true} si collision.
     */
    private boolean touchesPlayer() {
        return getRect().intersects(target.getRect());
    }

    /**
     * Saut contextuel.
     */
    private void attemptSmartJump() {

        if (!onGround) return;
        if (jumpCooldown > 0) return;

        Platform obstacle = detectObstacleInFront();
        if (obstacle != null) {
            vy = jumpForce * 60;
            jumpCooldown = 0.7;
            return;
        }

        double dy = target.getY() - y;
        double dx = Math.abs(target.getX() - x);

        if (dy < -20 && dx < 120) {
            vy = jumpForce * 60;
            jumpCooldown = 0.7;
        }
    }

    /**
     * Détecte la présence d’une plateforme juste devant l’ennemi.
     *
     * @return une plateforme bloquante, ou {@code null}.
     */
    private Platform detectObstacleInFront() {

        for (Platform p : level.getPlatforms()) {

            boolean closeHeight =
                    (y + height) >= p.y - 10 &&
                            (y + height) <= p.y + p.height + 60;

            if (!closeHeight) continue;

            if (direction == 1) {
                if (x + width + 5 >= p.x && x + width + 5 <= p.x + 20)
                    return p;
            } else {
                if (x - 5 <= p.x + p.width && x - 5 >= p.x + p.width - 20)
                    return p;
            }
        }

        return null;
    }

    /**
     * Retourne une image miroir (horizontalement).
     *
     * @param img sprite à retourner
     * @return sprite inversé horizontalement
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
     * Rendu complet de l’ennemi.
     *
     * @param g contexte graphique
     */
    @Override
    public void render(Graphics2D g) {

        BufferedImage sprite = getCurrentSprite();

        if (sprite != null) {
            if (facingDirection == -1)
                sprite = flipImageHorizontally(sprite);

            g.drawImage(sprite, (int)x, (int)y, (int)width, (int)height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect((int)x, (int)y, (int)width, (int)height);
        }

        if (state == EnemyState.CHARGING)
            g.setColor(new Color(180, 0, 255));
        else
            g.setColor(new Color(240, 240, 0));

        renderHealthBar(g);
    }

    /**
     * @return la direction actuelle du sprite (1 = droite, -1 = gauche).
     */
    public int getFacingDirection() {
        return facingDirection;
    }
}
