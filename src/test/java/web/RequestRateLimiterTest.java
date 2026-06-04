package web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

/**
 * RequestRateLimiterのテストクラス。
 */
class RequestRateLimiterTest {

    /**
     * 最大回数まではリクエストを許可することを確認する。
     */
    @Test
    void tryAcquire_shouldAllowRequestsUntilLimit() {
        RequestRateLimiter limiter = new RequestRateLimiter("test", 3, 1_000L);
        MockHttpSession session = new MockHttpSession();

        assertTrue(limiter.tryAcquire(session, 1_000L));
        assertTrue(limiter.tryAcquire(session, 1_100L));
        assertTrue(limiter.tryAcquire(session, 1_200L));
        assertFalse(limiter.tryAcquire(session, 1_300L));
    }

    /**
     * 時間幅を過ぎたリクエストは制限判定から外れることを確認する。
     */
    @Test
    void tryAcquire_shouldAllowAgainAfterWindowPassed() {
        RequestRateLimiter limiter = new RequestRateLimiter("test", 2, 1_000L);
        MockHttpSession session = new MockHttpSession();

        assertTrue(limiter.tryAcquire(session, 1_000L));
        assertTrue(limiter.tryAcquire(session, 1_100L));
        assertFalse(limiter.tryAcquire(session, 1_200L));
        assertTrue(limiter.tryAcquire(session, 2_001L));
    }

    /**
     * セッションごとにリクエスト数を別々に管理することを確認する。
     */
    @Test
    void tryAcquire_shouldManageRequestsPerSession() {
        RequestRateLimiter limiter = new RequestRateLimiter("test", 1, 1_000L);
        MockHttpSession sessionA = new MockHttpSession();
        MockHttpSession sessionB = new MockHttpSession();

        assertTrue(limiter.tryAcquire(sessionA, 1_000L));
        assertFalse(limiter.tryAcquire(sessionA, 1_100L));
        assertTrue(limiter.tryAcquire(sessionB, 1_100L));
    }

    /**
     * 不正な設定値の場合は例外になることを確認する。
     */
    @Test
    void constructor_shouldThrowException_whenArgumentsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new RequestRateLimiter("", 1, 1_000L));
        assertThrows(IllegalArgumentException.class, () -> new RequestRateLimiter("test", 0, 1_000L));
        assertThrows(IllegalArgumentException.class, () -> new RequestRateLimiter("test", 1, 0L));
    }
}
