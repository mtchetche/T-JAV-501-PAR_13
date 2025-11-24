import org.junit.jupiter.api.Test;

import src.core.Constants;
import src.entities.Ak47Bullet;

import static org.junit.jupiter.api.Assertions.*;

public class Ak47BulletTest {

    @Test
    void ak47BulletDiesOutsideScreen() {
        Ak47Bullet bullet = new Ak47Bullet(Constants.WINDOW_WIDTH + 10, 10, 300, 0, 2);

        bullet.update(0.5);

        assertFalse(bullet.isAlive());
    }
}
