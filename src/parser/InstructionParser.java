package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instruction.AddInstruction;
import instruction.AddiInstruction;
import instruction.AndInstruction;
import instruction.AndiInstruction;
import instruction.BeqInstruction;
import instruction.BneInstruction;
import instruction.Instruction;
import instruction.JalInstruction;
import instruction.JrInstruction;
import instruction.JumpInstruction;
import instruction.LbInstruction;
import instruction.LbuInstruction;
import instruction.LhInstruction;
import instruction.LhuInstruction;
import instruction.LiInstruction;
import instruction.LuiInstruction;
import instruction.LwInstruction;
import instruction.NorInstruction;
import instruction.OrInstruction;
import instruction.OriInstruction;
import instruction.SbInstruction;
import instruction.ShInstruction;
import instruction.SllInstruction;
import instruction.SllvInstruction;
import instruction.SltInstruction;
import instruction.SltiInstruction;
import instruction.SltiuInstruction;
import instruction.SltuInstruction;
import instruction.SraInstruction;
import instruction.SravInstruction;
import instruction.SrlInstruction;
import instruction.SrlvInstruction;
import instruction.SubInstruction;
import instruction.SwInstruction;
import instruction.XorInstruction;
import instruction.XoriInstruction;

/**
 * アセンブリ風の文字列をInstructionに変換するパーサ。
 */
public class InstructionParser {

    /**
     * 複数行のアセンブリ文字列を命令オブジェクトの一覧へ変換する。
     * 
     * @param lines アセンブリ文字列の一覧
     * @return 命令オブジェクト一覧
     */
    public List<Instruction> parse(List<String> lines) {
        Map<String, Integer> labels = collectLabels(lines);
        List<Instruction> instructions = new ArrayList<>();

        for (String line : lines) {
            String normalized = normalize(line);

            if (normalized.isEmpty()) {
                continue;
            }

            String instructionLine = normalized;

            // ラベル行または先頭ラベル+命令部分か？
            if (hasLabel(normalized)) {
                // 先頭ラベル+命令部分から命令部分を取り出す
                instructionLine = extractInstructionPart(normalized);

                // 命令部分が無いならばラベル行なので飛ばす。
                if (instructionLine.isEmpty()) {
                    continue;
                }
            }

            instructions.add(parseLine(instructionLine, labels));
        }

        return instructions;
    }

    /**
     * 1行のアセンブリ文字列を命令オブジェクトへ変換する。
     * 
     * @param line   正規化済みの1行
     * @param labels ラベル名と命令番号の対応表
     * @return 命令オブジェクト
     */
    public Instruction parseLine(String line, Map<String, Integer> labels) {
        // オペコードとオペランドに分割する。
        // 例
        // "add $t0, $t1, $t2" -> ["add", "$t0, $t1, $t2"]
        String[] parts = line.split("\\s+", 2);
        String opcode = parts[0];

        if (parts.length < 2) {
            throw new IllegalArgumentException("オペランドがありません: " + line);
        }

        String[] operands = splitOperands(parts[1]);

        return switch (opcode) {
            case "li" -> parseLi(operands, line);
            case "add" -> parseAdd(operands, line);
            case "addi" -> parseAddi(operands, line);
            case "sub" -> parseSub(operands, line);
            case "beq" -> parseBeq(operands, line, labels);
            case "bne" -> parseBne(operands, line, labels);
            case "j" -> parseJump(operands, line, labels);
            case "jal" -> parseJal(operands, line, labels);
            case "jr" -> parseJr(operands, line);
            case "lw" -> parseLw(operands, line);
            case "sw" -> parseSw(operands, line);
            case "lb" -> parseLb(operands, line);
            case "sb" -> parseSb(operands, line);
            case "lh" -> parseLh(operands, line);
            case "sh" -> parseSh(operands, line);
            case "lbu" -> parseLbu(operands, line);
            case "lhu" -> parseLhu(operands, line);
            case "and" -> parseAnd(operands, line);
            case "or" -> parseOr(operands, line);
            case "xor" -> parseXor(operands, line);
            case "nor" -> parseNor(operands, line);
            case "andi" -> parseAndi(operands, line);
            case "ori" -> parseOri(operands, line);
            case "xori" -> parseXori(operands, line);
            case "lui" -> parseLui(operands, line);
            case "sll" -> parseSll(operands, line);
            case "srl" -> parseSrl(operands, line);
            case "sllv" -> parseSllv(operands, line);
            case "srlv" -> parseSrlv(operands, line);
            case "sra" -> parseSra(operands, line);
            case "srav" -> parseSrav(operands, line);
            case "slt" -> parseSlt(operands, line);
            case "slti" -> parseSlti(operands, line);
            case "sltu" -> parseSltu(operands, line);
            case "sltiu" -> parseSltiu(operands, line);
            default -> throw new IllegalArgumentException("未対応の命令です: " + line);
        };
    }

