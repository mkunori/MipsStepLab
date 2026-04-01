package parser;

import java.util.ArrayList;
import java.util.List;

import instruction.AddInstruction;
import instruction.AddiInstruction;
import instruction.Instruction;
import instruction.LiInstruction;
import instruction.SubInstruction;

/**
 * アセンブリ風の文字列をInstructionに変換するパーサ。
 */
public class InstructionParser {

    /**
     * 複数行のアセンブリ文字列を命令オブジェクトの一覧へ変換する。
     * 
     * @param lines      アセンブリ文字列の一覧
     * @param 命令オブジェクト一覧
     */
    public List<Instruction> parse(List<String> lines) {
        List<Instruction> instructions = new ArrayList<>();

        for (String line : lines) {
            String normalized = normalize(line);

            if (normalized.isEmpty()) {
                continue;
            }

            instructions.add(parseLine(normalized));
        }

        return instructions;
    }

    /**
     * 1行のアセンブリ文字列を命令オブジェクトへ変換する。
     * 
     * @param line 正規化済みの1行
     * @return 命令オブジェクト
     */
    public Instruction parseLine(String line) {
        String[] parts = line.split("\\s+", 2);
        String opcode = parts[0];

        if (parts.length < 2) {
            throw new IllegalArgumentException("オペランドがありません: " + line);
        }

        String[] operand = splitOperands(parts[1]);

        return switch (opcode) {
            case "li" -> parseLi(operand, line);
            case "add" -> parseAdd(operand, line);
            case "addi" -> parseAddi(operand, line);
            case "sub" -> parseSub(operand, line);
            default -> throw new IllegalArgumentException("未対応の命令です: " + line);
        };
    }

    /**
     * li命令を解析する。
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
     * レジスタ文字列をレジスタ番号へ変換する。
     * 
     * @param token 例: $t0
     * @return レジスタ番号
     */
    private int parseRegister(String token) {
        return switch (token) {
            case "$zero" -> 0;
            case "$v0" -> 2;
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
     * 前後空白を除去する。
     * 
     * @param line 元の文字列
     * @return 正規化後の文字列
     */
    private String normalize(String line) {
        return line.trim();
    }
}
