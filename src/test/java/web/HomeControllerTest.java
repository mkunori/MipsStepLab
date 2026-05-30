package web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * HomeControllerのテストクラス。
 *
 * Web版MipsStepLabの画面表示やフォーム送信が、
 * Controllerを通して正しく処理されることを確認する。
 */
@WebMvcTest(HomeController.class)
@Import({
        WebMipsSessionService.class,
        StepResultViewMapper.class,
        MipsViewModelFactory.class
})
class HomeControllerTest {

    /** ControllerのHTTPリクエストをテストするためのMockMvc。 */
    @Autowired
    private MockMvc mockMvc;

    /** テスト用のWebMipsSessionService。 */
    @Autowired
    private WebMipsSessionService service;

    /**
     * GET /mips で画面が表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void home_shouldReturnMipsPage() throws Exception {
        mockMvc.perform(get("/mips"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("MipsStepLab")));
    }

    /**
     * 正常なMIPSプログラムをPOSTすると、WebMipsSessionが作成されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void submitProgram_shouldCreateSession_whenProgramIsValid() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        MvcResult result = mockMvc.perform(post("/mips")
                .param("programText", programText))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(request().sessionAttribute(
                        "mipsSession",
                        instanceOf(WebMipsSession.class)))
                .andReturn();

        HttpSession session = result.getRequest().getSession(false);

        assertNotNull(session);
        assertNotNull(session.getAttribute("mipsSession"));
    }

    /**
     * 不正なプログラムをPOSTすると、入力エラーとして表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void submitProgram_shouldShowError_whenProgramIsInvalid() throws Exception {
        mockMvc.perform(post("/mips")
                .param("programText", "invalid instruction"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("入力エラー")));
    }

    /**
     * セッションなしでPOST /mips/stepすると、
     * 実行状態なしのエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void step_shouldShowError_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/mips/step"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態がありません")));
    }

    /**
     * セッションに実行状態がある場合、
     * POST /mips/step で1ステップ実行できることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void step_shouldExecuteOneInstruction_whenSessionExists() throws Exception {
        WebMipsSessionService service = new WebMipsSessionService();

        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/step")
                .sessionAttr("mipsSession", session))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行中: 1ステップ実行しました。")));

        assertEquals(5, session.getCpu().getRegister(8));
        assertEquals(1, session.getStepRunner().getPc());
        assertTrue(session.getExecutedPcs().contains(0));
    }

    /**
     * セッションに実行状態がある場合、
     * POST /mips/reset で同じプログラムを初期状態に戻せることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void reset_shouldResetSession_whenSessionExists() throws Exception {
        WebMipsSessionService service = new WebMipsSessionService();

        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession oldSession = service.createSession(programText);
        service.step(oldSession);

        MvcResult result = mockMvc.perform(post("/mips/reset")
                .sessionAttr("mipsSession", oldSession))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態をリセットしました。")))
                .andReturn();

        WebMipsSession newSession = (WebMipsSession) result.getRequest().getSession()
                .getAttribute("mipsSession");

        assertNotNull(newSession);
        assertEquals(programText, newSession.getProgramText());
        assertEquals(0, newSession.getCpu().getRegister(8));
        assertEquals(0, newSession.getStepRunner().getPc());
        assertTrue(newSession.getExecutedPcs().isEmpty());
    }

    /**
     * セッションに実行状態がある場合、
     * POST /mips/breakpoints でブレークポイントを追加できることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void addBreakpoint_shouldAddBreakpoint_whenSessionExists() throws Exception {
        WebMipsSessionService service = new WebMipsSessionService();

        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/breakpoints")
                .sessionAttr("mipsSession", session)
                .param("breakpointPc", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("ブレークポイントを追加しました: PC 1")));

        assertTrue(session.getBreakpointManager().contains(1));
    }

    /**
     * セッションに実行状態がある場合、
     * POST /mips/breakpoints/delete でブレークポイントを削除できることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void deleteBreakpoint_shouldDeleteBreakpoint_whenSessionExists() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 1);

        mockMvc.perform(post("/mips/breakpoints/delete")
                .sessionAttr("mipsSession", session)
                .param("breakpointPc", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("ブレークポイントを削除しました: PC 1")));

        assertFalse(session.getBreakpointManager().contains(1));
    }

    /**
     * セッションに実行状態とブレークポイントがある場合、
     * POST /mips/run でブレークポイントまで実行できることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void run_shouldRunUntilBreakpoint_whenBreakpointExists() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "add $t2, $t0, $t1");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 2);

        mockMvc.perform(post("/mips/run")
                .sessionAttr("mipsSession", session))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("ブレークポイントに到達しました: PC 2")));

        assertEquals(5, session.getCpu().getRegister(8));
        assertEquals(3, session.getCpu().getRegister(9));
        assertEquals(0, session.getCpu().getRegister(10));
        assertEquals(2, session.getStepRunner().getPc());
        assertTrue(session.getExecutedPcs().contains(0));
        assertTrue(session.getExecutedPcs().contains(1));
        assertFalse(session.getExecutedPcs().contains(2));
    }

    /**
     * 現在PCがすでにブレークポイントの場合、
     * POST /mips/run で命令を実行せずに停止することを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void run_shouldStopWithoutExecution_whenCurrentPcIsBreakpoint() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 0);

        mockMvc.perform(post("/mips/run")
                .sessionAttr("mipsSession", session))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("現在のPCがブレークポイントです: PC 0")));

        assertEquals(0, session.getCpu().getRegister(8));
        assertEquals(0, session.getStepRunner().getPc());
        assertTrue(session.getExecutedPcs().isEmpty());
    }

    /**
     * セッションなしでPOST /mips/runすると、
     * 実行状態なしのエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void run_shouldShowError_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/mips/run"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態がありません")));
    }

    /**
     * セッションなしでPOST /mips/resetすると、
     * 実行状態なしのエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void reset_shouldShowError_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/mips/reset"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態がありません")));
    }

    /**
     * セッションなしでPOST /mips/breakpointsすると、
     * 実行状態なしのエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void addBreakpoint_shouldShowError_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/mips/breakpoints")
                .param("breakpointPc", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態がありません")));
    }

    /**
     * セッションなしでPOST /mips/breakpoints/deleteすると、
     * 実行状態なしのエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void deleteBreakpoint_shouldShowError_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/mips/breakpoints/delete")
                .param("breakpointPc", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("実行状態がありません")));
    }

    /**
     * 範囲外PCを指定してPOST /mips/breakpointsすると、
     * ブレークポイント追加失敗メッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void addBreakpoint_shouldShowError_whenPcIsOutOfRange() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/breakpoints")
                .sessionAttr("mipsSession", session)
                .param("breakpointPc", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("ブレークポイント追加失敗")))
                .andExpect(content().string(containsString("PCがプログラム範囲外です: 2")));

        assertFalse(session.getBreakpointManager().contains(2));
    }

    /**
     * PC番号なしでPOST /mips/breakpointsすると、
     * PC番号入力を促すエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void addBreakpoint_shouldShowError_whenPcIsMissing() throws Exception {
        String programText = "nop";
        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/breakpoints")
                .sessionAttr("mipsSession", session))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("PC番号を入力してください。")));

        assertTrue(session.getBreakpointManager().getAll().isEmpty());
    }

    /**
     * PC番号なしでPOST /mips/breakpoints/deleteすると、
     * 削除対象PC番号の入力を促すエラーメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void deleteBreakpoint_shouldShowError_whenPcIsMissing() throws Exception {
        String programText = "nop";
        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/breakpoints/delete")
                .sessionAttr("mipsSession", session))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("削除するPC番号を入力してください。")));
    }

    /**
     * 未登録PCを指定してPOST /mips/breakpoints/deleteすると、
     * 未登録であることを示すメッセージが表示されることを確認する。
     *
     * @throws Exception MockMvc実行時に例外が発生した場合
     */
    @Test
    void deleteBreakpoint_shouldShowWarning_whenBreakpointDoesNotExist() throws Exception {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        mockMvc.perform(post("/mips/breakpoints/delete")
                .sessionAttr("mipsSession", session)
                .param("breakpointPc", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mips"))
                .andExpect(model().attributeExists("viewModel"))
                .andExpect(content().string(containsString("ブレークポイントは登録されていません: PC 1")));

        assertFalse(session.getBreakpointManager().contains(1));
    }
}