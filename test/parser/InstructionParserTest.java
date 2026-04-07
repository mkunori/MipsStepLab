package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import cpu.Cpu;
import instruction.AddiInstruction;
import instruction.AndInstruction;
import instruction.BeqInstruction;
import instruction.BneInstruction;
import instruction.Instruction;
import instruction.JumpInstruction;
import instruction.LiInstruction;
import instruction.LwInstruction;
import instruction.NorInstruction;
import instruction.OrInstruction;
import instruction.SwInstruction;
import instruction.XorInstruction;

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

        assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of("mul $t0, $t1, $t2")));
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