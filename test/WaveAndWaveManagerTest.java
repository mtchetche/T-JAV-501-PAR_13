import org.junit.jupiter.api.Test;

import src.core.ProjectileManager;
import src.core.TimeFreezeManager;
import src.core.Wave;
import src.core.WaveManager;
import src.entities.Player;
import src.input.KeyboardInput;
import src.world.Level;

import static org.junit.jupiter.api.Assertions.*;

public class WaveAndWaveManagerTest {

    @Test
    void waveFinishesAfterDuration() {
        Wave wave = new Wave(2.0);

        wave.update(1.5);
        assertFalse(wave.isFinished());
        assertEquals(0.5, wave.getRemainingTime(), 1e-6);

        wave.update(1.0);
        assertTrue(wave.isFinished());
        assertEquals(0.0, wave.getRemainingTime(), 1e-9);
    }

    @Test
    void waveManagerCapsAtThree() {
        KeyboardInput keyboardInput = new KeyboardInput();
        Level level = new Level();
        Player player = new Player(0, 0, keyboardInput, level);
        TimeFreezeManager timeFreezeManager = new TimeFreezeManager();
        ProjectileManager projectileManager = new ProjectileManager(level, player, timeFreezeManager);
        WaveManager waveManager = new WaveManager(level, player, projectileManager, timeFreezeManager);

        assertEquals(1, waveManager.getCurrentWaveNumber());

        waveManager.goToNextWave();
        waveManager.goToNextWave();
        waveManager.goToNextWave();

        assertEquals(3, waveManager.getCurrentWaveNumber());
    }
}
