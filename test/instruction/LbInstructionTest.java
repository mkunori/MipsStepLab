package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LbInstructionクラスのテスト。
 */
class LbInstructionTest {

    /**
     * 指定メモリ位置の値がレジスタへ読み込まれることを確認する。
     */
    @Test
    void バイト単位で読み込んでレジスタへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100); // $t0
        cpu.storeByte(100, 5);

        LbInstruction instruction = new LbInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(9));
    }

    /**
     * 負の値も読み込めることを確認する。
     */
    @Test
    void 負の値を符号拡張して読み込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 0xFF);

        LbInstruction instruction = new LbInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(-1, cpu.getRegister(9));
    }
}