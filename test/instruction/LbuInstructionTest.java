package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LbuInstructionクラスのテスト。
 */
class LbuInstructionTest {

    /**
     * バイト単位で読み込んでレジスタへ格納できることを確認する。
     */
    @Test
    void バイト単位で読み込んでレジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 5);

        LbuInstruction instruction = new LbuInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(9));
    }

    /**
     * 負のbyte値もゼロ拡張して読み込めることを確認する。
     */
    @Test
    void 負のbyte値をゼロ拡張して読み込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 0xFF);

        LbuInstruction instruction = new LbuInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(255, cpu.getRegister(9));
    }
}