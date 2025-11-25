

import src.entities.Bullet;
import src.world.Level;

public class BulletCollisionTest {

    @Test
    void bulletDiesOnPlatformCollision() {
        Level level = new Level();
        Bullet bullet = new Bullet(10, 690, 0, 0, 1, level);

        bullet.update(0.1);

        assertFalse(bullet.isAlive());
    }

    private void assertFalse(boolean alive) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertFalse'");
    }
}
