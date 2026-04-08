package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SllInstructionクラスのテスト。
 */
class SllInstructionTest {

    /**
     * 左シフトが正しく実行されることを確認する。
     */
    @Test
    void executeで左シフトできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        SllInstruction instruction = new SllInstruction(9, 8, 2);

        instruction.execute(cpu);

        assertEquals(12, cpu.getRegister(9));
    }

    /**
     * シフト量が0の場合、値が変わらないことを確認する。
     */
    @Test
    void executeでシフト量0なら値は変わらない() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);

        SllInstruction instruction = new SllInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(9));
    }

    /**
     * 0をシフトしても結果は0になることを確認する。
     */
    @Test
    void executeで0はシフトしても0() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);

        SllInstruction instruction = new SllInstruction(9, 8, 5);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }
}