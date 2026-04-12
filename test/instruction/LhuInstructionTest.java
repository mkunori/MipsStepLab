package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LhuInstructionクラスのテスト。
 */
class LhuInstructionTest {

    /**
     * ハーフワード単位で読み込んでレジスタへ格納できることを確認する。
     */
    @Test
    void ハーフワード単位で読み込んでレジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0x1234);

        LhuInstruction instruction = new LhuInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(0x1234, cpu.getRegister(9));
    }

    /**
     * 負のhalfword値もゼロ拡張して読み込めることを確認する。
     */
    @Test
    void 負のhalfword値をゼロ拡張して読み込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0xFFFF);

        LhuInstruction instruction = new LhuInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(65535, cpu.getRegister(9));
    }
}