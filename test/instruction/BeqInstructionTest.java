package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * BeqInstructionクラスのテスト。
 */
class BeqInstructionTest {

    /**
     * 比較する2つのレジスタ値が等しいとき、PCが分岐先へ変更されることを確認する。
     */
    @Test
    void executeで条件成立時に分岐する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 10);
        cpu.setPc(3);

        BeqInstruction instruction = new BeqInstruction(8, 9, 7);

        instruction.execute(cpu);

        assertEquals(7, cpu.getPc());
    }

    /**
     * 比較する2つのレジスタ値が等しくないとき、PCが変更されないことを確認する。
     */
    @Test
    void executeで条件不成立時は分岐しない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 20);
        cpu.setPc(3);

        BeqInstruction instruction = new BeqInstruction(8, 9, 7);

        instruction.execute(cpu);

        assertEquals(3, cpu.getPc());
    }
}