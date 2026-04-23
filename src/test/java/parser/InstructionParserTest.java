package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import cpu.Cpu;

import instruction.AddiInstruction;
import instruction.AndInstruction;
import instruction.AndiInstruction;
import instruction.BeqInstruction;
import instruction.BgezInstruction;
import instruction.BgtzInstruction;
import instruction.BlezInstruction;
import instruction.BltzInstruction;
import instruction.BneInstruction;
import instruction.DivInstruction;
import instruction.DivuInstruction;
import instruction.Instruction;
import instruction.JalInstruction;
import instruction.JalrInstruction;
import instruction.JrInstruction;
import instruction.JumpInstruction;
import instruction.LbInstruction;
import instruction.LbuInstruction;
import instruction.LhInstruction;
import instruction.LhuInstruction;
import instruction.LiInstruction;
import instruction.LuiInstruction;
import instruction.LwInstruction;
import instruction.MfhiInstruction;
import instruction.MfloInstruction;
import instruction.MthiInstruction;
import instruction.MtloInstruction;
import instruction.MulInstruction;
import instruction.MultInstruction;
import instruction.MultuInstruction;
import instruction.NorInstruction;
import instruction.OrInstruction;
import instruction.OriInstruction;
import instruction.RemInstruction;
import instruction.SbInstruction;
import instruction.ShInstruction;
import instruction.SllInstruction;
import instruction.SllvInstruction;
import instruction.SltInstruction;
import instruction.SltiInstruction;
import instruction.SltiuInstruction;
import instruction.SltuInstruction;
import instruction.SravInstruction;
import instruction.SrlvInstruction;
import instruction.SwInstruction;
import instruction.XorInstruction;
import instruction.XoriInstruction;

/**
 * InstructionParserクラスのテスト。
 */
class InstructionParserTest {

    /**
     * li命令を正しくパースできることを確認する。
     */
    @Test
    void li命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 10"));

        assertEquals(1, program.size());
        assertInstanceOf(LiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        program.get(0).execute(cpu);

        assertEquals(10, cpu.getRegister(8));
    }

    /**
     * lui命令を正しくパースできることを確認する。
     */
    @Test
    void lui命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lui $t0, 1"));

        assertEquals(1, program.size());
        assertInstanceOf(LuiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        program.get(0).execute(cpu);

        assertEquals(1 << 16, cpu.getRegister(8));
    }

