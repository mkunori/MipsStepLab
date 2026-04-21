package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * JalInstructionクラスのテスト。
 */
class JalInstructionTest {

    /**
     * executeで戻り先を$raへ保存し、指定したPCへジャンプすることを確認する。
     */
    @Test
    void executeで戻り先をraへ保存してジャンプする() {
        Cpu cpu = new Cpu();
        cpu.setPc(2);

        JalInstruction instruction = new JalInstruction(8);

        instruction.execute(cpu);

        assertEquals(3, cpu.getRegister(31));
        assertEquals(8, cpu.getPc());
    }
}