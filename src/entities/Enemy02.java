package src.entities;

import src.core.ProjectileManager;
import src.world.Level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * Ennemi à distance (Enemy02).
 *
 * <p>
 * Comportement :
 * <ul>
 *     <li>Chasse le joueur s'il est trop loin.</li>
 *     <li>Tire à distance lorsque le joueur est dans la portée de tir.</li>
 *     <li>Fuit si le joueur est trop proche.</li>
 *     <li>Peut sauter pour atteindre des plateformes plus hautes.</li>
 * </ul>
 *
 * <p>
 * Cet ennemi utilise plusieurs sprites (idle, marche, saut, tir) et possède
 * une animation simple pour la marche. Il peut tirer des projectiles
 * dirigés vers la position actuelle du joueur.
 */
public class Enemy02 extends Enemy {

    // Sprites (images)
    private BufferedImage idleSprite;
    private BufferedImage jumpSprite;
    private BufferedImage shootSprite;
    private BufferedImage run1Sprite;
    private BufferedImage run2Sprite;

    // Tableau pour l’animation de marche
    private BufferedImage[] runSprites;

    // Animation
    private int animationIndex = 0;
    private double animationTimer = 0;

    // Distances de comportement
    private double shootRange = 350;
    private double fleeRange = 150;

    // Tir
    private final double shootCooldownMax = 3.0;
    private double shootCooldown = 0.0;
    private final ProjectileManager projectileManager;

    /**
     * Crée un ennemi de type Enemy02.
     *
     * @param x position X initiale
     * @param y position Y initiale
     * @param level référence vers le niveau actuel
     * @param target joueur visé
     * @param projectileManager gestionnaire de projectiles pour les tirs
     */
    public Enemy02(double x, double y, Level level, Player target, ProjectileManager projectileManager) {
        super(x, y, 1, 2, 12.0, level, target);

        this.width = 30 * 2;
        this.height = 40 * 2;
        this.jumpForce = -21.0;

        this.projectileManager = projectileManager;

        loadSprites();
    }

    /**
     * Charge toutes les images du personnage et initialise l’animation de marche.
     */
    private void loadSprites() {
        idleSprite = loadSprite("assets/enemy02/hugo-arret.png");
        jumpSprite = loadSprite("assets/enemy02/hugo-jump.png");
        run1Sprite = loadSprite("assets/enemy02/hugo-marche01.png");
        run2Sprite = loadSprite("assets/enemy02/hugo-marche02.png");
        shootSprite = loadSprite("assets/enemy02/hugo-attaque.png");

        runSprites = new BufferedImage[] { run1Sprite, run2Sprite };
    }

    /**
     * Loader robuste :
     * - classpath (avec/sans /)
     * - fichiers relatifs au cwd
     * - remonte plusieurs parents si besoin (../)
     */
    private BufferedImage loadSprite(String path) {
        // 1) Classpath
        BufferedImage cp = tryLoadFromClasspath(path);
        if (cp != null) return cp;

        // 2) File system simple
        BufferedImage fs = tryLoadFromFile(path);
        if (fs != null) return fs;

        // 3) Remonter les dossiers parents (bin/, out/, build/, etc.)
        File base = new File(System.getProperty("user.dir"));
        for (int i = 0; i < 4; i++) {
            File candidate = new File(base, path);
            if (candidate.exists()) {
                try { return ImageIO.read(candidate); }
                catch (IOException ignored) {}
            }
            base = base.getParentFile();
            if (base == null) break;
        }

        System.err.println("ERREUR sprite introuvable : " + path +
                " | cwd=" + System.getProperty("user.dir"));
        return null;
    }

    private BufferedImage tryLoadFromClasspath(String path) {
        try {
            ClassLoader cl = Enemy02.class.getClassLoader();
            InputStream is = cl.getResourceAsStream(path);
            if (is == null) is = cl.getResourceAsStream("/" + path);
            if (is != null) return ImageIO.read(is);
        } catch (IOException ignored) {}
        return null;
    }

    private BufferedImage tryLoadFromFile(String path) {
        try {
            File f1 = new File(path);
            if (f1.exists()) return ImageIO.read(f1);

            File f2 = new File("./" + path);
            if (f2.exists()) return ImageIO.read(f2);
        } catch (IOException ignored) {}
        return null;
    }

    /**
     * Détermine le sprite à afficher selon l’état actuel de l’ennemi :
     * saut, tir, marche ou idle.
     */
    private BufferedImage getCurrentSprite() {
        if (!onGround && jumpSprite != null) return jumpSprite;
        if (state == EnemyState.SHOOTING && shootSprite != null) return shootSprite;

        if (Math.abs(vx) > 0.1 && runSprites != null && runSprites.length > 0) {
            BufferedImage r = runSprites[animationIndex];
            if (r != null) return r;
        }

        return idleSprite;
    }

    /**
     * Met à jour l’animation de marche.
     */
    private void updateAnimation(double dt) {
        if (Math.abs(vx) < 0.1 || !onGround || runSprites == null || runSprites.length == 0) return;

        animationTimer += dt;
        if (animationTimer >= 0.1) {
            animationTimer = 0;
            animationIndex = (animationIndex + 1) % runSprites.length;
        }
    }

    /**
     * Flip horizontal.
     */
    private BufferedImage flipImageHorizontally(BufferedImage img) {
        if (img == null) return null;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        return new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
                .filter(img, null);
    }

    /**
     * IA principale.
     */
    @Override
    protected void updateAI(double dt) {

        updateAnimation(dt);
        lookAtPlayer();

        if (shootCooldown > 0) shootCooldown -= dt;

        double dist = distanceToPlayer();

        if (dist <= fleeRange)       state = EnemyState.FLEEING;
        else if (dist <= shootRange) state = EnemyState.SHOOTING;
        else                         state = EnemyState.CHASING;

        switch (state) {

            case FLEEING:
                vx = -direction * (moveSpeed * 0.5) * 60;
                break;

            case SHOOTING:
                vx = 0;
                tryToReachHighGround();
                if (shootCooldown <= 0) {
                    shoot();
                    shootCooldown = shootCooldownMax;
                }
                break;

            case CHASING:
                moveTowardsPlayer(dt);
                if (onGround && target.getY() < this.y - 40) {
                    vy = jumpForce * 60;
                }
                break;
            default:
                break;
        }
    }

    private void tryToReachHighGround() {
        if (target.getY() < this.y - 40 && onGround)
            vy = jumpForce * 60;
    }

    private void shoot() {
        double ex = getCenterX();
        double ey = getCenterY();

        double px = target.getCenterX();
        double py = target.getCenterY();

        double dx = px - ex;
        double dy = py - ey;

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) len = 1;

        double vxB = (dx / len) * 400;
        double vyB = (dy / len) * 400;

        projectileManager.addBullet(new Bullet(ex - 5, ey - 5, vxB, vyB, damage, level));
    }

    @Override
    public void render(Graphics2D g) {

        BufferedImage sprite = getCurrentSprite();

        if (sprite != null) {

            if (direction == -1)
                sprite = flipImageHorizontally(sprite);

            g.drawImage(sprite, (int)x, (int)y, (int)width, (int)height, null);

        } else {
            g.setColor(Color.RED);
            g.fillRect((int)x, (int)y, (int)width, (int)height);
            g.setColor(Color.BLACK);
            g.drawRect((int)x, (int)y, (int)width, (int)height);
        }

        renderHealthBar(g);
    }
}
