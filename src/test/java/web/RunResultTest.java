package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * RunResultのテストクラス。
 */
class RunResultTest {

    /**
     * 停止理由とメッセージを保持できることを確認する。
     */
    @Test
    void constructor_shouldStoreValues() {
        RunResult result = new RunResult(
                null,
                10,
                RunStopReason.BREAKPOINT_REACHED,
                "ブレークポイントに到達しました: PC 2");

        assertNull(result.getLastStepResult());
        assertEquals(10, result.getExecutedStepCount());
        assertEquals(RunStopReason.BREAKPOINT_REACHED, result.getStopReason());
        assertEquals("ブレークポイントに到達しました: PC 2", result.getMessage());
        assertFalse(result.isMaxStepsReached());
    }

    /**
     * 最大実行ステップ数到達の場合、isMaxStepsReachedがtrueを返すことを確認する。
     */
    @Test
    void isMaxStepsReached_shouldReturnTrue_whenStopReasonIsMaxStepsReached() {
        RunResult result = new RunResult(
                null,
                1000,
                RunStopReason.MAX_STEPS_REACHED,
                "最大実行ステップ数に到達したため停止しました。");

        assertTrue(result.isMaxStepsReached());
    }
}
