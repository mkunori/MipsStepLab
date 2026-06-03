package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import execution.StepResult;

/**
 * StepResultViewMapperのテストクラス。
 *
 * StepResultから、Web画面表示用のデータへ正しく変換できることを確認する。
 */
class StepResultViewMapperTest {

    /** Web版の実行状態を扱うService。 */
    private WebMipsSessionService service;

    /** テスト対象のMapper。 */
    private StepResultViewMapper mapper;

    /**
     * 各テストの前に、ServiceとMapperを生成する。
     */
    @BeforeEach
    void setUp() {
        service = new WebMipsSessionService();
        mapper = new StepResultViewMapper();
    }

    /**
     * レジスタ一覧作成時、今回変更されたレジスタだけchanged=trueになることを確認する。
     */
    @Test
    void createRegisterValues_shouldMarkChangedRegister() {
        String programText = "addi $t0, $zero, 5";
        WebMipsSession session = service.createSession(programText);

        StepResult result = service.step(session);

        List<RegisterValue> values = mapper.createRegisterValues(result);

        RegisterValue r8 = values.get(8);
        RegisterValue r9 = values.get(9);

        assertEquals(8, r8.getRegisterNumber());
        assertEquals(5, r8.getValue());
        assertTrue(r8.isChanged());

        assertEquals(9, r9.getRegisterNumber());
        assertEquals(0, r9.getValue());
        assertFalse(r9.isChanged());
    }

    /**
     * HI/LO一覧作成時、今回変更されたLOだけchanged=trueになることを確認する。
     */
    @Test
    void createHiLoValues_shouldMarkChangedHiLo() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "mult $t0, $t1");

        WebMipsSession session = service.createSession(programText);

        service.step(session);
        service.step(session);
        StepResult result = service.step(session);

        List<HiLoValue> values = mapper.createHiLoValues(result);

        HiLoValue hi = values.get(0);
        HiLoValue lo = values.get(1);

        assertEquals("HI", hi.getRegisterName());
        assertEquals(0, hi.getValue());
        assertFalse(hi.isChanged());

        assertEquals("LO", lo.getRegisterName());
        assertEquals(15, lo.getValue());
        assertTrue(lo.isChanged());
    }

    /**
     * メモリ一覧作成時、今回変更されたメモリアドレスだけchanged=trueになることを確認する。
     */
    @Test
    void createMemoryValues_shouldMarkChangedMemoryAddress() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "sw $t0, 0($zero)");

        WebMipsSession session = service.createSession(programText);

        service.step(session);
        StepResult result = service.step(session);

        List<MemoryValue> values = mapper.createMemoryValues(result);

        boolean existsChangedMemory = values.stream()
                .anyMatch(MemoryValue::isChanged);

        assertTrue(existsChangedMemory);
    }

    /**
     * 実行命令テキストをpcBeforeに対応する入力行から取得できることを確認する。
     */
    @Test
    void getExecutedInstructionText_shouldReturnLineAtPcBefore() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        StepResult result = service.step(session);
        List<String> programLines = service.splitLines(programText);

        String executedInstructionText = mapper.getExecutedInstructionText(result, programLines);

        assertEquals("addi $t0, $zero, 5", executedInstructionText);
    }

    /**
     * StepResultから実行命令表示用データを作成できることを確認する。
     */
    @Test
    void createExecutedInstructionView_shouldCreateViewData() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        StepResult result = service.step(session);
        List<String> programLines = service.splitLines(programText);

        ExecutedInstructionView view = mapper.createExecutedInstructionView(result, programLines);

        assertEquals(1, view.getStep());
        assertEquals(0, view.getPcBefore());
        assertEquals(1, view.getPcAfter());
        assertEquals("addi $t0, $zero, 5", view.getInstructionText());
        assertTrue(view.hasInstructionText());
    }

    /**
     * toViewDataで、StepResult由来の表示用データ一式を作成できることを確認する。
     */
    @Test
    void toViewData_shouldCreateViewData() {
        String programText = "addi $t0, $zero, 5";
        WebMipsSession session = service.createSession(programText);

        StepResult result = service.step(session);

        StepResultViewData viewData = mapper.toViewData(result);

        assertEquals(1, viewData.getRegisterDiffs().size());
        assertEquals(32, viewData.getRegisterValues().size());
        assertEquals(2, viewData.getHiLoValues().size());
        assertFalse(viewData.getMemoryValues().isEmpty());
    }
}