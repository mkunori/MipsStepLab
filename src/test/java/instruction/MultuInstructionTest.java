package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MultuInstructionクラスのテスト。
 */
class MultuInstructionTest {

    /**
     * 符号なし乗算結果がHIとLOへ格納されることを確認する。
     */
    @Test
    void 符号なし乗算結果をHIとLOへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        MultuInstruction instruction = new MultuInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(0, cpu.getHi());
        assertEquals(42, cpu.getLo());
    }

    /**
     * 符号付きでは負数に見える値も、符号なしとして乗算できることを確認する。
     */
    @Test
    void 符号なしとして乗算できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1); // unsigned では 0xFFFFFFFF
        cpu.setRegister(9, 2);

        MultuInstruction instruction = new MultuInstruction(8, 9);
        instruction.execute(cpu);

        long result = Integer.toUnsignedLong(-1) * 2L;
        assertEquals((int) (result >>> 32), cpu.getHi());
        assertEquals((int) result, cpu.getLo());
    }
}