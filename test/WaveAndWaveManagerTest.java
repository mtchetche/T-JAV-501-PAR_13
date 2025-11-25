

import src.core.ProjectileManager;
import src.core.TimeFreezeManager;
import src.core.Wave;
import src.core.WaveManager;
import src.entities.Player;
import src.input.KeyboardInput;
import src.world.Level;

public class WaveAndWaveManagerTest {

    @Test
    void waveFinishesAfterDuration() {
        Wave wave = new Wave(2.0);

        wave.update(1.5);
        assertFalse(wave.isFinished());
        assertEquals(0.5, wave.getRemainingTime(), 1e-6);

        wave.update(1.0);
        assertFalse(wave.isFinished());
        assertEquals(0.0, wave.getRemainingTime(), 1e-9);
    }

    private void assertEquals(double d, double remainingTime, double e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertEquals'");
    }

    private void assertFalse(boolean finished) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertFalse'");
    }

    @Test
    void waveManagerCapsAtThree() {
        KeyboardInput keyboardInput = new KeyboardInput();
        Level level = new Level();
        Player player = new Player(0, 0, keyboardInput, level);
        TimeFreezeManager timeFreezeManager = new TimeFreezeManager();
        ProjectileManager projectileManager = new ProjectileManager(level, player, timeFreezeManager);
        WaveManager waveManager = new WaveManager(level, player, projectileManager, timeFreezeManager);

        assertEquals(1, waveManager.getCurrentWaveNumber(), 0);

        waveManager.goToNextWave();
        waveManager.goToNextWave();
        waveManager.goToNextWave();

        assertEquals(3, waveManager.getCurrentWaveNumber(), 0);
    }
}
