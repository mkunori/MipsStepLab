package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * RegisterValueのテストクラス。
 */
class RegisterValueTest {

    /**
     * レジスタ番号からMIPSの別名を取得できることを確認する。
     */
    @Test
    void getRegisterName_shouldReturnMipsRegisterAlias() {
        RegisterValue zero = new RegisterValue(0, 0, false);
        RegisterValue t0 = new RegisterValue(8, 5, true);
        RegisterValue s0 = new RegisterValue(16, 10, false);
        RegisterValue ra = new RegisterValue(31, 20, false);

        assertEquals("$zero", zero.getRegisterName());
        assertEquals("$t0", t0.getRegisterName());
        assertEquals("$s0", s0.getRegisterName());
        assertEquals("$ra", ra.getRegisterName());
    }

    /**
     * 範囲外のレジスタ番号では例外になることを確認する。
     */
    @Test
    void constructor_shouldThrowException_whenRegisterNumberIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterValue(-1, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new RegisterValue(32, 0, false));
    }
}
