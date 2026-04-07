package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * OrInstructionクラスのテスト。
 */
class OrInstructionTest {

    /**
     * 2つのレジスタ値に対してOR演算を行い、
     * 結果を指定レジスタへ格納することを確認する。
     */
    @Test
    void executeでビット単位ORを実行できる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        OrInstruction instruction = new OrInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(14, cpu.getRegister(10)); // 1110
    }

    /**
     * 片方が0のとき、もう片方の値がそのまま結果になることを確認する。
     */
    @Test
    void executeで片方が0ならもう片方の値になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setRegister(9, 5);

        OrInstruction instruction = new OrInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(5, cpu.getRegister(10));
    }

    /**
     * 両方が0のとき、結果も0になることを確認する。
     */
    @Test
    void executeで両方0なら結果も0になる() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setRegister(9, 0);

        OrInstruction instruction = new OrInstruction(10, 8, 9);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }
}