    /**
     * addi命令を正しくパースできることを確認する。
     */
    @Test
    void addi命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "addi $t1, $t0, 5"));

        assertEquals(1, program.size());
        assertInstanceOf(AddiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        program.get(0).execute(cpu);

        assertEquals(15, cpu.getRegister(9));
    }

    /**
     * sw命令を正しくパースできることを確認する。
     */
    @Test
    void sw命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sw $t0, 0($t1)"));

        assertEquals(1, program.size());
        assertInstanceOf(SwInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 123);
        cpu.setRegister(9, 10);
        program.get(0).execute(cpu);

        assertEquals(123, cpu.loadWord(10));
    }

    /**
     * lw命令を正しくパースできることを確認する。
     */
    @Test
    void lw命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lw $t0, 0($t1)"));

        assertEquals(1, program.size());
        assertInstanceOf(LwInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(9, 10);
        cpu.storeWord(10, 456);
        program.get(0).execute(cpu);

        assertEquals(456, cpu.getRegister(8));
    }

    /**
     * beq命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void beq命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 1",
                "li $t1, 1",
                "beq $t0, $t1, equal",
                "li $v0, 0",
                "equal: li $v0, 1"));

        assertEquals(5, program.size());
        assertInstanceOf(BeqInstruction.class, program.get(2));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);
        cpu.setRegister(9, 1);
        cpu.setPc(2);

        program.get(2).execute(cpu);

        assertEquals(4, cpu.getPc());
    }

    /**
     * bne命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void bne命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 1",
                "li $t1, 2",
                "bne $t0, $t1, notEqual",
                "li $v0, 0",
                "notEqual: li $v0, 1"));

        assertEquals(5, program.size());
        assertInstanceOf(BneInstruction.class, program.get(2));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);
        cpu.setRegister(9, 2);
        cpu.setPc(2);

        program.get(2).execute(cpu);

        assertEquals(4, cpu.getPc());
    }

    /**
     * j命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void j命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "j end",
                "li $v0, 0",
                "end: li $v0, 1"));

        assertEquals(3, program.size());
        assertInstanceOf(JumpInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setPc(0);

        program.get(0).execute(cpu);

        assertEquals(2, cpu.getPc());
    }

    /**
     * jal命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void jal命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "jal func",
                "li $v0, 0",
                "func: li $v0, 1"));

        assertEquals(3, program.size());
        assertInstanceOf(JalInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setPc(0);

        program.get(0).execute(cpu);

        assertEquals(1, cpu.getRegister(31));
        assertEquals(2, cpu.getPc());
    }

    /**
     * jr命令を正しくパースできることを確認する。
     */
    @Test
    void jr命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "jr $ra"));

        assertEquals(1, program.size());
        assertInstanceOf(JrInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(31, 5);

        program.get(0).execute(cpu);

        assertEquals(5, cpu.getPc());
    }

    @Test
    void jalr命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "jalr $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(JalrInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setPc(3);
        cpu.setRegister(8, 10);

        program.get(0).execute(cpu);

        assertEquals(4, cpu.getRegister(31));
        assertEquals(10, cpu.getPc());
    }

    /**
     * and命令を正しくパースできることを確認する。
     */
    @Test
    void and命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "and $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(AndInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        program.get(0).execute(cpu);

        assertEquals(8, cpu.getRegister(10));
    }

    /**
     * or命令を正しくパースできることを確認する。
     */
    @Test
    void or命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "or $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(OrInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12); // 1100
        cpu.setRegister(9, 10); // 1010

        program.get(0).execute(cpu);

        assertEquals(14, cpu.getRegister(10));
    }

    /**
     * xor命令を正しくパースできることを確認する。
     */
    @Test
    void xor命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "xor $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(XorInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);
        cpu.setRegister(9, 10);

        program.get(0).execute(cpu);

        assertEquals(6, cpu.getRegister(10));
    }

    /**
     * nor命令を正しくパースできることを確認する。
     */
    @Test
    void nor命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "nor $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(NorInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);
        cpu.setRegister(9, 10);

        program.get(0).execute(cpu);

        assertEquals(-15, cpu.getRegister(10));
    }

    /**
     * andi命令を正しくパースできることを確認する。
     */
    @Test
    void andi命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "andi $t1, $t0, 10"));

        assertEquals(1, program.size());
        assertInstanceOf(AndiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);

        program.get(0).execute(cpu);

        assertEquals(8, cpu.getRegister(9));
    }

    /**
     * ori命令を正しくパースできることを確認する。
     */
    @Test
    void ori命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "ori $t1, $t0, 10"));

        assertEquals(1, program.size());
        assertInstanceOf(OriInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);

        program.get(0).execute(cpu);

        assertEquals(14, cpu.getRegister(9));
    }

    /**
     * xori命令を正しくパースできることを確認する。
     */
    @Test
    void xori命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "xori $t1, $t0, 10"));

        assertEquals(1, program.size());
        assertInstanceOf(XoriInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 12);

        program.get(0).execute(cpu);

        assertEquals(6, cpu.getRegister(9));
    }

    /**
     * sll命令を正しくパースできることを確認する。
     */
    @Test
    void sll命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sll $t1, $t0, 2"));

        assertEquals(1, program.size());
        assertInstanceOf(SllInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        program.get(0).execute(cpu);

        assertEquals(12, cpu.getRegister(9));
    }

    /**
     * sllv命令を正しくパースできることを確認する。
     */
    @Test
    void sllv命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sllv $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SllvInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1); // $t0
        cpu.setRegister(9, 3); // $t1

        program.get(0).execute(cpu);

        assertEquals(8, cpu.getRegister(10));
    }

    /**
     * srlv命令を正しくパースできることを確認する。
     */
    @Test
    void srlv命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "srlv $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SrlvInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 16); // $t0
        cpu.setRegister(9, 2); // $t1

        program.get(0).execute(cpu);

        assertEquals(4, cpu.getRegister(10));
    }

    /**
     * srlv命令で負数を論理右シフトできることを確認する。
     */
    @Test
    void srlv命令で負数を論理右シフトできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "srlv $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SrlvInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8); // $t0 = シフト対象
        cpu.setRegister(9, 2); // $t1 = シフト量

        program.get(0).execute(cpu);

        assertEquals(-8 >>> 2, cpu.getRegister(10));
    }

    /**
     * sllv命令でシフト量が0なら値がそのままになることを確認する。
     */
    @Test
    void sllv命令でシフト量0なら値はそのまま() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sllv $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SllvInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 7); // $t0 = シフト対象
        cpu.setRegister(9, 0); // $t1 = シフト量

        program.get(0).execute(cpu);

        assertEquals(7, cpu.getRegister(10));
    }

    /**
     * sltu命令で負数が大きい値として扱われることを確認する。
     */
    @Test
    void sltu命令で負数を符号なし比較できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sltu $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SltuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1); // $t0 = シフト対象
        cpu.setRegister(9, 1); // $t1 = シフト量

        program.get(0).execute(cpu);

        assertEquals(0, cpu.getRegister(10));
    }

    /**
     * sltiu命令で負数が大きい値として扱われることを確認する。
     */
    @Test
    void sltiu命令で負数を符号なし比較できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sltiu $t1, $t0, 1"));

        assertEquals(1, program.size());
        assertInstanceOf(SltiuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        program.get(0).execute(cpu);

        assertEquals(0, cpu.getRegister(9));
    }

    /**
     * slt命令を正しくパースできることを確認する。
     */
    @Test
    void slt命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "slt $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SltInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);
        cpu.setRegister(9, 5);

        program.get(0).execute(cpu);

        assertEquals(1, cpu.getRegister(10));
    }

    /**
     * slti命令を正しくパースできることを確認する。
     */
    @Test
    void slti命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "slti $t1, $t0, 5"));

        assertEquals(1, program.size());
        assertInstanceOf(SltiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        program.get(0).execute(cpu);

        assertEquals(1, cpu.getRegister(9));
    }

    /**
     * sltu命令を正しくパースできることを確認する。
     */
    @Test
    void sltu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sltu $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SltuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);
        cpu.setRegister(9, 5);

        program.get(0).execute(cpu);

        assertEquals(1, cpu.getRegister(10));
    }

    /**
     * sltiu命令を正しくパースできることを確認する。
     */
    @Test
    void sltiu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sltiu $t1, $t0, 5"));

        assertEquals(1, program.size());
        assertInstanceOf(SltiuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 3);

        program.get(0).execute(cpu);

        assertEquals(1, cpu.getRegister(9));
    }

    /**
     * lb命令を正しくパースできることを確認する。
     */
    @Test
    void lb命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lb $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LbInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 5);

        program.get(0).execute(cpu);

        assertEquals(5, cpu.getRegister(9));
    }

    /**
     * lb命令を正しくパースできることを確認する。
     */
    @Test
    void lb命令で負の値を符号拡張して読み込める() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lb $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LbInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 0xFF);

        program.get(0).execute(cpu);

        assertEquals(-1, cpu.getRegister(9));
    }

    /**
     * sb命令を正しくパースできることを確認する。
     */
    @Test
    void sb命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sb $t1, 1($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(SbInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0xAB);

        program.get(0).execute(cpu);

        assertEquals(0xAB, cpu.loadByte(101) & 0xFF);
    }

    /**
     * sb命令を正しくパースできることを確認する。
     */
    @Test
    void sb命令で下位8ビットだけを書き込める() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sb $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(SbInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0x1234ABCD);

        program.get(0).execute(cpu);

        assertEquals(0xCD, cpu.loadByte(100) & 0xFF);
    }

    /**
     * lh命令を正しくパースできることを確認する。
     */
    @Test
    void lh命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lh $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LhInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0x1234);

        program.get(0).execute(cpu);

        assertEquals(0x1234, cpu.getRegister(9));
    }

    /**
     * lh命令で負のhalfwordを符号拡張して読み込めることを確認する。
     */
    @Test
    void lh命令で負のhalfwordを符号拡張して読み込める() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lh $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LhInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0xFFFF);

        program.get(0).execute(cpu);

        assertEquals(-1, cpu.getRegister(9));
    }

    /**
     * sh命令を正しくパースできることを確認する。
     */
    @Test
    void sh命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sh $t1, 2($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(ShInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0x1234ABCD);

        program.get(0).execute(cpu);

        assertEquals(0xABCD, cpu.loadHalfWord(102) & 0xFFFF);
    }

    /**
     * sh命令で下位16ビットだけを書き込めることを確認する。
     */
    @Test
    void sh命令で下位16ビットだけを書き込める() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "sh $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(ShInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.setRegister(9, 0x1234ABCD);

        program.get(0).execute(cpu);

        assertEquals(0xABCD, cpu.loadHalfWord(100) & 0xFFFF);
    }

    /**
     * lbu命令を正しくパースできることを確認する。
     */
    @Test
    void lbu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lbu $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LbuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeByte(100, 0xFF);

        program.get(0).execute(cpu);

        assertEquals(255, cpu.getRegister(9));
    }

    /**
     * lhu命令を正しくパースできることを確認する。
     */
    @Test
    void lhu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "lhu $t1, 0($t0)"));

        assertEquals(1, program.size());
        assertInstanceOf(LhuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 100);
        cpu.storeHalfWord(100, 0xFFFF);

        program.get(0).execute(cpu);

        assertEquals(65535, cpu.getRegister(9));
    }

    /**
     * srav命令を正しくパースできることを確認する。
     */
    @Test
    void srav命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "srav $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SravInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 16); // $t0
        cpu.setRegister(9, 2); // $t1

        program.get(0).execute(cpu);

        assertEquals(4, cpu.getRegister(10));
    }

    /**
     * srav命令で負数のシフトを正しくパースできることを確認する。
     */
    @Test
    void srav命令で負数を算術右シフトできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "srav $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(SravInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -8); // $t0
        cpu.setRegister(9, 2); // $t1

        program.get(0).execute(cpu);

        assertEquals(-2, cpu.getRegister(10));
    }

    @Test
    void mfhi命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mfhi $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MfhiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setHi(99);

        program.get(0).execute(cpu);

        assertEquals(99, cpu.getRegister(8));
    }

    @Test
    void mflo命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mflo $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MfloInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setLo(456);

        program.get(0).execute(cpu);

        assertEquals(456, cpu.getRegister(8));
    }

    @Test
    void mthi命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mthi $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MthiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 99);

        program.get(0).execute(cpu);

        assertEquals(99, cpu.getHi());
    }

    @Test
    void mthi命令で負の値をHIへコピーできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mthi $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MthiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        program.get(0).execute(cpu);

        assertEquals(-1, cpu.getHi());
    }

    @Test
    void mtlo命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mtlo $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MtloInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 456);

        program.get(0).execute(cpu);

        assertEquals(456, cpu.getLo());
    }

    @Test
    void mtlo命令で負の値をLOへコピーできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mtlo $t0"));

        assertEquals(1, program.size());
        assertInstanceOf(MtloInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);

        program.get(0).execute(cpu);

        assertEquals(-1, cpu.getLo());
    }

    @Test
    void mult命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mult $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(MultInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        program.get(0).execute(cpu);

        assertEquals(0, cpu.getHi());
        assertEquals(42, cpu.getLo());
    }

    @Test
    void mult命令で負数を含む乗算をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mult $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(MultInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -3);
        cpu.setRegister(9, 5);

        program.get(0).execute(cpu);

        assertEquals(-1, cpu.getHi());
        assertEquals(-15, cpu.getLo());
    }

    @Test
    void multu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "multu $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(MultuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        program.get(0).execute(cpu);

        assertEquals(0, cpu.getHi());
        assertEquals(42, cpu.getLo());
    }

    @Test
    void multu命令で符号なし乗算をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "multu $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(MultuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);
        cpu.setRegister(9, 2);

        program.get(0).execute(cpu);

        long result = Integer.toUnsignedLong(-1) * 2L;
        assertEquals((int) (result >>> 32), cpu.getHi());
        assertEquals((int) result, cpu.getLo());
    }

    @Test
    void div命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "div $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(DivInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        program.get(0).execute(cpu);

        assertEquals(3, cpu.getLo());
        assertEquals(2, cpu.getHi());
    }

    @Test
    void divu命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "divu $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(DivuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        program.get(0).execute(cpu);

        assertEquals(3, cpu.getLo());
        assertEquals(2, cpu.getHi());
    }

    @Test
    void divu命令で符号なし除算をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "divu $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(DivuInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);
        cpu.setRegister(9, 2);

        program.get(0).execute(cpu);

        assertEquals(Integer.divideUnsigned(-1, 2), cpu.getLo());
        assertEquals(Integer.remainderUnsigned(-1, 2), cpu.getHi());
    }

    /**
     * bgez命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void bgez命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 0",
                "bgez $t0, nonNegative",
                "li $v0, 0",
                "nonNegative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BgezInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(3, cpu.getPc());
    }

    /**
     * bltz命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void bltz命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, -1",
                "bltz $t0, negative",
                "li $v0, 0",
                "negative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BltzInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(3, cpu.getPc());
    }

    /**
     * bgtz命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void bgtz命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 1",
                "bgtz $t0, positive",
                "li $v0, 0",
                "positive: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BgtzInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(3, cpu.getPc());
    }

    /**
     * blez命令でラベルを正しく解決できることを確認する。
     */
    @Test
    void blez命令でラベルを解決できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 0",
                "blez $t0, zeroOrNegative",
                "li $v0, 0",
                "zeroOrNegative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BlezInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(3, cpu.getPc());
    }

    /**
     * bgez命令で条件を満たさない場合は分岐しないことを確認する。
     */
    @Test
    void bgez命令で条件を満たさない場合は分岐しない() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, -1",
                "bgez $t0, nonNegative",
                "li $v0, 0",
                "nonNegative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BgezInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -1);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(1, cpu.getPc());
    }

    /**
     * bltz命令で条件を満たさない場合は分岐しないことを確認する。
     */
    @Test
    void bltz命令で条件を満たさない場合は分岐しない() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 0",
                "bltz $t0, negative",
                "li $v0, 0",
                "negative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BltzInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(1, cpu.getPc());
    }

    /**
     * bgtz命令で条件を満たさない場合は分岐しないことを確認する。
     */
    @Test
    void bgtz命令で条件を満たさない場合は分岐しない() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 0",
                "bgtz $t0, positive",
                "li $v0, 0",
                "positive: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BgtzInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 0);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(1, cpu.getPc());
    }

    /**
     * blez命令で条件を満たさない場合は分岐しないことを確認する。
     */
    @Test
    void blez命令で条件を満たさない場合は分岐しない() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 1",
                "blez $t0, zeroOrNegative",
                "li $v0, 0",
                "zeroOrNegative: li $v0, 1"));

        assertEquals(4, program.size());
        assertInstanceOf(BlezInstruction.class, program.get(1));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 1);
        cpu.setPc(1);

        program.get(1).execute(cpu);

        assertEquals(1, cpu.getPc());
    }

    /**
     * move擬似命令を正しくパースできることを確認する。
     */
    @Test
    void move擬似命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "move $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(AddiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(9, 123);

        program.get(0).execute(cpu);

        assertEquals(123, cpu.getRegister(8));
    }

    /**
     * nop擬似命令を正しくパースできることを確認する。
     */
    @Test
    void nop擬似命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "nop"));

        assertEquals(1, program.size());
        assertInstanceOf(SllInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 123);

        program.get(0).execute(cpu);

        assertEquals(123, cpu.getRegister(8));
        assertEquals(0, cpu.getRegister(0));
    }

    /**
     * rem擬似命令を正しくパースできることを確認する。
     */
    @Test
    void rem擬似命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "rem $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(RemInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 20);
        cpu.setRegister(9, 6);

        program.get(0).execute(cpu);

        assertEquals(2, cpu.getRegister(10));
        assertEquals(3, cpu.getLo());
        assertEquals(2, cpu.getHi());
    }

    /**
     * rem擬似命令を正しくパースできることを確認する。
     */
    @Test
    void rem擬似命令で負数の余りを計算できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "rem $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(RemInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, -20);
        cpu.setRegister(9, 6);

        program.get(0).execute(cpu);

        assertEquals(-2, cpu.getRegister(10));
        assertEquals(-3, cpu.getLo());
        assertEquals(-2, cpu.getHi());
    }

    /**
     * mul擬似命令を正しくパースできることを確認する。
     */
    @Test
    void mul擬似命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "mul $t2, $t0, $t1"));

        assertEquals(1, program.size());
        assertInstanceOf(MulInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 6);
        cpu.setRegister(9, 7);

        program.get(0).execute(cpu);

        assertEquals(42, cpu.getRegister(10));
    }

    /**
     * ラベル付き命令を正しくパースできることを確認する。
     */
    @Test
    void label付き命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "loop: addi $t0, $t0, 1"));

        assertEquals(1, program.size());
        assertInstanceOf(AddiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        cpu.setRegister(8, 10);
        program.get(0).execute(cpu);

        assertEquals(11, cpu.getRegister(8));
    }

    /**
     * コメントを除去してパースできることを確認する。
     */
    @Test
    void コメント付き命令をパースできる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "li $t0, 10 # 初期値"));

        assertEquals(1, program.size());
        assertInstanceOf(LiInstruction.class, program.get(0));

        Cpu cpu = new Cpu();
        program.get(0).execute(cpu);

        assertEquals(10, cpu.getRegister(8));
    }

    /**
     * 空行やコメント行を無視できることを確認する。
     */
    @Test
    void 空行やコメント行を無視できる() {
        InstructionParser parser = new InstructionParser();

        List<Instruction> program = parser.parse(List.of(
                "",
                "   ",
                "# comment",
                "li $t0, 10"));

        assertEquals(1, program.size());
        assertInstanceOf(LiInstruction.class, program.get(0));
    }

    /**
     * 未対応の命令では例外が発生することを確認する。
     */
    @Test
    void 未対応命令で例外が発生する() {
        InstructionParser parser = new InstructionParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("xyz $t0, $t1, $t2")));
    }

    /**
     * 不正なレジスタ名では例外が発生することを確認する。
     */
    @Test
    void 不正なレジスタ名で例外が発生する() {
        InstructionParser parser = new InstructionParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("li $tx, 10")));
    }

    /**
     * 未定義ラベルでは例外が発生することを確認する。
     */
    @Test
    void 未定義ラベルで例外が発生する() {
        InstructionParser parser = new InstructionParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("beq $t0, $t1, unknownLabel")));
    }

    /**
     * ラベルの重複定義では例外が発生することを確認する。
     */
    @Test
    void ラベル重複で例外が発生する() {
        InstructionParser parser = new InstructionParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of(
                "loop: li $t0, 1",
                "loop: li $t1, 2")));
    }
}