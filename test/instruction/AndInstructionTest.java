package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * AndInstructionクラスのテスト。
 */
class AndInstructionTest {

    /**
     * 2つのレジスタ値に対してAND演算を行い、
     * 結果を指定レジスタへ格納することを確認する。
     */
    @Test
    void executeでビット単位ANDを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        AndInstruction instruction = new AndInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(8, cpu.getRegister(10)); // 1000
    }

    /**
     * AND演算の結果が0になる場合も正しく格納できることを確認する。
     */
    @Test
    void executeで結果が0になる場合も格納できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 4); // 0100
        cpu.setRegister(9, 3); // 0011

        AndInstruction instruction = new AndInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }
}