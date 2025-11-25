package src.entities;

import src.world.Level;
import src.world.Platform;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Classe abstraite représentant un ennemi générique.
 *
 * <p>Elle gère :</p>
 * <ul>
 *     <li>la physique de base (gravité, collisions horizontales et verticales) ;</li>
 *     <li>la détection du joueur ;</li>
 *     <li>l’orientation (regarder vers le joueur) ;</li>
 *     <li>le déplacement horizontal ;</li>
 *     <li>la gestion des états via {@link EnemyState} ;</li>
 *     <li>le rendu simple (ovale rouge + barre de vie).</li>
 * </ul>
 *
 * <p>Chaque sous-classe doit implémenter la logique IA via
 * {@link #updateAI(double)}.</p>
 */
public abstract class Enemy extends LivingEntity {
    // Cooldown d'attaque pour éviter le spam
    protected double attackCooldown = 0.0;
    protected static final double ATTACK_DELAY = 0.7; // secondes

    // Pour éviter la superposition d'ennemis
    protected static final double MIN_DIST_BETWEEN_ENEMIES = 40.0;

    /**
     * Référence vers le niveau permettant l'accès aux plateformes
     * pour la gestion des collisions.
     */
    protected Level level;
    /** Vitesse horizontale désirée (utilisée pour interpolation / lissage). */
    protected double desiredVx = 0.0;

    /**
     * Joueur ciblé par l’ennemi. Sert à orienter l'ennemi et à calculer
     * les distances de détection.
     */
    protected Player target;

    /**
     * État actuel de l’ennemi (idle, attack, chase...).
     */
    protected EnemyState state = EnemyState.IDLE;

    /**
     * Direction actuelle de l’ennemi :
     * <ul>
     *     <li>{@code -1} = vers la gauche</li>
     *     <li>{@code +1} = vers la droite</li>
     * </ul>
     */
    protected int direction = 1;

    /**
     * Distance maximale à laquelle l’ennemi détecte le joueur.
     */
    protected double detectionRange = 0;

    /**
     * Intensité de la gravité appliquée à tous les ennemis.
     */
    protected static final double GRAVITY = 60.0;

    /**
     * Indique si l’ennemi touche le sol (utile pour des sauts éventuels).
     */
    protected boolean onGround = false;

    /**
     * Constructeur générique d'un ennemi.
     *
     * @param x position X initiale
     * @param y position Y initiale
     * @param maxHealth points de vie maximum
     * @param damage dégâts causés par l’ennemi au joueur
     * @param moveSpeed vitesse de déplacement
     * @param level niveau contenant le décor et les plateformes
     * @param target joueur visé
     */
    public Enemy(double x, double y,
                 int maxHealth, int damage,
                 double moveSpeed,
                 Level level,
                 Player target) {

        super(x, y, 50, 50, maxHealth, damage, moveSpeed, 0);
        this.level = level;
        this.target = target;
        // Register this enemy in the level so global entity lists are kept in sync.
        if (this.level != null) this.level.addEntity(this);
    }

    /**
     * Calcule la distance linéaire entre l’ennemi et le joueur.
     *
     * @return distance en pixels
     */
    protected double distanceToPlayer() {
        double dx = target.getCenterX() - getCenterX();
        double dy = target.getCenterY() - getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Vérifie si le joueur se trouve dans la portée de détection.
     *
     * @return {@code true} si le joueur est détecté
     */
    protected boolean isPlayerInRange() {
        return distanceToPlayer() <= detectionRange;
    }

    /**
     * Oriente l’ennemi vers le joueur en ajustant la direction
     * selon la position relative du joueur.
     */
    protected void lookAtPlayer() {
        if (target.getCenterX() < this.getCenterX()) {
            direction = -1;
        } else {
            direction = 1;
        }
    }

    /**
     * Déplace l’ennemi horizontalement en direction du joueur.
     *
     * @param dt delta time
     */
    protected void moveTowardsPlayer(double dt) {
        // Set desired horizontal velocity; actual vx will be interpolated in update()
        desiredVx = direction * moveSpeed * 60;
    }

    /**
     * Applique la gravité verticale.
     *
     * @param dt delta time
     */
    protected void applyGravity(double dt) {
        vy += GRAVITY * 60 * dt;
    }

    /**
     * Résout les collisions horizontales avec les plateformes
     * (s’arrête contre un mur).
     */
    private void resolveHorizontalCollision() {
        for (Platform p : level.getPlatforms()) {
            if (p.intersects(x, y, width, height)) {

                if (vx < 0) {
                    x = p.x + p.width;
                } else if (vx > 0) {
                    x = p.x - width;
                }
                vx = 0;
            }
        }
    }

    /**
     * Résout les collisions verticales (sol, plafond)
     * et met à jour l’état {@link #onGround}.
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
     * Met à jour l'ennemi :
     * <ul>
     *     <li>IA spécifique (implémentée dans les sous-classes) ;</li>
     *     <li>gravité ;</li>
     *     <li>déplacements ;</li>
     *     <li>collisions.</li>
     * </ul>
     *
     * @param dt delta time
     */
    @Override
    public void update(double dt) {

        // Mise à jour du cooldown d'attaque
        if (attackCooldown > 0) attackCooldown -= dt;

        // IA spécifique
        updateAI(dt);

        // Fluidification du mouvement (interpolation vers la vitesse désirée)
        double targetVx = desiredVx;
        vx += (targetVx - vx) * 0.22; // interpolation douce

        // Limiter la vitesse horizontale pour éviter des valeurs extrêmes
        double maxHoriz = Math.max(Math.abs(moveSpeed * 60), Math.abs(desiredVx));
        if (Math.abs(vx) > maxHoriz * 2.5) {
            vx = Math.signum(vx) * maxHoriz * 2.5;
        }

        applyGravity(dt);

        // Correction de la superposition d'ennemis
        avoidOverlapWithOtherEnemies();

        x += vx * dt;
        resolveHorizontalCollision();

        y += vy * dt;
        resolveVerticalCollision();
    }

    /**
     * Empêche les ennemis de se superposer en les repoussant légèrement.
     */
    protected void avoidOverlapWithOtherEnemies() {
        if (level == null) return;
        for (LivingEntity e : level.getEntities()) {
            if (e != this && e instanceof Enemy) {
                double dx = e.getCenterX() - getCenterX();
                double dy = e.getCenterY() - getCenterY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < MIN_DIST_BETWEEN_ENEMIES && dist > 0.1) {
                    // Apply a slight velocity nudging instead of hard position shifts to be smoother
                    double push = (MIN_DIST_BETWEEN_ENEMIES - dist) * 0.04;
                    // nudge both horizontal and vertical velocities slightly away from the other enemy
                    vx -= push * dx / dist;
                    vy -= push * dy / dist;
                }
            }
        }
    }

    /**
     * IA spécifique de chaque type d’ennemi.
     *
     * @param dt delta time
     */
    protected abstract void updateAI(double dt);

    /**
     * Rendu de base : un cercle rouge représentant l’ennemi
     * ainsi que sa barre de vie.
     *
     * @param g contexte graphique
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillOval((int) x, (int) y, (int) width, (int) height);

        renderHealthBar(g);
    }
}
