package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * ShInstructionクラスのテスト。
 */
class ShInstructionTest {

    /**
     * レジスタの値が指定メモリ位置へ書き込まれることを確認する。
     */
    @Test
    void レジスタの下位16ビットを2バイト書き込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100); // ベース
        cpu.setRegister(9, 0x1234ABCD);

        ShInstruction instruction = new ShInstruction(9, 0, 8);
        instruction.execute(cpu);

        assertEquals(0xABCD, cpu.loadHalfWord(100) & 0xFFFF);
    }

    /**
     * レジスタの値が指定メモリ位置へオフセット付きで書き込まれることを確認する。
     */
    @Test
    void オフセット付きで2バイト書き込む() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0x7FFF);

        ShInstruction instruction = new ShInstruction(9, 2, 8);
        instruction.execute(cpu);

        assertEquals(0x7FFF, cpu.loadHalfWord(102) & 0xFFFF);
    }
}