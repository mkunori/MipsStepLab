package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * JumpInstructionクラスのテスト。
 */
class JumpInstructionTest {

    /**
     * executeで指定したPCへジャンプすることを確認する。
     */
    @Test
    void executeで指定したpcへジャンプする() {
        Cpu cpu = new Cpu();
        cpu.setPc(2);

        JumpInstruction instruction = new JumpInstruction(8);

        instruction.execute(cpu);

        assertEquals(8, cpu.getPc());
    }
}