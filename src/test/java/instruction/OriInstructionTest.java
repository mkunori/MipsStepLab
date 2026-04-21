package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * OriInstructionクラスのテスト。
 */
class OriInstructionTest {

    /**
     * レジスタ値と即値のOR演算が正しく実行されることを確認する。
     */
    @Test
    void executeでレジスタと即値のORを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100

        OriInstruction instruction = new OriInstruction(9, 8, 10); // 1010

        instruction.execute(cpu);

        assertEquals(14, cpu.getRegister(9)); // 1110
    }

    /**
     * 即値が0の場合、元の値がそのまま結果になることを確認する。
     */
    @Test
    void executeで即値が0なら元の値になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 5);

        OriInstruction instruction = new OriInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(9));
    }

    /**
     * すでに立っているビットに対してORしても値が変わらないことを確認する。
     */
    @Test
    void executeで既に1のビットはそのままになる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100

        OriInstruction instruction = new OriInstruction(9, 8, 4); // 0100

        instruction.execute(cpu);

        assertEquals(12, cpu.getRegister(9)); // 1100
    }
}