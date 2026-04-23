package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * MthiInstructionクラスのテスト。
 */
class MthiInstructionTest {

    /**
     * レジスタの値をHIへコピーできることを確認する。
     */
    @Test
    void mthi命令でレジスタの値をHIへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 123);

        MthiInstruction instruction = new MthiInstruction(8);
        instruction.execute(cpu);

        assertEquals(123, cpu.getHi());
    }

    /**
     * 負の値もHIへコピーできることを確認する。
     */
    @Test
    void mthi命令で負の値をHIへコピーできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        MthiInstruction instruction = new MthiInstruction(8);
        instruction.execute(cpu);

        assertEquals(-1, cpu.getHi());
    }
}