package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MtloInstructionクラスのテスト。
 */
class MtloInstructionTest {

    /**
     * レジスタの値をLOへコピーできることを確認する。
     */
    @Test
    void mtlo命令でレジスタの値をLOへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 456);

        MtloInstruction instruction = new MtloInstruction(8);
        instruction.execute(cpu);

        assertEquals(456, cpu.getLo());
    }

    /**
     * 負の値もLOへコピーできることを確認する。
     */
    @Test
    void mtlo命令で負の値をLOへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        MtloInstruction instruction = new MtloInstruction(8);
        instruction.execute(cpu);

        assertEquals(-1, cpu.getLo());
    }
}