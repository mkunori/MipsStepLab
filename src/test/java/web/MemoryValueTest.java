package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * MemoryValueのテストクラス。
 *
 * メモリ現在値の10進数表示用データと16進数表示用データを確認する。
 */
class MemoryValueTest {

    /**
     * 現在値を符号なし整数として取得できることを確認する。
     */
    @Test
    void getUnsignedValue_shouldReturnUnsignedByteValue() {
        MemoryValue zero = new MemoryValue(0, (byte) 0, false);
        MemoryValue positive = new MemoryValue(1, (byte) 127, true);
        MemoryValue negative = new MemoryValue(2, (byte) 0xFF, true);

        assertEquals(0, zero.getUnsignedValue());
        assertEquals(127, positive.getUnsignedValue());
        assertEquals(255, negative.getUnsignedValue());
        assertFalse(zero.isChanged());
        assertTrue(positive.isChanged());
    }

    /**
     * 現在値を0x00形式の16進数文字列として取得できることを確認する。
     */
    @Test
    void getHexValue_shouldReturnTwoDigitHexString() {
        MemoryValue value = new MemoryValue(0, (byte) 15, false);
        MemoryValue maxValue = new MemoryValue(1, (byte) 0xFF, false);

        assertEquals("0x0F", value.getHexValue());
        assertEquals("0xFF", maxValue.getHexValue());
    }

    /**
     * アドレスを0x0000形式の16進数文字列として取得できることを確認する。
     */
    @Test
    void getHexAddress_shouldReturnFourDigitHexString() {
        MemoryValue value = new MemoryValue(16, (byte) 0, false);

        assertEquals("0x0010", value.getHexAddress());
    }
}
