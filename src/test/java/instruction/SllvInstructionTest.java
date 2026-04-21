package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

class SllvInstructionTest {

    /**
     * 左シフトが正しく実行されることを確認する。
     */
    @Test
    void レジスタで指定したビット数だけ左シフトする() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1); // $t0 = 値
        cpu.setRegister(9, 3); // $t1 = シフト量

        SllvInstruction instruction = new SllvInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(8, cpu.getRegister(10));
    }

    /**
     * シフト量が0の場合、値が変わらないことを確認する。
     */
    @Test
    void シフト量が0なら値はそのまま() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);
        cpu.setRegister(9, 0);

        SllvInstruction instruction = new SllvInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(10));
    }
}