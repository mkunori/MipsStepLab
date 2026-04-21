package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MfloInstructionクラスのテスト。
 */
class MfloInstructionTest {

    /**
     * LOの値をmflo命令で読み出せる。
     */
    @Test
    void mflo命令でLOの値をレジスタへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setLo(456);

        MfloInstruction instruction = new MfloInstruction(8);
        instruction.execute(cpu);

        assertEquals(456, cpu.getRegister(8));
    }
}