package web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * MemoryDiffのテストクラス。
 *
 * メモリ差分の10進数表示用データと16進数表示用データを確認する。
 */
class MemoryDiffTest {

    /**
     * 実行前後の値を符号なし整数として取得できることを確認する。
     */
    @Test
    void getUnsignedValues_shouldReturnUnsignedByteValues() {
        MemoryDiff diff = new MemoryDiff(0, (byte) 0x80, (byte) 0xFF);

        assertEquals(128, diff.getUnsignedBeforeValue());
        assertEquals(255, diff.getUnsignedAfterValue());
    }

    /**
     * 実行前後の値を0x00形式の16進数文字列として取得できることを確認する。
     */
    @Test
    void getHexValues_shouldReturnTwoDigitHexStrings() {
        MemoryDiff diff = new MemoryDiff(0, (byte) 10, (byte) 255);

        assertEquals("0x0A", diff.getHexBeforeValue());
        assertEquals("0xFF", diff.getHexAfterValue());
    }

    /**
     * アドレスを0x0000形式の16進数文字列として取得できることを確認する。
     */
    @Test
    void getHexAddress_shouldReturnFourDigitHexString() {
        MemoryDiff diff = new MemoryDiff(32, (byte) 0, (byte) 1);

        assertEquals("0x0020", diff.getHexAddress());
    }
}
