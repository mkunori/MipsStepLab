package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LhInstructionクラスのテスト。
 */
class LhInstructionTest {

    /**
     * 指定メモリ位置の値がレジスタへ読み込まれることを確認する。
     */
    @Test
    void ハーフワード単位で読み込んでレジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100); // $t0
        cpu.storeHalfWord(100, 0x1234);

        LhInstruction instruction = new LhInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(0x1234, cpu.getRegister(9));
    }

    /**
     * 負の値も読み込めることを確認する。
     */
    @Test
    void 負のhalfwordを符号拡張して読み込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0xFFFF);

        LhInstruction instruction = new LhInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(-1, cpu.getRegister(9));
    }
}