package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.ServletException;

/**
 * RequestRateLimitFilterのテストクラス。
 */
class RequestRateLimitFilterTest {

    /**
     * GET / は制限対象にしないことを確認する。
     *
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Test
    void doFilter_shouldNotLimitGetHome() throws ServletException, IOException {
        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpServletRequest request = createRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    /**
     * MipsStepLab以外のPOSTは制限対象にしないことを確認する。
     *
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Test
    void doFilter_shouldNotLimitOtherPostPath() throws ServletException, IOException {
        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpServletRequest request = createRequest("POST", "/other");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }

    /**
     * 短時間にMipsStepLabのPOSTが集中した場合、HTTP 429を返すことを確認する。
     *
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Test
    void doFilter_shouldReturnTooManyRequests_whenPostStepRequestsAreConcentrated()
            throws ServletException, IOException {

        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpSession session = new MockHttpSession();

        for (int i = 0; i < 60; i++) {
            MockHttpServletResponse response = executePostStep(filter, session);

            assertEquals(200, response.getStatus());
        }

        MockHttpServletResponse blockedResponse = executePostStep(filter, session);

        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getContentAsString()
                .contains("短時間に操作が集中したため"));
    }

    /**
     * context-pathが/mipsの場合でも、アプリ内パス/stepを制限対象にすることを確認する。
     *
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Test
    void doFilter_shouldLimitPostStep_whenContextPathIsMips()
            throws ServletException, IOException {

        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpSession session = new MockHttpSession();

        for (int i = 0; i < 60; i++) {
            MockHttpServletResponse response = executePostStepWithMipsContextPath(filter, session);

            assertEquals(200, response.getStatus());
        }

        MockHttpServletResponse blockedResponse = executePostStepWithMipsContextPath(filter, session);

        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getContentAsString()
                .contains("短時間に操作が集中したため"));
    }

    /**
     * POST /step をFilterに通す。
     *
     * @param filter  テスト対象Filter
     * @param session HTTPセッション
     * @return HTTPレスポンス
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    private MockHttpServletResponse executePostStep(
            RequestRateLimitFilter filter,
            MockHttpSession session) throws ServletException, IOException {

        MockHttpServletRequest request = createRequest("POST", "/step");
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);
        return response;
    }

    /**
     * context-pathが/mipsのPOST /stepをFilterに通す。
     *
     * @param filter  テスト対象Filter
     * @param session HTTPセッション
     * @return HTTPレスポンス
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    private MockHttpServletResponse executePostStepWithMipsContextPath(
            RequestRateLimitFilter filter,
            MockHttpSession session) throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/mips/step");
        request.setContextPath("/mips");
        request.setServletPath("/step");
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);
        return response;
    }

    /**
     * servletPathを明示したMockリクエストを作成する。
     *
     * @param method HTTPメソッド
     * @param path   アプリ内パス
     * @return Mockリクエスト
     */
    private MockHttpServletRequest createRequest(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        return request;
    }
}
