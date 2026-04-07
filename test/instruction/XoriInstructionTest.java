package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * XoriInstructionクラスのテスト。
 */
class XoriInstructionTest {

    /**
     * レジスタと即値のXOR演算が正しく実行されることを確認する。
     */
    @Test
    void executeでレジスタと即値のXORを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100

        XoriInstruction instruction = new XoriInstruction(9, 8, 10); // 1010

        instruction.execute(cpu);

        assertEquals(6, cpu.getRegister(9)); // 0110
    }

    /**
     * 同じ値同士のXORは0になることを確認する。
     */
    @Test
    void executeで同じ値同士なら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);

        XoriInstruction instruction = new XoriInstruction(9, 8, 7);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }

    /**
     * 即値が0の場合、元の値がそのまま結果になることを確認する。
     */
    @Test
    void executeで即値が0なら元の値になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 5);

        XoriInstruction instruction = new XoriInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(9));
    }
}