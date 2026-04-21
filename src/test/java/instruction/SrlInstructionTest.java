package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SrlInstructionクラスのテスト。
 */
class SrlInstructionTest {

    /**
     * 論理右シフトが正しく実行されることを確認する。
     */
    @Test
    void executeで論理右シフトできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);

        SrlInstruction instruction = new SrlInstruction(9, 8, 2);

        instruction.execute(cpu);

        assertEquals(3, cpu.getRegister(9));
    }

    /**
     * シフト量が0の場合、値が変わらないことを確認する。
     */
    @Test
    void executeでシフト量0なら値は変わらない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);

        SrlInstruction instruction = new SrlInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(9));
    }

    /**
     * 負の値でも論理右シフトでは左側が0埋めされることを確認する。
     */
    @Test
    void executeで負の値も論理右シフトできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8);

        SrlInstruction instruction = new SrlInstruction(9, 8, 2);

        instruction.execute(cpu);

        assertEquals(1073741822, cpu.getRegister(9));
    }
}