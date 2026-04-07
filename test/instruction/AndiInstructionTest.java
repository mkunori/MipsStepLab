package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * AndiInstructionクラスのテスト。
 */
class AndiInstructionTest {

    /**
     * レジスタと即値のAND演算が正しく実行されることを確認する。
     */
    @Test
    void executeでレジスタと即値のANDを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100

        AndiInstruction instruction = new AndiInstruction(9, 8, 10); // 1010

        instruction.execute(cpu);

        assertEquals(8, cpu.getRegister(9)); // 1000
    }

    /**
     * 即値が0の場合、結果が0になることを確認する。
     */
    @Test
    void executeで即値が0なら結果は0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 15);

        AndiInstruction instruction = new AndiInstruction(9, 8, 0);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }

    /**
     * 即値が全ビット1の場合、元の値がそのまま結果になることを確認する。
     */
    @Test
    void executeで即値が全ビット1なら元の値になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);

        AndiInstruction instruction = new AndiInstruction(9, 8, -1);

        instruction.execute(cpu);

        assertEquals(7, cpu.getRegister(9));
    }
}