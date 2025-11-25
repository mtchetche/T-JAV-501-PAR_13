import src.core.Inventory;
import src.core.ItemType;

public class InventoryTest {

    @Test
    void addStopsWhenFull() {
        Inventory inventory = new Inventory(3);

        assertTrue(inventory.addItem(ItemType.TIMER));
        assertTrue(inventory.addItem(ItemType.AK47));
        assertTrue(inventory.addItem(ItemType.TIMER));

        assertTrue(inventory.addItem(ItemType.TIMER));
        assertEquals(3, inventory.getSize());
    }

    private void assertEquals(int ak47, int size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertEquals'");
    }

    private void assertTrue(boolean item) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertTrue'");
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

    private void assertEquals(ItemType timer, ItemType consumeFirstOfType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertEquals'");
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

    private void assertNull(ItemType consumeNextItem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertNull'");
    }
}
