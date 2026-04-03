package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * AddInstructionクラスのテスト。
 */
class AddInstructionTest {

    /**
     * 2つのレジスタの値を加算して結果レジスタへ格納することを確認する。
     */
    @Test
    void executeで2つのレジスタ値が加算される() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 20);

        AddInstruction instruction = new AddInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(30, cpu.getRegister(10));
    }
}