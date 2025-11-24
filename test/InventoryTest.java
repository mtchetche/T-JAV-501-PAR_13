import org.junit.jupiter.api.Test;

import src.core.Inventory;
import src.core.ItemType;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    void addStopsWhenFull() {
        Inventory inventory = new Inventory(3);

        assertTrue(inventory.addItem(ItemType.TIMER));
        assertTrue(inventory.addItem(ItemType.AK47));
        assertTrue(inventory.addItem(ItemType.TIMER));

        assertFalse(inventory.addItem(ItemType.TIMER));
        assertEquals(3, inventory.getSize());
    }

    @Test
    void consumeFirstOfTypeRemovesOnlyThatType() {
        Inventory inventory = new Inventory(3);
        inventory.addItem(ItemType.TIMER);
        inventory.addItem(ItemType.AK47);
        inventory.addItem(ItemType.TIMER);

        assertEquals(ItemType.AK47, inventory.consumeFirstOfType(ItemType.AK47));
        assertEquals(2, inventory.count(ItemType.TIMER));
        assertEquals(2, inventory.getSize());

        assertEquals(ItemType.TIMER, inventory.consumeFirstOfType(ItemType.TIMER));
        assertEquals(1, inventory.count(ItemType.TIMER));
        assertEquals(1, inventory.getSize());
    }

    @Test
    void consumeNextItemIsFifo() {
        Inventory inventory = new Inventory(3);
        inventory.addItem(ItemType.TIMER);
        inventory.addItem(ItemType.AK47);

        assertEquals(ItemType.TIMER, inventory.consumeNextItem());
        assertEquals(1, inventory.getSize());
        assertEquals(ItemType.AK47, inventory.consumeNextItem());
        assertEquals(0, inventory.getSize());
        assertNull(inventory.consumeNextItem());
    }
}
