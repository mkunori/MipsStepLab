package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * DivInstructionクラスのテスト。
 */
class DivInstructionTest {

    /**
     * 商と余りが正しくHI/LOへ格納されることを確認する。
     */
    @Test
    void 除算結果をHIとLOへ格納する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        DivInstruction instruction = new DivInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(3, cpu.getLo()); // 商
        assertEquals(2, cpu.getHi()); // 余り
    }

    /**
     * 負数を含む除算を確認する。
     */
    @Test
    void 負数を含む除算ができる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -20);
        cpu.setRegister(9, 6);

        DivInstruction instruction = new DivInstruction(8, 9);
        instruction.execute(cpu);

        assertEquals(-3, cpu.getLo());
        assertEquals(-2, cpu.getHi());
    }

    /**
     * 0除算で例外が発生することを確認する。
     */
    @Test
    void ゼロ除算で例外が発生する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        cpu.setRegister(9, 0);

        DivInstruction instruction = new DivInstruction(8, 9);

        assertThrows(ArithmeticException.class, () -> {
            instruction.execute(cpu);
        });
    }
}