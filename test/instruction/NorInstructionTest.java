package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * NorInstructionクラスのテスト。
 */
class NorInstructionTest {

    /**
     * OR演算結果をビット反転した値が格納されることを確認する。
     */
    @Test
    void executeでNOR演算を実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        NorInstruction instruction = new NorInstruction(10, 8, 9);

        instruction.execute(cpu);

        // ~(12 | 10) = ~14 = -15
        assertEquals(-15, cpu.getRegister(10));
    }

    /**
     * 両方0の場合、結果が-1になることを確認する。
     */
    @Test
    void executeで両方0なら結果はマイナス1になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setRegister(9, 0);

        NorInstruction instruction = new NorInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(-1, cpu.getRegister(10));
    }

    /**
     * 片方が0の場合、もう片方の値をORしたあと反転されることを確認する。
     */
    @Test
    void executeで片方が0の場合の結果を確認する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setRegister(9, 5);

        NorInstruction instruction = new NorInstruction(10, 8, 9);

        instruction.execute(cpu);

        // ~(0 | 5) = ~5 = -6
        assertEquals(-6, cpu.getRegister(10));
    }
}