package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SltiuInstructionクラスのテスト。
 */
class SltiuInstructionTest {

    /**
     * レジスタ値が即値より小さい場合、1が格納されることを確認する。
     */
    @Test
    void unsgined比較でレジスタ値が即値より小さいなら1になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        SltiuInstruction instruction = new SltiuInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(9));
    }

    /**
     * レジスタ値が即値以上の場合、0が格納されることを確認する。
     */
    @Test
    void unsgined比較でレジスタ値が即値以上なら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 5);

        SltiuInstruction instruction = new SltiuInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }

    /**
     * 負数は大きい正値として比較されることを確認する。
     */
    @Test
    void unsigned即値比較で負数は大きい値として扱われる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        SltiuInstruction instruction = new SltiuInstruction(10, 8, 1);
        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }
}