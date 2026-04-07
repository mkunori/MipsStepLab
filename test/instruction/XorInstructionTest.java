package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * XorInstructionクラスのテスト。
 */
class XorInstructionTest {

    /**
     * 2つのレジスタ値に対してXOR演算を行い、
     * 結果を指定レジスタへ格納することを確認する。
     */
    @Test
    void executeでビット単位XORを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        XorInstruction instruction = new XorInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(6, cpu.getRegister(10)); // 0110
    }

    /**
     * 同じ値同士のXORは0になることを確認する。
     */
    @Test
    void executeで同じ値同士なら0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7);
        cpu.setRegister(9, 7);

        XorInstruction instruction = new XorInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }

    /**
     * 片方が0の場合、もう片方の値がそのまま結果になることを確認する。
     */
    @Test
    void executeで片方が0ならもう片方の値になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setRegister(9, 5);

        XorInstruction instruction = new XorInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(10));
    }
}