package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SltiInstructionクラスのテスト。
 */
class SltiInstructionTest {

    /**
     * レジスタ値が即値より小さい場合、1が格納されることを確認する。
     */
    @Test
    void executeでレジスタ値が即値より小さいなら1になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        SltiInstruction instruction = new SltiInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(9));
    }

    /**
     * レジスタ値が即値以上の場合、0が格納されることを確認する。
     */
    @Test
    void executeでレジスタ値が即値以上なら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 5);

        SltiInstruction instruction = new SltiInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }

    /**
     * 負の即値との比較も正しく行えることを確認する。
     */
    @Test
    void executeで負の即値も比較できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -10);

        SltiInstruction instruction = new SltiInstruction(9, 8, -3);

        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(9));
    }
}