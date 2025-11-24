package src.core;

/**
 * Représente un item stocké dans l'inventaire.
 *
 * <p>Un item est actuellement défini uniquement par son {@link ItemType}.
 * Cette classe est volontairement simple pour servir de base à
 * d'éventuelles évolutions futures, telles que :</p>
 *
 * <ul>
 *   <li>une quantité par item,</li>
 *   <li>une rareté,</li>
 *   <li>une icône ou un sprite spécifique,</li>
 *   <li>des métadonnées supplémentaires,</li>
 *   <li>des effets personnalisés.</li>
 * </ul>
 */
public class Item {

    /** Type de l'item (définit son identité et sa catégorie). */
    private final ItemType type;

    /**
     * Crée un nouvel item du type donné.
     *
     * @param type type de l'item
     */
    public Item(ItemType type) {
        this.type = type;
    }

    /**
     * Retourne le type associé à cet item.
     *
     * @return le {@link ItemType} de l’item
     */
    public ItemType getType() {
        return type;
    }
}
