package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MulInstructionクラスのテスト。
 */
class MulInstructionTest {

    /**
     * 乗算結果がLOに格納されることを確認する。
     */
    @Test
    void 乗算結果をレジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        MulInstruction instruction = new MulInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(42, cpu.getRegister(10));
        assertEquals(0, cpu.getHi());
        assertEquals(42, cpu.getLo());
    }

    /**
     * 負数を含む乗算結果をHIとLOへ格納できることを確認する。
     */
    @Test
    void 負数を含む乗算ができる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -3);
        cpu.setRegister(9, 5);

        MulInstruction instruction = new MulInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(-15, cpu.getRegister(10));
        assertEquals(-1, cpu.getHi());
        assertEquals(-15, cpu.getLo());
    }
}