package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SrlvInstructionクラスのテスト。
 */
class SrlvInstructionTest {

    /**
     * 論理右シフトが正しく実行されることを確認する。
     */
    @Test
    void レジスタで指定したビット数だけ論理右シフトする() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 16); // 値
        cpu.setRegister(9, 2); // シフト量

        SrlvInstruction instruction = new SrlvInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(4, cpu.getRegister(10));
    }

    /**
     * 負の値でも論理右シフトでは左側が0埋めされることを確認する。
     */
    @Test
    void 負数でも論理右シフトできる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8);
        cpu.setRegister(9, 2);

        SrlvInstruction instruction = new SrlvInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(-8 >>> 2, cpu.getRegister(10));
    }
}