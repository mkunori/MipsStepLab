package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MfhiInstructionクラスのテスト。
 */
class MfhiInstructionTest {

    /**
     * HIの値をmfhi命令で読み出せる。
     */
    @Test
    void mfhi命令でHIの値をレジスタへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setHi(123);

        MfhiInstruction instruction = new MfhiInstruction(8);
        instruction.execute(cpu);

        assertEquals(123, cpu.getRegister(8));
    }
}