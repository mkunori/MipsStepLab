package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SltInstructionクラスのテスト。
 */
class SltInstructionTest {

    /**
     * 左辺が右辺より小さい場合、1が格納されることを確認する。
     */
    @Test
    void executeで左辺が小さいなら1になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);
        cpu.setRegister(9, 5);

        SltInstruction instruction = new SltInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(10));
    }

    /**
     * 左辺が右辺以上の場合、0が格納されることを確認する。
     */
    @Test
    void executeで左辺が小さくないなら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 5);
        cpu.setRegister(9, 5);

        SltInstruction instruction = new SltInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }

    /**
     * 負の値を含む比較も正しく行えることを確認する。
     */
    @Test
    void executeで負の値も比較できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -3);
        cpu.setRegister(9, 2);

        SltInstruction instruction = new SltInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(10));
    }
}