    /**
     * li命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LiInstruction
     */
    private Instruction parseLi(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("liのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int imm = parseImmediate(operands[1]);
        return new LiInstruction(dest, imm);
    }

    /**
     * add命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return AddInstruction
     */
    private Instruction parseAdd(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("addのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);
        return new AddInstruction(dest, left, right);
    }

    /**
     * addi命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return AddiInstruction
     */
    private Instruction parseAddi(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("addiのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int imm = parseImmediate(operands[2]);
        return new AddiInstruction(dest, src, imm);
    }

    /**
     * sub命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SubInstruction
     */
    private Instruction parseSub(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("subのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);
        return new SubInstruction(dest, left, right);
    }

    /**
     * beq命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @param labels   ラベル対応表
     * @return BeqInstruction
     */
    private Instruction parseBeq(String[] operands, String line, Map<String, Integer> labels) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("beqのオペランド数が不正です: " + line);
        }

        int left = parseRegister(operands[0]);
        int right = parseRegister(operands[1]);
        int targetPc = resolveTarget(operands[2], labels);

        return new BeqInstruction(left, right, targetPc);
    }

    /**
     * bne命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @param labels   ラベル対応表
     * @return BneInstruction
     */
    private Instruction parseBne(String[] operands, String line, Map<String, Integer> labels) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("bneのオペランド数が不正です: " + line);
        }

        int left = parseRegister(operands[0]);
        int right = parseRegister(operands[1]);
        int targetPc = resolveTarget(operands[2], labels);

        return new BneInstruction(left, right, targetPc);
    }

    /**
     * j命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @param labels   ラベル対応表
     * @return JumpInstruction
     */
    private Instruction parseJump(String[] operands, String line, Map<String, Integer> labels) {
        if (operands.length != 1) {
            throw new IllegalArgumentException("jのオペランド数が不正です: " + line);
        }

        int targetPc = resolveTarget(operands[0], labels);
        return new JumpInstruction(targetPc);
    }

    /**
     * jal命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @param labels   ラベル対応表
     * @return JalInstruction
     */
    private Instruction parseJal(String[] operands, String line, Map<String, Integer> labels) {
        if (operands.length != 1) {
            throw new IllegalArgumentException("jalのオペランド数が不正です: " + line);
        }

        int targetPc = resolveTarget(operands[0], labels);
        return new JalInstruction(targetPc);
    }

    /**
     * jr命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return JrInstruction
     */
    private Instruction parseJr(String[] operands, String line) {
        if (operands.length != 1) {
            throw new IllegalArgumentException("jrのオペランド数が不正です: " + line);
        }

        int sourceRegister = parseRegister(operands[0]);
        return new JrInstruction(sourceRegister);
    }

