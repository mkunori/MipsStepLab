package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MultInstructionクラスのテスト。
 */
class MultInstructionTest {

    /**
     * 乗算結果がLOに格納されることを確認する。
     */
    @Test
    void 乗算結果をHIとLOへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        MultInstruction instruction = new MultInstruction(8, 9);
        instruction.execute(cpu);

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

        MultInstruction instruction = new MultInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(-1, cpu.getHi());
        assertEquals(-15, cpu.getLo());
    }

    /**
     * 64ビット結果の上位32ビットがHIへ入ることを確認する。
     */
    @Test
    void 上位32ビットがHIへ入る() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0x7FFFFFFF);
        cpu.setRegister(9, 2);

        MultInstruction instruction = new MultInstruction(8, 9);
        instruction.execute(cpu);

        long result = (long) 0x7FFFFFFF * 2L;
        assertEquals((int) (result >>> 32), cpu.getHi());
        assertEquals((int) result, cpu.getLo());
    }
}