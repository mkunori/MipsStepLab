package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * ExecutedInstructionViewのテストクラス。
 */
class ExecutedInstructionViewTest {

    /**
     * コンストラクタで渡した値を取得できることを確認する。
     */
    @Test
    void constructor_shouldStoreValues() {
        ExecutedInstructionView view = new ExecutedInstructionView(
                3,
                2,
                5,
                "beq $t0, $t1, end");

        assertEquals(3, view.getStep());
        assertEquals(2, view.getPcBefore());
        assertEquals(5, view.getPcAfter());
        assertEquals("beq $t0, $t1, end", view.getInstructionText());
        assertTrue(view.hasInstructionText());
    }

    /**
     * 命令文字列にnullを渡した場合は空文字として扱うことを確認する。
     */
    @Test
    void constructor_shouldConvertNullInstructionTextToEmptyText() {
        ExecutedInstructionView view = new ExecutedInstructionView(
                1,
                0,
                1,
                null);

        assertEquals("", view.getInstructionText());
        assertFalse(view.hasInstructionText());
    }
}
