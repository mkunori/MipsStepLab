package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SubInstructionクラスのテスト。
 */
class SubInstructionTest {

    /**
     * 2つのレジスタの値を減算して、結果レジスタへ格納することを確認する。
     */
    @Test
    void executeで2つのレジスタ値を減算できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 30);
        cpu.setRegister(9, 12);

        SubInstruction instruction = new SubInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(18, cpu.getRegister(10));
    }

    /**
     * 減算結果が負の値になる場合も正しく格納できることを確認する。
     */
    @Test
    void executeで負の結果も格納できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 20);

        SubInstruction instruction = new SubInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(-10, cpu.getRegister(10));
    }
}