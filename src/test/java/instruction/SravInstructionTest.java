package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * SravInstructionクラスのテスト。
 */
class SravInstructionTest {

    /**
     * レジスタで指定したビット数だけ算術右シフトすることを確認する。
     */
    @Test
    void レジスタで指定したビット数だけ算術右シフトする() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 16);
        cpu.setRegister(9, 2);

        SravInstruction instruction = new SravInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(4, cpu.getRegister(10));
    }

    /**
     * 負数を算術右シフトしたときに符号ビットが維持されることを確認する。
     */
    @Test
    void 負数を算術右シフトすると符号ビットを維持する() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8);
        cpu.setRegister(9, 2);

        SravInstruction instruction = new SravInstruction(10, 8, 9);
        instruction.execute(cpu);

        assertEquals(-2, cpu.getRegister(10));
    }
}