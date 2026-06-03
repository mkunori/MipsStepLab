package web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * RegisterDiffのテストクラス。
 */
class RegisterDiffTest {

    /**
     * レジスタ番号からMIPSの別名を取得できることを確認する。
     */
    @Test
    void getRegisterName_shouldReturnMipsRegisterAlias() {
        RegisterDiff t0 = new RegisterDiff(8, 0, 5);
        RegisterDiff s0 = new RegisterDiff(16, 0, 10);
        RegisterDiff ra = new RegisterDiff(31, 0, 20);

        assertEquals("$t0", t0.getRegisterName());
        assertEquals("$s0", s0.getRegisterName());
        assertEquals("$ra", ra.getRegisterName());
    }

    /**
     * 範囲外のレジスタ番号では例外になることを確認する。
     */
    @Test
    void constructor_shouldThrowException_whenRegisterNumberIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new RegisterDiff(-1, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new RegisterDiff(32, 0, 1));
    }
}
