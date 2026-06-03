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

        ExecutedInstructionView executedInstructionView = mapper.createExecutedInstructionView(
                result,
                service.splitLines(programText));

        MipsViewModel viewModel = factory.createStepResultViewModel(
                session,
                result,
                "プログラムが終了しました。",
                MessageType.SUCCESS,
                viewData,
                executedInstructionView);

        assertEquals(programText, viewModel.getProgramText());
        assertEquals(MessageType.SUCCESS.getCssClassName(), viewModel.getMessageType());
        assertEquals(true, viewModel.getParseSuccess());
        assertFalse(viewModel.isReadyToRun());
        assertEquals(-1, viewModel.getCurrentPc());

        assertNotNull(viewModel.getStepResult());
        assertNotNull(viewModel.getExecutedInstructionView());
        assertEquals(0, viewModel.getExecutedInstructionView().getPcBefore());
        assertEquals(1, viewModel.getExecutedInstructionView().getPcAfter());
        assertEquals("addi $t0, $zero, 5", viewModel.getExecutedInstructionText());
        assertEquals("addi $t0, $zero, 5", viewModel.getExecutedInstructionView().getInstructionText());

        assertEquals(1, viewModel.getRegisterDiffs().size());
        assertEquals(32, viewModel.getRegisterValues().size());
        assertEquals(2, viewModel.getHiLoValues().size());
        assertFalse(viewModel.getMemoryValues().isEmpty());
    }

    /**
     * 現在の実行状態を表示するViewModelでは、
     * セッション内のCPU現在値がレジスタ一覧に反映されることを確認する。
     */
    @Test
    void createSessionStateViewModel_shouldReflectCurrentRegisterValues() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        service.step(session);

        MipsViewModel viewModel = factory.createSessionStateViewModel(
                session,
                "ブレークポイントを追加しました: PC 1",
                MessageType.SUCCESS);

        RegisterValue r8 = viewModel.getRegisterValues().get(8);
        RegisterValue r9 = viewModel.getRegisterValues().get(9);

        assertEquals(8, r8.getRegisterNumber());
        assertEquals(5, r8.getValue());
        assertFalse(r8.isChanged());

        assertEquals(9, r9.getRegisterNumber());
        assertEquals(0, r9.getValue());
        assertFalse(r9.isChanged());
    }

    /**
     * 現在の実行状態を表示するViewModelでは、
     * セッション内のメモリ現在値がメモリ一覧に反映されることを確認する。
     */
    @Test
    void createSessionStateViewModel_shouldReflectCurrentMemoryValues() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "sw $t0, 0($zero)");

        WebMipsSession session = service.createSession(programText);

        service.step(session);
        service.step(session);

        MipsViewModel viewModel = factory.createSessionStateViewModel(
                session,
                "ブレークポイントを追加しました: PC 1",
                MessageType.SUCCESS);

        boolean existsNonZeroMemory = viewModel.getMemoryValues().stream()
                .anyMatch(memory -> memory.getValue() != 0);

        boolean existsChangedMemory = viewModel.getMemoryValues().stream()
                .anyMatch(MemoryValue::isChanged);

        assertTrue(existsNonZeroMemory);
        assertFalse(existsChangedMemory);
    }

    /**
     * 現在の実行状態を表示するViewModelでは、
     * セッション内のHI/LO現在値がHI/LO一覧に反映されることを確認する。
     */
    @Test
    void createSessionStateViewModel_shouldReflectCurrentHiLoValues() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "mult $t0, $t1");

        WebMipsSession session = service.createSession(programText);

        service.step(session);
        service.step(session);
        service.step(session);

        MipsViewModel viewModel = factory.createSessionStateViewModel(
                session,
                "ブレークポイントを追加しました: PC 1",
                MessageType.SUCCESS);

        HiLoValue hi = viewModel.getHiLoValues().get(0);
        HiLoValue lo = viewModel.getHiLoValues().get(1);

        assertEquals("HI", hi.getRegisterName());
        assertEquals(0, hi.getValue());
        assertFalse(hi.isChanged());

        assertEquals("LO", lo.getRegisterName());
        assertEquals(15, lo.getValue());
        assertFalse(lo.isChanged());
    }

    /**
     * プログラム一覧表示用の行情報では、
     * ラベル行にはPCが付かず、命令行だけにPCが割り当てられることを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateProgramLineViewsSkippingLabelLines() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 0",
                "addi $t1, $zero, 3",
                "loop:",
                "addi $t0, $t0, 1",
                "bne $t0, $t1, loop",
                "addi $t2, $zero, 99");

        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 5 命令",
                MessageType.SUCCESS,
                true,
                5,
                true);

        List<ProgramLineView> lineViews = viewModel.getProgramLineViews();

        assertEquals(6, lineViews.size());

        assertEquals(0, lineViews.get(0).getLineNumber());
        assertEquals(0, lineViews.get(0).getPc());
        assertEquals("addi $t0, $zero, 0", lineViews.get(0).getText());
        assertTrue(lineViews.get(0).isInstructionLine());

        assertEquals(1, lineViews.get(1).getLineNumber());
        assertEquals(1, lineViews.get(1).getPc());
        assertEquals("addi $t1, $zero, 3", lineViews.get(1).getText());
        assertTrue(lineViews.get(1).isInstructionLine());

        assertEquals(2, lineViews.get(2).getLineNumber());
        assertNull(lineViews.get(2).getPc());
        assertEquals("loop:", lineViews.get(2).getText());
        assertFalse(lineViews.get(2).isInstructionLine());

        assertEquals(3, lineViews.get(3).getLineNumber());
        assertEquals(2, lineViews.get(3).getPc());
        assertEquals("addi $t0, $t0, 1", lineViews.get(3).getText());
        assertTrue(lineViews.get(3).isInstructionLine());

        assertEquals(4, lineViews.get(4).getLineNumber());
        assertEquals(3, lineViews.get(4).getPc());
        assertEquals("bne $t0, $t1, loop", lineViews.get(4).getText());
        assertTrue(lineViews.get(4).isInstructionLine());

        assertEquals(5, lineViews.get(5).getLineNumber());
        assertEquals(4, lineViews.get(5).getPc());
        assertEquals("addi $t2, $zero, 99", lineViews.get(5).getText());
        assertTrue(lineViews.get(5).isInstructionLine());
    }

    /**
     * プログラム一覧表示用の行情報では、
     * Serviceで除外された空行が含まれず、命令行だけにPCが割り当てられることを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateProgramLineViewsWithoutEmptyLines() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 1",
                "",
                "addi $t1, $zero, 2");

        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 2 命令",
                MessageType.SUCCESS,
                true,
                2,
                true);

        List<ProgramLineView> lineViews = viewModel.getProgramLineViews();

        assertEquals(2, lineViews.size());

        assertEquals(0, lineViews.get(0).getLineNumber());
        assertEquals(0, lineViews.get(0).getPc());
        assertEquals("addi $t0, $zero, 1", lineViews.get(0).getText());
        assertTrue(lineViews.get(0).isInstructionLine());

        assertEquals(1, lineViews.get(1).getLineNumber());
        assertEquals(1, lineViews.get(1).getPc());
        assertEquals("addi $t1, $zero, 2", lineViews.get(1).getText());
        assertTrue(lineViews.get(1).isInstructionLine());
    }

    /**
     * プログラム一覧表示用の行情報では、
     * Serviceで除外された空白だけの行が含まれず、命令行だけにPCが割り当てられることを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateProgramLineViewsWithoutBlankLines() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 1",
                "    ",
                "\t",
                "addi $t1, $zero, 2");

        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 2 命令",
                MessageType.SUCCESS,
                true,
                2,
                true);

        List<ProgramLineView> lineViews = viewModel.getProgramLineViews();

        assertEquals(2, lineViews.size());

        assertEquals(0, lineViews.get(0).getLineNumber());
        assertEquals(0, lineViews.get(0).getPc());
        assertEquals("addi $t0, $zero, 1", lineViews.get(0).getText());
        assertTrue(lineViews.get(0).isInstructionLine());

        assertEquals(1, lineViews.get(1).getLineNumber());
        assertEquals(1, lineViews.get(1).getPc());
        assertEquals("addi $t1, $zero, 2", lineViews.get(1).getText());
        assertTrue(lineViews.get(1).isInstructionLine());
    }

    /**
     * プログラム一覧表示用の行情報では、
     * 複数のラベル行があっても命令行のPCがずれないことを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateProgramLineViewsSkippingMultipleLabelLines() {
        String programText = String.join(System.lineSeparator(),
                "start:",
                "addi $t0, $zero, 1",
                "loop:",
                "addi $t0, $t0, 1",
                "end:",
                "nop");

        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 3 命令",
                MessageType.SUCCESS,
                true,
                3,
                true);

        List<ProgramLineView> lineViews = viewModel.getProgramLineViews();

        assertEquals(6, lineViews.size());

        assertNull(lineViews.get(0).getPc());
        assertFalse(lineViews.get(0).isInstructionLine());

        assertEquals(0, lineViews.get(1).getPc());
        assertTrue(lineViews.get(1).isInstructionLine());

        assertNull(lineViews.get(2).getPc());
        assertFalse(lineViews.get(2).isInstructionLine());

        assertEquals(1, lineViews.get(3).getPc());
        assertTrue(lineViews.get(3).isInstructionLine());

        assertNull(lineViews.get(4).getPc());
        assertFalse(lineViews.get(4).isInstructionLine());

        assertEquals(2, lineViews.get(5).getPc());
        assertTrue(lineViews.get(5).isInstructionLine());
    }

    /**
     * プログラム一覧表示用の行情報では、
     * Serviceで空行が除外されたあとも、ラベル行と命令行のPC対応がずれないことを確認する。
     */
    @Test
    void createParsedViewModel_shouldCreateProgramLineViewsWithoutEmptyLinesAndSkippingLabels() {
        String programText = String.join(System.lineSeparator(),
                "",
                "start:",
                "",
                "addi $t0, $zero, 1",
                "    ",
                "loop:",
                "addi $t0, $t0, 1",
                "bne $t0, $t1, loop");

        List<String> programLines = service.splitLines(programText);

        MipsViewModel viewModel = factory.createParsedViewModel(
                programText,
                programLines,
                "パース成功: 3 命令",
                MessageType.SUCCESS,
                true,
                3,
                true);

        List<ProgramLineView> lineViews = viewModel.getProgramLineViews();

        assertEquals(5, lineViews.size());

        assertEquals(0, lineViews.get(0).getLineNumber());
        assertNull(lineViews.get(0).getPc());
        assertEquals("start:", lineViews.get(0).getText());
        assertFalse(lineViews.get(0).isInstructionLine());

        assertEquals(1, lineViews.get(1).getLineNumber());
        assertEquals(0, lineViews.get(1).getPc());
        assertEquals("addi $t0, $zero, 1", lineViews.get(1).getText());
        assertTrue(lineViews.get(1).isInstructionLine());

        assertEquals(2, lineViews.get(2).getLineNumber());
        assertNull(lineViews.get(2).getPc());
        assertEquals("loop:", lineViews.get(2).getText());
        assertFalse(lineViews.get(2).isInstructionLine());

        assertEquals(3, lineViews.get(3).getLineNumber());
        assertEquals(1, lineViews.get(3).getPc());
        assertEquals("addi $t0, $t0, 1", lineViews.get(3).getText());
        assertTrue(lineViews.get(3).isInstructionLine());

        assertEquals(4, lineViews.get(4).getLineNumber());
        assertEquals(2, lineViews.get(4).getPc());
        assertEquals("bne $t0, $t1, loop", lineViews.get(4).getText());
        assertTrue(lineViews.get(4).isInstructionLine());
    }

    /**
     * クリア後のViewModelでは、
     * プログラム一覧表示用の行情報が空になることを確認する。
     */
    @Test
    void createClearedViewModel_shouldCreateEmptyProgramLineViews() {
        MipsViewModel viewModel = factory.createClearedViewModel("入力欄をクリアしました。");

        assertEquals("", viewModel.getProgramText());
        assertTrue(viewModel.getProgramLines().isEmpty());
        assertTrue(viewModel.getProgramLineViews().isEmpty());
        assertEquals(32, viewModel.getRegisterValues().size());
        assertEquals(2, viewModel.getHiLoValues().size());
        assertEquals(96, viewModel.getMemoryValues().size());
    }

}