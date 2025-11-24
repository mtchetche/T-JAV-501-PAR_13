package src.entities;

import src.core.ProjectileManager;
import src.world.Level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

/**
 * Enemy03 — Ennemi hybride combinant la charge et le tir à distance.
 */
public class Enemy03 extends Enemy {

    // -----------------------------
    // SPRITES (alexis)
    // -----------------------------
    private BufferedImage idleSprite;
    private BufferedImage jumpSprite;
    private BufferedImage shootSprite;
    private BufferedImage chargePrepSprite;
    private BufferedImage chargeSprite;
    private BufferedImage run1Sprite;
    private BufferedImage run2Sprite;

    private BufferedImage[] runSprites;

    private int animationIndex = 0;
    private double animationTimer = 0.0;

    // -----------------------------
    // Portées & timers
    // -----------------------------
    private double shootRange = 350;
    private double chargeRange = 150;

    private final double shootCooldownMax = 2.0;
    private double shootCooldown = 0.0;

    private final double chargePrepTime = 0.7;
    private double chargeTimer = 0.0;

    private boolean isChargingPrep = false;
    private boolean isCharging = false;

    private final ProjectileManager projectileManager;

    public Enemy03(double x, double y, Level level, Player target, ProjectileManager projectileManager) {
        super(x, y, 5, 3, 8.0, level, target);
        this.projectileManager = projectileManager;

        this.width = 30 * 2;
        this.height = 40 * 2;
        this.jumpForce = -21.0;

        loadSprites();
    }

    private void loadSprites() {
        idleSprite       = loadSprite("assets/enemy03/alexis-arret.png");
        jumpSprite       = loadSprite("assets/enemy03/alexis-jump.png");
        run1Sprite       = loadSprite("assets/enemy03/alexis-marche1.png");
        run2Sprite       = loadSprite("assets/enemy03/alexis-marche2.png");
        chargeSprite     = loadSprite("assets/enemy03/alexis-rapproché.png");
        shootSprite      = loadSprite("assets/enemy03/alexis-tir.png");

        runSprites = new BufferedImage[] { run1Sprite, run2Sprite };
        animationIndex = 0;
        animationTimer = 0.0;
    }

    /**
     * Loader robuste identique à Enemy02.
     */
    private BufferedImage loadSprite(String path) {
        BufferedImage cp = tryLoadFromClasspath(path);
        if (cp != null) return cp;

        BufferedImage fs = tryLoadFromFile(path);
        if (fs != null) return fs;

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
            ClassLoader cl = Enemy03.class.getClassLoader();
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

    private void updateAnimation(double dt) {
        if (isChargingPrep || isCharging) return;

        boolean isMoving = Math.abs(vx) > 0.1;
        boolean isJumping = !onGround;

        if (!isMoving || isJumping || runSprites == null || runSprites.length == 0) {
            animationIndex = 0;
            animationTimer = 0.0;
            return;
        }

        animationTimer += dt;
        if (animationTimer >= 0.10) {
            animationTimer = 0.0;
            animationIndex = (animationIndex + 1) % runSprites.length;
        }
    }

    private BufferedImage getCurrentSprite() {
        if (isChargingPrep) {
            if (chargePrepSprite != null) return chargePrepSprite;
            return idleSprite;
        }

        if (isCharging) {
            if (chargeSprite != null) return chargeSprite;
            if (runSprites != null && runSprites.length > 0 && runSprites[animationIndex] != null)
                return runSprites[animationIndex];
            return idleSprite;
        }

        if (!onGround)
            return (jumpSprite != null ? jumpSprite : idleSprite);

        if (shootCooldown > shootCooldownMax - 0.15 && shootSprite != null)
            return shootSprite;

        if (Math.abs(vx) > 1 && runSprites != null && runSprites.length > 0) {
            BufferedImage r = runSprites[animationIndex];
            if (r != null) return r;
        }

        return idleSprite;
    }

    private BufferedImage flipImageHorizontally(BufferedImage img) {
        if (img == null) return null;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

    @Override
    protected void updateAI(double dt) {
        lookAtPlayer();
        updateAnimation(dt);

        double distance = distanceToPlayer();
        if (shootCooldown > 0) shootCooldown -= dt;

        if (isChargingPrep) {
            handleChargePrep(dt);
            return;
        }

        if (isCharging) {
            handleChargeActive(dt);
            return;
        }

        if (distance <= chargeRange) {
            startChargePrep();
        } else if (distance <= shootRange) {
            handleShooting(dt);
        } else {
            chasePlayer(dt);
        }
    }

    private void startChargePrep() {
        isChargingPrep = true;
        chargeTimer = 0.0;
        vx = 0;
    }

    private void handleChargePrep(double dt) {
        chargeTimer += dt;
        if (chargeTimer >= chargePrepTime) {
            isChargingPrep = false;
            startCharge();
        }
    }

    private void startCharge() {
        isCharging = true;
        vx = direction * (moveSpeed * 2.0) * 60;
    }

    private void handleChargeActive(double dt) {
        double distance = distanceToPlayer();
        if (distance > chargeRange * 2) {
            isCharging = false;
            vx = 0;
        }
    }

    private void handleShooting(double dt) {
        vx = 0;
        if (onGround && target.getY() < this.y - 40) {
            vy = jumpForce * 60;
        }
        if (shootCooldown <= 0) {
            shoot();
            shootCooldown = shootCooldownMax;
        }
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

        double dirX = dx / len;
        double dirY = dy / len;

        projectileManager.addBullet(
                new Bullet(ex - 5, ey - 5, dirX * 450, dirY * 450, damage, level)
        );
    }

    private void chasePlayer(double dt) {
        vx = direction * moveSpeed * 60;
        if (onGround && target.getY() < this.y - 50) {
            vy = jumpForce * 60;
        }
    }

    @Override
    public void render(Graphics2D g) {
        BufferedImage sprite = getCurrentSprite();

        if (sprite != null) {
            if (direction == -1) sprite = flipImageHorizontally(sprite);
            g.drawImage(sprite, (int) x, (int) y, (int) width, (int) height, null);
        } else {
            if (isChargingPrep) g.setColor(new Color(150, 0, 255));
            else g.setColor(Color.RED);

            g.fillRect((int) x, (int) y, (int) width, (int) height);
            g.setColor(Color.BLACK);
            g.drawRect((int) x, (int) y, (int) width, (int) height);
        }

        renderHealthBar(g);
    }
}
