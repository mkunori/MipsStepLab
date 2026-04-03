package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * BneInstructionクラスのテスト。
 */
class BneInstructionTest {

    /**
     * 比較する2つのレジスタ値が等しくないとき、PCが分岐先へ変更されることを確認する。
     */
    @Test
    void executeで条件成立時に分岐する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 20);
        cpu.setPc(4);

        BneInstruction instruction = new BneInstruction(8, 9, 9);

        instruction.execute(cpu);

        assertEquals(9, cpu.getPc());
    }

    /**
     * 比較する2つのレジスタ値が等しいとき、PCが変更されないことを確認する。
     */
    @Test
    void executeで条件不成立時は分岐しない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 10);
        cpu.setPc(4);

        BneInstruction instruction = new BneInstruction(8, 9, 9);

        instruction.execute(cpu);

        assertEquals(4, cpu.getPc());
    }
}