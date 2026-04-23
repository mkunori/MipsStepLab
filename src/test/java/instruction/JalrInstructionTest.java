package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * JalrInstructionクラスのテスト。
 */
class JalrInstructionTest {

    /**
     * jalr命令で戻り先を$raへ保存し、指定先へジャンプできることを確認する。
     */
    @Test
    void jalr命令で戻り先を保存してジャンプできる() {
        Cpu cpu = new Cpu();
        cpu.setPc(3);
        cpu.setRegister(8, 10);

        JalrInstruction instruction = new JalrInstruction(8);
        instruction.execute(cpu);

        assertEquals(4, cpu.getRegister(31)); // $ra
        assertEquals(10, cpu.getPc());
    }

    /**
     * jalr命令で0番レジスタ以外のレジスタをジャンプ先として使えることを確認する。
     */
    @Test
    void jalr命令でレジスタの値へジャンプできる() {
        Cpu cpu = new Cpu();
        cpu.setPc(1);
        cpu.setRegister(9, 7);

        JalrInstruction instruction = new JalrInstruction(9);
        instruction.execute(cpu);

        assertEquals(2, cpu.getRegister(31));
        assertEquals(7, cpu.getPc());
    }
}