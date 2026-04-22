package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * BltzInstructionクラスのテスト。
 */
class BltzInstructionTest {

    /**
     * レジスタの値が負の場合、分岐することを確認する。
     */
    @Test
    void 負数なら分岐する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        BltzInstruction instruction = new BltzInstruction(8, 5);
        instruction.execute(cpu);

        assertEquals(5, cpu.getPc());
    }

    /**
     * レジスタの値が0以上の場合、分岐しないことを確認する。
     */
    @Test
    void ゼロ以上なら分岐しない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);

        BltzInstruction instruction = new BltzInstruction(8, 5);
        instruction.execute(cpu);

        assertEquals(0, cpu.getPc());
    }
}