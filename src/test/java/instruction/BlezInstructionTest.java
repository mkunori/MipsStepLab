package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * BlezInstructionクラスのテスト。
 */
class BlezInstructionTest {

    /**
     * レジスタの値が0以下の場合、分岐することを確認する。
     */
    @Test
    void ゼロ以下なら分岐する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);

        BlezInstruction instruction = new BlezInstruction(8, 5);
        instruction.execute(cpu);

        assertEquals(5, cpu.getPc());
    }

    /**
     * レジスタの値が正の場合、分岐しないことを確認する。
     */
    @Test
    void 正の値なら分岐しない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);

        BlezInstruction instruction = new BlezInstruction(8, 5);
        instruction.execute(cpu);

        assertEquals(0, cpu.getPc());
    }
}