package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * AddiInstructionクラスのテスト。
 */
class AddiInstructionTest {

    /**
     * レジスタ値に即値を加算して、結果レジスタへ格納することを確認する。
     */
    @Test
    void executeでレジスタ値に即値を加算できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);

        AddiInstruction instruction = new AddiInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(15, cpu.getRegister(9));
    }

    /**
     * 負の即値を加算できることを確認する。
     */
    @Test
    void executeで負の即値を加算できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);

        AddiInstruction instruction = new AddiInstruction(9, 8, -3);

        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(9));
    }
}