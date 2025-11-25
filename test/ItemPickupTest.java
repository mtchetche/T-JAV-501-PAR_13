import src.core.ItemType;
import src.items.ItemPickup;

public class ItemPickupTest {

    @Test
    void itemTimerExpiresAfter20Seconds() {
        ItemPickup item = new ItemPickup(0, 0, ItemType.TIMER);

        item.update(10.0);
        assertFalse(item.isAlive());

        item.update(10.5);
        assertFalse(item.isAlive());
    }

    private void assertFalse(boolean alive) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertFalse'");
    }
}
