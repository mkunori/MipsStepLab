package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * RemInstructionクラスのテスト。
 */
class RemInstructionTest {

    /**
     * 余りを指定レジスタへ格納できることを確認する。
     */
    @Test
    void 余りを指定レジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        RemInstruction instruction = new RemInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(2, cpu.getRegister(10));
        assertEquals(3, cpu.getLo());
        assertEquals(2, cpu.getHi());
    }

    /**
     * 負数を含む余り計算ができることを確認する。
     */
    @Test
    void 負数を含む余り計算ができる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -20);
        cpu.setRegister(9, 6);

        RemInstruction instruction = new RemInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(-2, cpu.getRegister(10));
        assertEquals(-3, cpu.getLo());
        assertEquals(-2, cpu.getHi());
    }

    /**
     * 0割で例外が発生することを確認する。
     */
    @Test
    void ゼロ割で例外が発生する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 0);

        RemInstruction instruction = new RemInstruction(10, 8, 9);

        assertThrows(ArithmeticException.class, () -> {
            instruction.execute(cpu);
        });
    }
}