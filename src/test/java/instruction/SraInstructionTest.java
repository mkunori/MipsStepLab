package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SraInstructionクラスのテスト。
 */
class SraInstructionTest {

    /**
     * 正の値の算術右シフトが正しく行われることを確認する。
     */
    @Test
    void executeで正の値を算術右シフトできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);

        SraInstruction instruction = new SraInstruction(9, 8, 2);

        instruction.execute(cpu);

        assertEquals(3, cpu.getRegister(9));
    }

    /**
     * 負の値でも符号が維持されることを確認する。
     */
    @Test
    void executeで負の値は符号ビットで埋められる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8);

        SraInstruction instruction = new SraInstruction(9, 8, 2);

        instruction.execute(cpu);

        assertEquals(-2, cpu.getRegister(9));
    }

    /**
     * シフト量が0の場合、値が変わらないことを確認する。
     */
    @Test
    void executeでシフト量0なら値は変わらない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);

        SraInstruction instruction = new SraInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(9));
    }
}