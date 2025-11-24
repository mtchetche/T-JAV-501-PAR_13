import org.junit.jupiter.api.Test;

import src.core.TimeFreezeManager;

import static org.junit.jupiter.api.Assertions.*;

public class TimeFreezeManagerTest {

    @Test
    void timeFreezeDeactivatesAfterDuration() {
        TimeFreezeManager manager = new TimeFreezeManager();

        manager.activate(5.0);
        assertTrue(manager.isFrozen());

        manager.update(3.0);
        assertTrue(manager.isFrozen());

        manager.update(3.0);
        assertFalse(manager.isFrozen());
        assertEquals(0.0, manager.getRemainingFreezeTime());
    }
}