    /**
     * lw命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LwInstruction
     */
    private Instruction parseLw(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("lwのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new LwInstruction(dest, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * sw命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SwInstruction
     */
    private Instruction parseSw(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("swのオペランド数が不正です: " + line);
        }

        int src = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new SwInstruction(src, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * lb命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LbInstruction
     */
    private Instruction parseLb(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("lbのオペランド数が不正です: " + line);
        }

        int destRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new LbInstruction(destRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * sb命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SbInstruction
     */
    private Instruction parseSb(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("sbのオペランド数が不正です: " + line);
        }

        int srcRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new SbInstruction(srcRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * lh命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LhInstruction
     */
    private Instruction parseLh(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("lhのオペランド数が不正です: " + line);
        }

        int destRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new LhInstruction(destRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * sh命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return ShInstruction
     */
    private Instruction parseSh(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("shのオペランド数が不正です: " + line);
        }

        int srcRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new ShInstruction(srcRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * lbu命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LbuInstruction
     */
    private Instruction parseLbu(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("lbuのオペランド数が不正です: " + line);
        }

        int destRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new LbuInstruction(destRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * lhu命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LhuInstruction
     */
    private Instruction parseLhu(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("lhuのオペランド数が不正です: " + line);
        }

        int destRegister = parseRegister(operands[0]);
        MemoryOperand memoryOperand = parseMemoryOperand(operands[1]);

        return new LhuInstruction(destRegister, memoryOperand.offset(), memoryOperand.baseRegister());
    }

    /**
     * and命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return AndInstruction
     */
    private Instruction parseAnd(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("andのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new AndInstruction(dest, left, right);
    }

    /**
     * or命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return OrInstruction
     */
    private Instruction parseOr(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("orのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new OrInstruction(dest, left, right);
    }

    /**
     * xor命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return XorInstruction
     */
    private Instruction parseXor(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("xorのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new XorInstruction(dest, left, right);
    }

    /**
     * nor命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return NorInstruction
     */
    private Instruction parseNor(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("norのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new NorInstruction(dest, left, right);
    }

    /**
     * andi命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return AndiInstruction
     */
    private Instruction parseAndi(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("andiのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseImmediate(operands[2]);

        return new AndiInstruction(dest, left, right);
    }

    /**
     * ori命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return OriInstruction
     */
    private Instruction parseOri(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("oriのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseImmediate(operands[2]);

        return new OriInstruction(dest, left, right);
    }

    /**
     * xori命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return XoriInstruction
     */
    private Instruction parseXori(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("xoriのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseImmediate(operands[2]);

        return new XoriInstruction(dest, left, right);
    }

    /**
     * lui命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return LiInstruction
     */
    private Instruction parseLui(String[] operands, String line) {
        if (operands.length != 2) {
            throw new IllegalArgumentException("luiのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int imm = parseImmediate(operands[1]);
        return new LuiInstruction(dest, imm);
    }

    /**
     * sll命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SllInstruction
     */
    private Instruction parseSll(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sllのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseImmediate(operands[2]);

        return new SllInstruction(dest, src, shift);
    }

    /**
     * srl命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SrlInstruction
     */
    private Instruction parseSrl(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("srlのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseImmediate(operands[2]);

        return new SrlInstruction(dest, src, shift);
    }

    /**
     * sllv命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SllvInstruction
     */
    private Instruction parseSllv(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sllvのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseRegister(operands[2]);

        return new SllvInstruction(dest, src, shift);
    }

    /**
     * srlv命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SrlvInstruction
     */
    private Instruction parseSrlv(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("srlvのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseRegister(operands[2]);

        return new SrlvInstruction(dest, src, shift);
    }

    /**
     * sra命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SraInstruction
     */
    private Instruction parseSra(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sraのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseImmediate(operands[2]);

        return new SraInstruction(dest, src, shift);
    }

    /**
     * srav命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SravInstruction
     */
    private Instruction parseSrav(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sravのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int shift = parseRegister(operands[2]);

        return new SravInstruction(dest, src, shift);
    }

    /**
     * slt命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SltInstruction
     */
    private Instruction parseSlt(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sltのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new SltInstruction(dest, left, right);
    }

    /**
     * slti命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SltiInstruction
     */
    private Instruction parseSlti(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sltiのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int imm = parseImmediate(operands[2]);

        return new SltiInstruction(dest, src, imm);
    }

    /**
     * sltu命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SltInstruction
     */
    private Instruction parseSltu(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sltuのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int left = parseRegister(operands[1]);
        int right = parseRegister(operands[2]);

        return new SltuInstruction(dest, left, right);
    }

    /**
     * sltiu命令を解析する。
     * 
     * @param operands オペランド配列
     * @param line     元の命令文字列
     * @return SltiInstruction
     */
    private Instruction parseSltiu(String[] operands, String line) {
        if (operands.length != 3) {
            throw new IllegalArgumentException("sltiuのオペランド数が不正です: " + line);
        }

        int dest = parseRegister(operands[0]);
        int src = parseRegister(operands[1]);
        int imm = parseImmediate(operands[2]);

        return new SltiuInstruction(dest, src, imm);
    }

    /**
     * レジスタ文字列をレジスタ番号へ変換する。
     * 
     * @param token 例: $t0
     * @return レジスタ番号
     */
    private int parseRegister(String token) {
        return switch (token) {
            case "$zero" -> 0;
            case "$at" -> 1;
            case "$v0" -> 2;
            case "$v1" -> 3;
            case "$a0" -> 4;
            case "$a1" -> 5;
            case "$a2" -> 6;
            case "$a3" -> 7;
            case "$t0" -> 8;
            case "$t1" -> 9;
            case "$t2" -> 10;
            case "$t3" -> 11;
            case "$t4" -> 12;
            case "$t5" -> 13;
            case "$t6" -> 14;
            case "$t7" -> 15;
            case "$s0" -> 16;
            case "$s1" -> 17;
            case "$s2" -> 18;
            case "$s3" -> 19;
            case "$s4" -> 20;
            case "$s5" -> 21;
            case "$s6" -> 22;
            case "$s7" -> 23;
            case "$t8" -> 24;
            case "$t9" -> 25;
            case "$k0" -> 26;
            case "$k1" -> 27;
            case "$gp" -> 28;
            case "$sp" -> 29;
            case "$fp" -> 30;
            case "$ra" -> 31;
            default -> throw new IllegalArgumentException("未対応のレジスタ名です: " + token);
        };
    }

    /**
     * 即値文字列をintへ変換する。
     * 
     * @param token 即値文字列
     * @return int値
     */
    private int parseImmediate(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("即値として解釈できません: " + token, e);
        }
    }

    /**
     * オペランド列をカンマで分割し、前後の空白を除去する。
     * 
     * @param operandPart オペランド文字列
     * @return 分割後のオペランド配列
     */
    private String[] splitOperands(String operandPart) {
        String[] raw = operandPart.split(",");
        String[] result = new String[raw.length];

        for (int i = 0; i < raw.length; i++) {
            result[i] = raw[i].trim();
        }

        return result;
    }

    /**
     * 1行を正規化する。
     * 
     * 前後空白とコメント文字列を除去する。
     * 
     * @param line 元の文字列
     * @return 正規化後の文字列
     */
    private String normalize(String line) {
        int commentIndex = line.indexOf('#');
        if (commentIndex >= 0) {
            line = line.substring(0, commentIndex);
        }
        return line.trim();
    }

    /**
     * ラベル名と命令番号の対応表を作成する。
     * 
     * @param lines アセンブリ文字列の一覧
     * @return ラベル名と命令番号の対応表
     */
    private Map<String, Integer> collectLabels(List<String> lines) {
        Map<String, Integer> labels = new HashMap<>();
        int instructionIndex = 0;

        for (String line : lines) {
            String normalized = normalize(line);

            if (normalized.isEmpty()) {
                continue;
            }

            if (hasLabel(normalized)) {
                String label = extractLabelNameFromLine(normalized);

                if (label.isEmpty()) {
                    throw new IllegalArgumentException("空のラベルは使えません: " + normalized);
                }

                if (labels.containsKey(label)) {
                    throw new IllegalArgumentException("ラベルが重複しています: " + label);
                }

                labels.put(label, instructionIndex);

                String instructionPart = extractInstructionPart(normalized);
                // ラベル行か？
                if (instructionPart.isEmpty()) {
                    continue;
                }
            }

            instructionIndex++;
        }

        return labels;
    }

    /**
     * 分岐先を命令番号で取得する。
     * 
     * @param token  分岐先の命令番号またはラベル名
     * @param labels ラベル名と命令番号の対応表
     * @return 分岐先の命令番号
     */
    private int resolveTarget(String token, Map<String, Integer> labels) {
        try {
            // 分岐先が即値
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            // 分岐先がラベル
            Integer target = labels.get(token);
            if (target == null) {
                throw new IllegalArgumentException("未定義のラベルです: " + token);
            }
            return target;
        }
    }

    /**
     * ラベル付き行をどうか判定する。
     * 
     * @param line 正規化済みの1行
     * @return ラベル付き行ならば true
     */
    private boolean hasLabel(String line) {
        return line.contains(":");
    }

    /**
     * 行頭のラベル名を取得する。
     * 
     * @param line 正規化済みの1行
     * @return ラベル名
     */
    private String extractLabelNameFromLine(String line) {
        int colonIndex = line.indexOf(':');
        return line.substring(0, colonIndex).trim();
    }

    /**
     * 1行から命令部分を取得する。
     * 
     * @param line 正規化済みの1行
     * @return 命令部分
     */
    private String extractInstructionPart(String line) {
        int colonIndex = line.indexOf(':');
        return line.substring(colonIndex + 1).trim();
    }

    /**
     * メモリStore/Loadのオペランドを解析する。
     * 
     * @param token レジスタ文字列
     * @return MemoryOperand
     */
    private MemoryOperand parseMemoryOperand(String token) {
        int leftParenIndex = token.indexOf('(');
        int rightParenIndex = token.indexOf(')');

        if (leftParenIndex < 0 || rightParenIndex < 0 || leftParenIndex >= rightParenIndex) {
            throw new IllegalArgumentException("メモリオペランドの形式が不正です: " + token);
        }

        String offsetPart = token.substring(0, leftParenIndex).trim();
        String registerPart = token.substring(leftParenIndex + 1, rightParenIndex).trim();

        int offset = parseImmediate(offsetPart);
        int baseRegister = parseRegister(registerPart);

        return new MemoryOperand(offset, baseRegister);
    }

    /**
     * メモリオペランドのレコードクラス
     * 
     * @param offset       メモリ位置のオフセット
     * @param baseRegister メモリ位置のベース
     */
    private record MemoryOperand(int offset, int baseRegister) {
    }
}
