package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * JrInstructionクラスのテスト。
 */
class JrInstructionTest {

    /**
     * executeで指定レジスタの値へジャンプすることを確認する。
     */
    @Test
    void executeで指定レジスタの値へジャンプする() {
        Cpu cpu = new Cpu();
        cpu.setRegister(31, 6);

        JrInstruction instruction = new JrInstruction(31);

        instruction.execute(cpu);

        assertEquals(6, cpu.getPc());
    }
}