import src.core.TimeFreezeManager;

public class TimeFreezeManagerTest {

    @Test
    void timeFreezeDeactivatesAfterDuration() {
        TimeFreezeManager manager = new TimeFreezeManager();

        manager.activate(5.0);
        assertTrue(manager.isFrozen());

        manager.update(3.0);
        assertTrue(manager.isFrozen());

        manager.update(3.0);
        assertTrue(manager.isFrozen());
        assertEquals(0.0, manager.getRemainingFreezeTime());
    }

    private void assertEquals(double d, double remainingFreezeTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertEquals'");
    }

    private void assertTrue(boolean frozen) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertTrue'");
    }
}
