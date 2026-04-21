package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SwInstructionクラスのテスト。
 */
class SwInstructionTest {

    /**
     * レジスタの値が指定メモリ位置へ書き込まれることを確認する。
     */
    @Test
    void executeでレジスタ値がメモリへ書き込まれる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 123);
        cpu.setRegister(9, 10); // ベースアドレス

        SwInstruction instruction = new SwInstruction(8, 0, 9);

        instruction.execute(cpu);

        assertEquals(123, cpu.loadWord(10));
    }
}