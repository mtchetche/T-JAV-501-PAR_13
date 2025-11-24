import org.junit.jupiter.api.Test;

import src.core.ItemType;
import src.items.ItemPickup;

import static org.junit.jupiter.api.Assertions.*;

public class ItemPickupTest {

    @Test
    void itemTimerExpiresAfter20Seconds() {
        ItemPickup item = new ItemPickup(0, 0, ItemType.TIMER);

        item.update(10.0);
        assertTrue(item.isAlive());

        item.update(10.5);
        assertFalse(item.isAlive());
    }
}
