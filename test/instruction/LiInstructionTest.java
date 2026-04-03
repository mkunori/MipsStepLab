package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LiInstructionクラスのテスト。
 */
class LiInstructionTest {

    /**
     * 即値が指定したレジスタへ設定されることを確認する。
     */
    @Test
    void executeで即値がレジスタに設定される() {
        Cpu cpu = new Cpu();
        LiInstruction instruction = new LiInstruction(8, 10);

        instruction.execute(cpu);

        assertEquals(10, cpu.getRegister(8));
    }
}