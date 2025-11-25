import src.core.Constants;
import src.entities.Ak47Bullet;

public class Ak47BulletTest {

    @Test
    void ak47BulletDiesOutsideScreen() {
        Ak47Bullet bullet = new Ak47Bullet(Constants.WINDOW_WIDTH + 10, 10, 300, 0, 2);

        bullet.update(0.5);

        assertFalse(bullet.isAlive());
    }

    private void assertFalse(boolean alive) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertFalse'");
    }
}
