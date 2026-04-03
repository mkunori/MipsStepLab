package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LwInstructionクラスのテスト。
 */
class LwInstructionTest {

    /**
     * 指定メモリ位置の値がレジスタへ読み込まれることを確認する。
     */
    @Test
    void executeでメモリ値がレジスタへ読み込まれる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(9, 10); // ベースアドレス
        cpu.storeWord(10, 456);

        LwInstruction instruction = new LwInstruction(8, 0, 9);

        instruction.execute(cpu);

        assertEquals(456, cpu.getRegister(8));
    }
}