package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SltuInstructionクラスのテスト。
 */
class SltuInstructionTest {

    /**
     * 左辺が右辺より小さい場合、1が格納されることを確認する。
     */
    @Test
    void unsigned比較で左辺が小さいと1になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);
        cpu.setRegister(9, 2);

        SltuInstruction instruction = new SltuInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(1, cpu.getRegister(10));
    }

    /**
     * 左辺が右辺以上の場合、0が格納されることを確認する。
     */
    @Test
    void unsgined比較で左辺が小さくないなら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 2);
        cpu.setRegister(9, 2);

        SltuInstruction instruction = new SltuInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }

    /**
     * 負数は大きい正値として比較されることを確認する。
     */
    @Test
    void unsigned比較で負数は大きい値として扱われる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);
        cpu.setRegister(9, 1);

        SltuInstruction instruction = new SltuInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }
}