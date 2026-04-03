package cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
}