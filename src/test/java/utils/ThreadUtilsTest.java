package utils;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

class ThreadUtilsTest {
    @Test
    public void testWaitUntil() {
        ThreadUtils.waitUntil(LocalTime.of(0, 34));
    }

}
