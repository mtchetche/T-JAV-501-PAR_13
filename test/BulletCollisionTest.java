import org.junit.jupiter.api.Test;

import src.entities.Bullet;
import src.world.Level;

import static org.junit.jupiter.api.Assertions.*;

public class BulletCollisionTest {

    @Test
    void bulletDiesOnPlatformCollision() {
        Level level = new Level();
        Bullet bullet = new Bullet(10, 690, 0, 0, 1, level);

        bullet.update(0.1);

        assertFalse(bullet.isAlive());
    }
}
