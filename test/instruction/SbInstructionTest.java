package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SbInstructionクラスのテスト。
 */
class SbInstructionTest {

    /**
     * レジスタの値が指定メモリ位置へ書き込まれることを確認する。
     */
    @Test
    void レジスタの下位8ビットを1バイト書き込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100); // ベース
        cpu.setRegister(9, 0x1234ABCD);

        SbInstruction instruction = new SbInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(0xCD, cpu.loadByte(100) & 0xFF);
    }

    /**
     * レジスタの値が指定メモリ位置へオフセット付きで書き込まれることを確認する。
     */
    @Test
    void オフセット付きで1バイト書き込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0x7F);

        SbInstruction instruction = new SbInstruction(9, 2, 8);
        instruction.execute(cpu);

        assertEquals(0x7F, cpu.loadByte(102) & 0xFF);
    }
}