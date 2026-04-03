package cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Cpuクラスのテスト。
 */
class CpuTest {

    /**
     * レジスタに値を書き込み、読み出せることを確認する。
     */
    @Test
    void setRegisterで書き込んだ値をgetRegisterで取得できる() {
        Cpu cpu = new Cpu();

        cpu.setRegister(8, 123);

        assertEquals(123, cpu.getRegister(8));
    }

    /**
     * $zeroレジスタは書き換えできないことを確認する。
     */
    @Test
    void zeroレジスタは常に0のままである() {
        Cpu cpu = new Cpu();

        cpu.setRegister(0, 999);

        assertEquals(0, cpu.getRegister(0));
    }

    /**
     * メモリへ値を書き込み、読み出せることを確認する。
     */
    @Test
    void storeWordで書いた値をloadWordで取得できる() {
        Cpu cpu = new Cpu();

        cpu.storeWord(10, 456);

        assertEquals(456, cpu.loadWord(10));
    }

    /**
     * 負のPCは設定できないことを確認する。
     */
    @Test
    void setPcに負の値を渡すと例外が発生する() {
        Cpu cpu = new Cpu();

        assertThrows(IllegalArgumentException.class, () -> cpu.setPc(-1));
    }
}