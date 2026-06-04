package web;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import jakarta.servlet.http.HttpSession;

/**
 * HTTPセッション単位で、短時間のリクエスト数を制限するクラス。
 *
 * MipsStepLabでは、Web版の実行状態をHTTPセッションに保持する。
 * そのため、同じセッションから短時間に大量の操作が行われた場合は、
 * サーバー負荷を抑えるために一時的に制限する。
 */
public class RequestRateLimiter {

    /** セッション属性名の接頭辞。 */
    private static final String ATTRIBUTE_PREFIX = RequestRateLimiter.class.getName() + ".";

    /** 許可する最大リクエスト数。 */
    private final int maxRequests;

    /** リクエスト数を数える時間幅。 */
    private final long windowMillis;

    /** セッションに保存する属性名。 */
    private final String attributeName;

    /**
     * RequestRateLimiterを生成する。
     *
     * @param name         制限対象を識別する名前
     * @param maxRequests  許可する最大リクエスト数
     * @param windowMillis リクエスト数を数える時間幅。ミリ秒単位
     */
    public RequestRateLimiter(String name, int maxRequests, long windowMillis) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank.");
        }
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive.");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive.");
        }

        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.attributeName = ATTRIBUTE_PREFIX + name;
    }

    /**
     * リクエストを許可できるか判定する。
     *
     * 許可できる場合は、今回のリクエスト時刻を記録する。
     * 許可できない場合は記録せず、falseを返す。
     *
     * @param session   HTTPセッション
     * @param nowMillis 現在時刻。ミリ秒単位
     * @return リクエストを許可できる場合はtrue
     */
    public boolean tryAcquire(HttpSession session, long nowMillis) {
        RequestWindow window = getOrCreateWindow(session);

        synchronized (window) {
            window.removeExpiredRequests(nowMillis - windowMillis);

            if (window.size() >= maxRequests) {
                return false;
            }

            window.add(nowMillis);
            return true;
        }
    }

    /**
     * セッションからリクエスト記録用のWindowを取得する。
     *
     * @param session HTTPセッション
     * @return リクエスト記録用のWindow
     */
    private RequestWindow getOrCreateWindow(HttpSession session) {
        Object value = session.getAttribute(attributeName);

        if (value instanceof RequestWindow window) {
            return window;
        }

        RequestWindow window = new RequestWindow();
        session.setAttribute(attributeName, window);
        return window;
    }

    /**
     * 一定時間内のリクエスト時刻を保持するクラス。
     */
    private static class RequestWindow implements Serializable {

        /** シリアライズ用ID。 */
        private static final long serialVersionUID = 1L;

        /** リクエスト時刻の一覧。 */
        private final Deque<Long> requestTimes = new ArrayDeque<>();

        /**
         * 古くなったリクエスト時刻を削除する。
         *
         * @param oldestAllowedMillis 残す最古の時刻
         */
        void removeExpiredRequests(long oldestAllowedMillis) {
            while (!requestTimes.isEmpty() && requestTimes.peekFirst() <= oldestAllowedMillis) {
                requestTimes.removeFirst();
            }
        }

        /**
         * リクエスト時刻を追加する。
         *
         * @param nowMillis リクエスト時刻
         */
        void add(long nowMillis) {
            requestTimes.addLast(nowMillis);
        }

        /**
         * 現在保持しているリクエスト数を返す。
         *
         * @return 現在保持しているリクエスト数
         */
        int size() {
            return requestTimes.size();
        }
    }
}
