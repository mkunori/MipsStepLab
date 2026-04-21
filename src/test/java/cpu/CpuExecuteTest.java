package cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import instruction.JalInstruction;
import instruction.JrInstruction;
import instruction.JumpInstruction;
import instruction.LiInstruction;

/**
 * Cpu#executeのテスト。
 */
class CpuExecuteTest {

    /**
     * 通常命令ではexecute後にPCが1進むことを確認する。
     */
    @Test
    void executeで通常命令ならpcが1進む() {
        Cpu cpu = new Cpu();

        cpu.execute(new LiInstruction(8, 10));

        assertEquals(1, cpu.getPc());
    }

    /**
     * 分岐やジャンプでPCが変更された場合は、自動でPCを加算しないことを確認する。
     */
    @Test
    void executeでジャンプ命令ならpcを自動加算しない() {
        Cpu cpu = new Cpu();

        cpu.execute(new JumpInstruction(5));

        assertEquals(5, cpu.getPc());
    }

    @Test
    void setPcのあとでも通常命令ならpcが1進む() {
        Cpu cpu = new Cpu();
        cpu.setPc(10);

        cpu.execute(new LiInstruction(8, 123));

        assertEquals(11, cpu.getPc());
    }

    /**
     * jal命令ではPCが自動加算されず、ジャンプ先へ移動することを確認する。
     */
    @Test
    void jal命令ではジャンプ先へ移動する() {
        Cpu cpu = new Cpu();
        cpu.setPc(2);

        cpu.execute(new JalInstruction(7));

        assertEquals(3, cpu.getRegister(31));
        assertEquals(7, cpu.getPc());
    }

    /**
     * jr命令ではレジスタの値へ移動することを確認する。
     */
    @Test
    void jr命令ではレジスタの値へ移動する() {
        Cpu cpu = new Cpu();
        cpu.setPc(4);
        cpu.setRegister(31, 1);

        cpu.execute(new JrInstruction(31));

        assertEquals(1, cpu.getPc());
    }
}