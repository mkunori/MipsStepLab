package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * WebMipsSessionServiceのテストクラス。
 *
 * Web版MipsStepLabにおける、プログラム解析・実行状態作成・入力制限などの
 * 基本動作を確認する。
 */
class WebMipsSessionServiceTest {

    /** テスト対象のService。 */
    private WebMipsSessionService service;

    /**
     * 各テストの前に、テスト対象のServiceを生成する。
     */
    @BeforeEach
    void setUp() {
        service = new WebMipsSessionService();
    }

    /**
     * 正常なMIPSプログラムからWebMipsSessionを作成できることを確認する。
     */
    @Test
    void createSession_shouldCreateSession_whenProgramIsValid() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "add $t2, $t0, $t1");

        WebMipsSession session = service.createSession(programText);

        assertNotNull(session);
        assertEquals(programText, session.getProgramText());
        assertEquals(3, session.getProgram().size());
    }

    /**
     * 空入力の場合、例外になることを確認する。
     */
    @Test
    void createSession_shouldThrowException_whenProgramIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(""));

        assertEquals("プログラムを入力してください。", exception.getMessage());
    }

    /**
     * 最大行数を超えた場合、例外になることを確認する。
     */
    @Test
    void createSession_shouldThrowException_whenLineCountIsTooLarge() {
        StringBuilder programText = new StringBuilder();

        for (int i = 0; i < 201; i++) {
            programText.append("nop").append(System.lineSeparator());
        }

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(programText.toString()));

        assertEquals("プログラムの行数が多すぎます。最大 200 行までです。", exception.getMessage());
    }

    /**
     * 1行の文字数が上限を超えた場合、例外になることを確認する。
     */
    @Test
    void createSession_shouldThrowException_whenLineLengthIsTooLarge() {
        String tooLongLine = "a".repeat(201);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(tooLongLine));

        assertEquals("プログラムの 1 行目が長すぎます。1行は最大 200 文字までです。",
                exception.getMessage());
    }

    /**
     * 入力全体の文字数が上限を超えた場合、例外になることを確認する。
     */
    @Test
    void createSession_shouldThrowException_whenProgramTextIsTooLarge() {
        String tooLongProgram = "a".repeat(10_001);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(tooLongProgram));

        assertEquals("プログラムが長すぎます。最大 10000 文字までです。",
                exception.getMessage());
    }

    /**
     * stepを実行すると、1命令だけ実行されることを確認する。
     */
    @Test
    void step_shouldExecuteOneInstruction() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        service.step(session);

        assertEquals(5, session.getCpu().getRegister(8));
        assertEquals(1, session.getStepRunner().getPc());
    }

    /**
     * stepを実行すると、実行したPCが実行済みPCとして記録されることを確認する。
     */
    @Test
    void step_shouldMarkExecutedPc() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        service.step(session);

        assertTrue(session.getExecutedPcs().contains(0));
    }

    /**
     * 実行できる命令が残っている場合、canStepがtrueを返すことを確認する。
     */
    @Test
    void canStep_shouldReturnTrue_whenInstructionRemains() {
        String programText = "nop";

        WebMipsSession session = service.createSession(programText);

        assertTrue(service.canStep(session));
    }

    /**
     * 最後まで実行した場合、canStepがfalseを返すことを確認する。
     */
    @Test
    void canStep_shouldReturnFalse_whenProgramFinished() {
        String programText = "nop";

        WebMipsSession session = service.createSession(programText);

        service.step(session);

        assertEquals(false, service.canStep(session));
    }

    /**
     * 有効なPC番号を指定すると、ブレークポイントを追加できることを確認する。
     */
    @Test
    void addBreakpoint_shouldAddBreakpoint_whenPcIsValid() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        service.addBreakpoint(session, 1);

        assertTrue(service.getBreakpoints(session).contains(1));
    }

    /**
     * 範囲外のPC番号を指定すると、ブレークポイント追加で例外になることを確認する。
     */
    @Test
    void addBreakpoint_shouldThrowException_whenPcIsOutOfRange() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addBreakpoint(session, 2));

        assertEquals("PCがプログラム範囲外です: 2", exception.getMessage());
    }

    /**
     * 登録済みのブレークポイントを削除できることを確認する。
     */
    @Test
    void removeBreakpoint_shouldReturnTrue_whenBreakpointExists() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 1);

        boolean removed = service.removeBreakpoint(session, 1);

        assertTrue(removed);
        assertFalse(service.getBreakpoints(session).contains(1));
    }

    /**
     * 未登録のブレークポイントを削除しようとするとfalseを返すことを確認する。
     */
    @Test
    void removeBreakpoint_shouldReturnFalse_whenBreakpointDoesNotExist() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);

        boolean removed = service.removeBreakpoint(session, 1);

        assertFalse(removed);
    }

    /**
     * 負のPC番号を指定すると、ブレークポイント追加で例外になることを確認する。
     */
    @Test
    void addBreakpoint_shouldThrowException_whenPcIsNegative() {
        String programText = "nop";

        WebMipsSession session = service.createSession(programText);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.addBreakpoint(session, -1));

        assertEquals("PCがプログラム範囲外です: -1", exception.getMessage());
    }

    /**
     * runUntilBreakpointを実行すると、ブレークポイントの直前まで連続実行されることを確認する。
     *
     * PC 2にブレークポイントを設定した場合、
     * PC 0とPC 1は実行され、PC 2の命令はまだ実行されない。
     */
    @Test
    void runUntilBreakpoint_shouldStopAtBreakpoint() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "add $t2, $t0, $t1");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 2);

        RunResult result = service.runUntilBreakpoint(session);

        assertEquals(2, result.getExecutedStepCount());
        assertEquals("ブレークポイントに到達しました: PC 2", result.getMessage());

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
     * 命令を実行せずに停止することを確認する。
     */
    @Test
    void runUntilBreakpoint_shouldStopWithoutExecution_whenCurrentPcIsBreakpoint() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3");

        WebMipsSession session = service.createSession(programText);
        service.addBreakpoint(session, 0);

        RunResult result = service.runUntilBreakpoint(session);

        assertEquals(0, result.getExecutedStepCount());
        assertEquals("現在のPCがブレークポイントです: PC 0", result.getMessage());

        assertEquals(0, session.getCpu().getRegister(8));
        assertEquals(0, session.getStepRunner().getPc());
        assertTrue(session.getExecutedPcs().isEmpty());
    }

    /**
     * ブレークポイントが設定されていない場合、
     * プログラム終了まで連続実行されることを確認する。
     */
    @Test
    void runUntilBreakpoint_shouldRunUntilProgramFinished_whenNoBreakpointExists() {
        String programText = String.join(System.lineSeparator(),
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 3",
                "add $t2, $t0, $t1");

        WebMipsSession session = service.createSession(programText);

        RunResult result = service.runUntilBreakpoint(session);

        assertEquals(3, result.getExecutedStepCount());
        assertEquals("プログラムが終了しました。", result.getMessage());

        assertEquals(5, session.getCpu().getRegister(8));
        assertEquals(3, session.getCpu().getRegister(9));
        assertEquals(8, session.getCpu().getRegister(10));

        assertFalse(service.canStep(session));
        assertTrue(session.getExecutedPcs().contains(0));
        assertTrue(session.getExecutedPcs().contains(1));
        assertTrue(session.getExecutedPcs().contains(2));
    }

    /**
     * 無限ループするプログラムでも、最大実行ステップ数で停止することを確認する。
     */
    @Test
    void runUntilBreakpoint_shouldStop_whenMaxRunStepsReached() {
        String programText = String.join(System.lineSeparator(),
                "loop:",
                "addi $t0, $t0, 1",
                "j loop");

        WebMipsSession session = service.createSession(programText);

        RunResult result = service.runUntilBreakpoint(session);

        assertEquals(1000, result.getExecutedStepCount());
        assertEquals("最大実行ステップ数に到達したため停止しました。", result.getMessage());

        assertTrue(service.canStep(session));
    }
}