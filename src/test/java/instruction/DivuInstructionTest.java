package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * DivuInstructionクラスのテスト。
 */
class DivuInstructionTest {

    /**
     * 符号なし除算結果がHIとLOへ格納されることを確認する。
     */
    @Test
    void 符号なし除算結果をHIとLOへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        DivuInstruction instruction = new DivuInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(3, cpu.getLo());
        assertEquals(2, cpu.getHi());
    }

    /**
     * 符号付きでは負数に見える値も、符号なしとして除算できることを確認する。
     */
    @Test
    void 符号なしとして除算できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1); // unsigned では 4294967295
        cpu.setRegister(9, 2);

        DivuInstruction instruction = new DivuInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(Integer.divideUnsigned(-1, 2), cpu.getLo());
        assertEquals(Integer.remainderUnsigned(-1, 2), cpu.getHi());
    }

    /**
     * 0除算で例外が発生することを確認する。
     */
    @Test
    void ゼロ除算で例外が発生する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 0);

        DivuInstruction instruction = new DivuInstruction(8, 9);

        assertThrows(ArithmeticException.class, () -> {
            instruction.execute(cpu);
        });
    }
}