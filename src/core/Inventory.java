package src.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'inventaire du joueur.
 *
 * <p>L'inventaire stocke une liste d'items sous forme de file (FIFO),
 * jusqu'à un nombre maximal défini. Il permet d'ajouter des items,
 * de consommer le premier item disponible ou le premier d'un type donné,
 * ainsi que de consulter son état (plein, vide, nombre d'items, etc.).</p>
 *
 * <p>Important : cette classe ne gère pas les effets des items.
 * Elle se contente de stocker et de fournir des items. La logique
 * d'application des effets est déléguée au {@code Game} ou au
 * {@code Player}.</p>
 */
public class Inventory {

    /** Liste interne des items stockés (FIFO). */
    private final List<Item> items = new ArrayList<>();

    /** Capacité maximale de l'inventaire. */
    private int maxSize = 3;

    /** Constructeur par défaut (capacité = 3). */
    public Inventory() {}

    /**
     * Constructeur avec capacité personnalisée.
     *
     * @param maxSize nombre maximal d'items stockables
     */
    public Inventory(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Ajoute un item dans l'inventaire si celui-ci n'est pas plein.
     *
     * @param type type de l'item à ajouter
     * @return {@code true} si l'ajout a réussi,
     *         {@code false} si l'inventaire est plein
     */
    public boolean addItem(ItemType type) {
        if (items.size() >= maxSize) {
            return false;
        }
        items.add(new Item(type));
        return true;
    }

    /**
     * Consomme et retire le premier item (FIFO).
     *
     * @return le {@link ItemType} de l'item consommé,
     *         ou {@code null} si l'inventaire est vide
     */
    public ItemType consumeNextItem() {
        if (items.isEmpty()) {
            return null;
        }
        Item item = items.remove(0);
        return item.getType();
    }

    /**
     * Consomme le premier item correspondant au type donné.
     *
     * @param type type de l'item recherché
     * @return le type consommé (identique à {@code type}),
     *         ou {@code null} si aucun item de ce type n'existe
     */
    public ItemType consumeFirstOfType(ItemType type) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == type) {
                Item removed = items.remove(i);
                return removed.getType();
            }
        }
        return null;
    }

    /**
     * Compte combien d'items d'un type donné sont présents.
     * Utilisé pour l'affichage HUD ("Timer : X", etc.).
     *
     * @param type type d'item recherché
     * @return nombre d'items correspondants
     */
    public int count(ItemType type) {
        int c = 0;
        for (Item item : items) {
            if (item.getType() == type) {
                c++;
            }
        }
        return c;
    }

    /**
     * Retourne une copie de la liste d'items.
     * La copie empêche les modifications externes de la liste interne.
     *
     * @return une nouvelle liste contenant les items actuels
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * @return capacité maximale de l'inventaire
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return nombre d'items actuellement stockés
     */
    public int getSize() {
        return items.size();
    }

    /**
     * @return {@code true} si l'inventaire est vide
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @return {@code true} si l'inventaire est plein
     */
    public boolean isFull() {
        return items.size() >= maxSize;
    }
}
