package web;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * MipsStepLab Web版の操作リクエストを簡易的に制限するFilter。
 *
 * 短時間にPOST操作が集中した場合、サーバー負荷を抑えるために
 * HTTP 429 Too Many Requestsを返す。
 */
@Component
public class RequestRateLimitFilter extends OncePerRequestFilter {

    /** 制限対象のアプリ内POSTパス。 */
    private static final Set<String> TARGET_POST_PATHS = Set.of(
            "/",
            "/step",
            "/run",
            "/reset",
            "/clear",
            "/breakpoints",
            "/breakpoints/delete");

    /** 一定時間内に許可するPOST操作数。 */
    private static final int MAX_POST_REQUESTS = 60;

    /** リクエスト数を数える時間幅。 */
    private static final long WINDOW_MILLIS = 10_000L;

    /** 制限時に返すメッセージ。 */
    private static final String TOO_MANY_REQUESTS_MESSAGE = "短時間に操作が集中したため、少し時間をおいてから再度実行してください。";

    /** セッション単位のリクエスト制限。 */
    private final RequestRateLimiter limiter = new RequestRateLimiter(
            "mips-post-operations",
            MAX_POST_REQUESTS,
            WINDOW_MILLIS);

    /**
     * リクエストを制限するか判定し、制限対象でなければ後続処理へ渡す。
     *
     * @param request     HTTPリクエスト
     * @param response    HTTPレスポンス
     * @param filterChain 後続のFilterChain
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!shouldLimit(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        boolean allowed = limiter.tryAcquire(session, System.currentTimeMillis());

        if (allowed) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(TOO_MANY_REQUESTS_MESSAGE);
    }

    /**
     * リクエスト制限の対象か判定する。
     *
     * GET表示や静的ファイルは制限せず、MipsStepLabのPOST操作だけを制限する。
     * context-pathが設定されている環境でも同じ判定にするため、getServletPath()を使う。
     *
     * @param request HTTPリクエスト
     * @return 制限対象の場合はtrue
     */
    private boolean shouldLimit(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        return TARGET_POST_PATHS.contains(request.getServletPath());
    }
}
