package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import execution.StepResult;

/**
 * MipsViewModelFactoryのテストクラス。
 *
 * Web画面に渡すMipsViewModelが、用途ごとに正しく作成されることを確認する。
 */
class MipsViewModelFactoryTest {

    /** Web版の実行状態を扱うService。 */
    private WebMipsSessionService service;

    /** StepResultをWeb表示用データへ変換するMapper。 */
    private StepResultViewMapper mapper;

    /** テスト対象のFactory。 */
    private MipsViewModelFactory factory;

    /**
     * 各テストの前に、Service、Mapper、Factoryを生成する。
     */
    @BeforeEach
    void setUp() {
        service = new WebMipsSessionService();
        mapper = new StepResultViewMapper();
        factory = new MipsViewModelFactory(service);
    }

    /**
     * 初期表示用ViewModelを作成できることを確認する。
     */
    @Test
    void createInitialViewModel_shouldCreateInitialViewModel() {
        String defaultProgram = "nop";

        MipsViewModel viewModel = factory.createInitialViewModel(defaultProgram);

        assertEquals(defaultProgram, viewModel.getProgramText());
        assertEquals(List.of("nop"), viewModel.getProgramLines());
        assertNull(viewModel.getMessage());
        assertEquals(MessageType.INFO.getCssClassName(), viewModel.getMessageType());
        assertNull(viewModel.getParseSuccess());
        assertEquals(0, viewModel.getInstructionCount());
        assertFalse(viewModel.isReadyToRun());
        assertEquals(0, viewModel.getCurrentPc());
        assertTrue(viewModel.getExecutedPcs().isEmpty());
        assertTrue(viewModel.getBreakpoints().isEmpty());
        assertNull(viewModel.getStepResult());
    }

    /**
     * セッションなしエラー用ViewModelを作成できることを確認する。
     */
    @Test
    void createNoSessionViewModel_shouldCreateErrorViewModel() {
        String defaultProgram = "nop";
        String message = "実行状態がありません。";

        MipsViewModel viewModel = factory.createNoSessionViewModel(defaultProgram, message);

        assertEquals(defaultProgram, viewModel.getProgramText());
        assertEquals(message, viewModel.getMessage());
        assertEquals(MessageType.ERROR.getCssClassName(), viewModel.getMessageType());
        assertEquals(false, viewModel.getParseSuccess());
        assertFalse(viewModel.isReadyToRun());
        assertEquals(-1, viewModel.getCurrentPc());
        assertTrue(viewModel.getExecutedPcs().isEmpty());
        assertTrue(viewModel.getBreakpoints().isEmpty());
        assertNull(viewModel.getStepResult());
    }

    /**
     * パース成功後のViewModelを作成できることを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateParsedViewModel_whenParseSucceeded() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");
        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 2 命令",
                MessageType.SUCCESS,
                true,
                2,
                true);

        assertEquals(programText, viewModel.getProgramText());
        assertEquals(programLines, viewModel.getProgramLines());
        assertEquals("パース成功: 2 命令", viewModel.getMessage());
        assertEquals(MessageType.SUCCESS.getCssClassName(), viewModel.getMessageType());
        assertEquals(true, viewModel.getParseSuccess());
        assertEquals(2, viewModel.getInstructionCount());
        assertTrue(viewModel.isReadyToRun());
        assertEquals(0, viewModel.getCurrentPc());
        assertTrue(viewModel.getExecutedPcs().isEmpty());
        assertTrue(viewModel.getBreakpoints().isEmpty());
        assertNull(viewModel.getStepResult());
    }

    /**
     * パース失敗後のViewModelを作成できることを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateParsedViewModel_whenParseFailed() {
        String programText = "invalid instruction";
        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "入力エラー: 不正な命令です。",
                MessageType.ERROR,
                false,
                0,
                false);

        assertEquals(programText, viewModel.getProgramText());
        assertEquals(programLines, viewModel.getProgramLines());
        assertEquals(MessageType.ERROR.getCssClassName(), viewModel.getMessageType());
        assertEquals(false, viewModel.getParseSuccess());
        assertEquals(0, viewModel.getInstructionCount());
        assertFalse(viewModel.isReadyToRun());
        assertEquals(-1, viewModel.getCurrentPc());
        assertNull(viewModel.getStepResult());
    }

    /**
     * 現在の実行状態を反映したViewModelを作成できることを確認する。
     */
    @Test
    void createSessionStateViewModel_shouldReflectSessionState() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);
        service.step(session);
        service.addBreakpoint(session, 1);

        MipsViewModel viewModel = factory.createSessionStateViewModel(
                session,
                "ブレークポイントを追加しました: PC 1",
                MessageType.SUCCESS);

        assertEquals(programText, viewModel.getProgramText());
        assertEquals(2, viewModel.getInstructionCount());
        assertTrue(viewModel.isReadyToRun());
        assertEquals(1, viewModel.getCurrentPc());
        assertTrue(viewModel.getExecutedPcs().contains(0));
        assertTrue(viewModel.getBreakpoints().contains(1));
        assertNull(viewModel.getStepResult());
        assertEquals(MessageType.SUCCESS.getCssClassName(), viewModel.getMessageType());
    }

    /**
     * ステップ実行結果を含むViewModelを作成できることを確認する。
     */
    @Test
    void createStepResultViewModel_shouldCreateViewModelWithStepResult() {
        String programText = "addi $t0, $zero, 5";
        WebMipsSession session = service.createSession(programText);

        StepResult result = service.step(session);
        StepResultViewData viewData = mapper.toViewData(result);

        MipsViewModel viewModel = factory.createStepResultViewModel(
                session,
                result,
                "プログラムが終了しました。",
                MessageType.SUCCESS,
                viewData,
                "addi $t0, $zero, 5");

        assertEquals(programText, viewModel.getProgramText());
        assertEquals(MessageType.SUCCESS.getCssClassName(), viewModel.getMessageType());
        assertEquals(true, viewModel.getParseSuccess());
        assertFalse(viewModel.isReadyToRun());
        assertEquals(-1, viewModel.getCurrentPc());

        assertNotNull(viewModel.getStepResult());
        assertEquals("addi $t0, $zero, 5", viewModel.getExecutedInstructionText());

        assertEquals(1, viewModel.getRegisterDiffs().size());
        assertEquals(32, viewModel.getRegisterValues().size());
        assertEquals(2, viewModel.getHiLoValues().size());
        assertFalse(viewModel.getMemoryValues().isEmpty());
    }
}