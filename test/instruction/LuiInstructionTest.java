package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

/**
 * LuiInstruction のテストクラス。
 */
class LuiInstructionTest {

    /**
     * 即値を16ビット左シフトして格納されることを確認する。
     */
    @Test
    void 即値を左に16ビットシフトして格納する() {
        Cpu cpu = new Cpu();
        LuiInstruction instruction = new LuiInstruction(8, 1); // $t0

        instruction.execute(cpu);

        assertEquals(1 << 16, cpu.getRegister(8));
    }

    /**
     * 0を指定した場合は結果も0になることを確認する。
     */
    @Test
    void 即値0は0になる() {
        Cpu cpu = new Cpu();
        LuiInstruction instruction = new LuiInstruction(8, 0);

        instruction.execute(cpu);

        assertEquals(0, cpu.getRegister(8));
    }

    /**
     * 負数の即値でも正しく上位ビットに配置されることを確認する。
     */
    @Test
    void 負数の即値でも正しく動作する() {
        Cpu cpu = new Cpu();
        LuiInstruction instruction = new LuiInstruction(8, -1);

        instruction.execute(cpu);

        assertEquals(0xFFFF0000, cpu.getRegister(8));
    }

    /**
     * 任意の値でビット構造が正しいことを確認する。
     */
    @Test
    void 任意の値で正しく上位ビットに配置される() {
        Cpu cpu = new Cpu();
        LuiInstruction instruction = new LuiInstruction(8, 0x1234);

        instruction.execute(cpu);

        assertEquals(0x12340000, cpu.getRegister(8));
    }

    /**
     * 既存の値が上書きされることを確認する。
     */
    @Test
    void 既存値を上書きする() {
        Cpu cpu = new Cpu();
        cpu.setRegister(8, 999);

        LuiInstruction instruction = new LuiInstruction(8, 2);
        instruction.execute(cpu);

        assertEquals(2 << 16, cpu.getRegister(8));
    }

    /**
     * oriと組み合わせて32bit値を構築できることを確認する。f
     */
    @Test
    void luiとoriで32bit値を構築できる() {
        Cpu cpu = new Cpu();

        new LuiInstruction(8, 0x1234).execute(cpu);
        cpu.setRegister(8, cpu.getRegister(8) | 0x5678);

        assertEquals(0x12345678, cpu.getRegister(8));
    }
}