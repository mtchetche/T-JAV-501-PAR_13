package src.items;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import src.core.Constants;
import src.core.Inventory;
import src.core.ItemType;
import src.entities.Player;

/**
 * Gère tous les objets ramassables (ItemPickup) présents dans la partie.
 *
 * <p><b>Règles de spawn :</b></p>
 * <ul>
 *   <li>TIMER apparaît une fois en vague 2 et une fois en vague 3 (deux fois maximum par run).</li>
 *   <li>AK47 apparaît une seule fois, en vague 3.</li>
 * </ul>
 *
 * <p><b>Règles de durée de vie :</b></p>
 * <ul>
 *   <li>TIMER disparaît après 20 secondes et clignote pendant les 5 dernières secondes.</li>
 *   <li>AK47 ne disparaît jamais.</li>
 * </ul>
 *
 * <p><b>Ramassage :</b></p>
 * <ul>
 *   <li>TIMER → ajouté dans l'inventaire si une case est disponible.</li>
 *   <li>AK47  → donne définitivement l'arme au joueur.</li>
 * </ul>
 */
public class ItemPickupManager {

    private final ArrayList<ItemPickup> items = new ArrayList<>();
    private final Player player;
    private final Inventory inventory;
    private final Random random = new Random();

    private boolean timerSpawnedWave2 = false;
    private boolean timerSpawnedWave3 = false;
    private boolean ak47Spawned = false;

    private int lastWaveIndex = 0;

    /**
     * Constructeur.
     *
     * @param player     Joueur utilisé pour détecter les collisions avec les items
     * @param inventory  Inventaire où sont stockés les TIMER ramassés
     */
    public ItemPickupManager(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    /**
     * Met à jour tous les items existants : durée de vie, disparition,
     * collisions avec le joueur et gestion des changements de vague.
     *
     * @param dt                Temps écoulé depuis la dernière frame (en secondes)
     * @param currentWaveIndex  Numéro de la vague en cours
     */
    public void update(double dt, int currentWaveIndex) {

        // Détection de changement de vague
        if (currentWaveIndex != lastWaveIndex) {
            onWaveChanged(currentWaveIndex);
            lastWaveIndex = currentWaveIndex;
        }

        // Update des items
        for (ItemPickup item : items) {
            item.update(dt);

            if (!item.isAlive()) continue;

            // Collision avec le joueur
            if (player.getRect().intersects(item.getBounds())) {
                if (item.getType() == ItemType.TIMER) {
                    boolean added = inventory.addItem(ItemType.TIMER);
                    if (added) {
                        item.kill();
                    }
                }
                else if (item.getType() == ItemType.AK47) {
                    player.giveAk47();
                    item.kill();
                }
            }
        }

        items.removeIf(i -> !i.isAlive());
    }

    /**
     * Appelé automatiquement lorsqu'une vague change.
     * Déclenche le spawn des objets selon les règles du jeu.
     *
     * @param waveIndex Nouvelle vague active
     */
    private void onWaveChanged(int waveIndex) {
        if (waveIndex == 2 && !timerSpawnedWave2) {
            spawnTimerItem();
            timerSpawnedWave2 = true;
        }

        if (waveIndex == 3) {
            if (!timerSpawnedWave3) {
                spawnTimerItem();
                timerSpawnedWave3 = true;
            }
            if (!ak47Spawned) {
                spawnAk47Item();
                ak47Spawned = true;
            }
        }
    }

    /**
     * Fait apparaître un item TIMER à une position pseudo-aléatoire sur la map.
     */
    private void spawnTimerItem() {
        double x = 100 + random.nextDouble() * (Constants.WINDOW_WIDTH - 200);
        double y = 150 + random.nextDouble() * (Constants.WINDOW_HEIGHT - 300);
        items.add(new ItemPickup(x, y, ItemType.TIMER));
    }

    /**
     * Fait apparaître un item AK47 à une position pseudo-aléatoire sur la map.
     */
    private void spawnAk47Item() {
        double x = 100 + random.nextDouble() * (Constants.WINDOW_WIDTH - 200);
        double y = 150 + random.nextDouble() * (Constants.WINDOW_HEIGHT - 300);
        items.add(new ItemPickup(x, y, ItemType.AK47));
    }

    /**
     * Dessine tous les items encore visibles sur la scène.
     *
     * @param g Contexte graphique utilisé pour le rendu
     */
    public void render(Graphics2D g) {
        for (ItemPickup item : items) {
            item.render(g);
        }
    }
}
