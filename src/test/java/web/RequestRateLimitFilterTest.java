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
     * GET /mips は制限対象にしないことを確認する。
     *
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    @Test
    void doFilter_shouldNotLimitGetMips() throws ServletException, IOException {
        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/mips");
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
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/other");
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
    void doFilter_shouldReturnTooManyRequests_whenPostMipsRequestsAreConcentrated()
            throws ServletException, IOException {

        RequestRateLimitFilter filter = new RequestRateLimitFilter();
        MockHttpSession session = new MockHttpSession();

        for (int i = 0; i < 60; i++) {
            MockHttpServletResponse response = executePostMipsStep(filter, session);

            assertEquals(200, response.getStatus());
        }

        MockHttpServletResponse blockedResponse = executePostMipsStep(filter, session);

        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getContentAsString()
                .contains("短時間に操作が集中したため"));
    }

    /**
     * POST /mips/step をFilterに通す。
     *
     * @param filter  テスト対象Filter
     * @param session HTTPセッション
     * @return HTTPレスポンス
     * @throws ServletException Servlet処理で例外が発生した場合
     * @throws IOException      入出力で例外が発生した場合
     */
    private MockHttpServletResponse executePostMipsStep(
            RequestRateLimitFilter filter,
            MockHttpSession session) throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/mips/step");
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);
        return response;
    }
}
