package cpu;

import instruction.Instruction;

/**
 * MIPS風CPUを表すクラス。
 * 
 * このCPUは以下の要素を保持する。
 * - 32本の汎用レジスタ
 * - PC（プログラムカウンタ）
 */
public class Cpu {

    /**
     * MIPSの汎用レジスタ32本。
     * 
     * Indexがレジスタ番号に対応する。
     * 例：
     * 0番 = {@code $zero}
     * 8番 = {@code $t0}
     * 9番 = {@code $t1}
     */
    private final int[] registers = new int[32];

    /**
     * MIPSのプログラムカウンタ。
     * 
     * 一旦は実メモリアドレスではなく、
     * 「何番目の命令を実行しているか」を表す番号として扱う。
     */
    private int pc;

    /**
     * レジスタの値を取得する。
     * 
     * @param index レジスタ番号
     * @return 指定したレジスタの値
     * @throws IllegalArguementExeption レジスタ番号が0～31の範囲外の場合
     */
    public int getRegister(int index) {
        validateRegisterIndex(index);

        return registers[index];
    }

    /**
     * レジスタに値を設定する。
     * 
     * @param index レジスタ番号
     * @param value 設定する値
     * @throws IllegalArguementExeption レジスタ番号が0～31の範囲外の場合
     */
    public void setRegister(int index, int value) {
        validateRegisterIndex(index);

        // MIPSの0番レジスタはzero固定なので書き込まない。
        if (index == 0) {
            return;
        }

        registers[index] = value;
    }

    /**
     * 現在のPCを取得する。
     * 
     * @return 現在のPC
     */
    public int getPc() {
        return pc;
    }

    /**
     * PCを次の命令へ進める。
     */
    public void incrementPc() {
        pc++;
    }

    /**
     * 命令を1つ実行する。
     * 
     * @param instruction 実行する命令
     */
    public void execute(Instruction instruction) {
        instruction.execute(this);
        incrementPc();
    }

    /**
     * レジスタ番号が有効かどうか検証する。
     * 
     * @param index レジスタ番号
     * @throws IllegalArguementExeption レジスタ番号が0～31の範囲外の場合
     */
    private void validateRegisterIndex(int index) {
        if (index < 0 || index >= registers.length) {
            throw new IllegalArgumentException(
                    "レジスタ番号は0～31で指定してください: " + index);
        }
    }

    /**
     * レジスタの名前を返す。
     * 
     * @param index レジスタ番号
     * @return レジスタ名
     */
    public String getRegisterName(int index) {
        validateRegisterIndex(index);

        return switch (index) {
            case 0 -> "$zero"; // 定数0
            case 1 -> "$at"; // 疑似命令で一時的に使用
            case 2 -> "$v0"; // 関数の戻り値や式の評価値
            case 3 -> "$v1";
            case 4 -> "$a0"; // 関数の引数
            case 5 -> "$a1";
            case 6 -> "$a2";
            case 7 -> "$a3";
            case 8 -> "$t0"; // 一時変数
            case 9 -> "$t1";
            case 10 -> "$t2";
            case 11 -> "$t3";
            case 12 -> "$t4";
            case 13 -> "$t5";
            case 14 -> "$t6";
            case 15 -> "$t7";
            case 16 -> "$s0"; // 一時変数(Saved)
            case 17 -> "$s1";
            case 18 -> "$s2";
            case 19 -> "$s3";
            case 20 -> "$s4";
            case 21 -> "$s5";
            case 22 -> "$s6";
            case 23 -> "$s7";
            case 24 -> "$t8"; // 一時変数
            case 25 -> "$t9";
            case 26 -> "$k0"; // OS用に予約
            case 27 -> "$k1";
            case 28 -> "$gp"; // グローバルポインタ
            case 29 -> "$sp"; // スタックポインタ
            case 30 -> "$fp"; // フレームポインタ
            case 31 -> "$ra"; // リターンアドレス
            default -> "$r" + index; // (アドレス検証しているので入らないはず)
        };
    }

    /**
     * 指定したレジスタの内容を文字列で返す。
     * 
     * @param index レジスタ番号
     * @return 例: {@code $t0 = 10}
     */
    public String formatRegister(int index) {
        return getRegisterName(index) + " = " + getRegister(index);
    }

    /**
     * 主要レジスタの内容をまとめた文字列を返す。
     *
     * 実行結果の確認をしやすくするため、
     * とりあえず学習でよく使うレジスタだけを表示する。
     *
     * @return レジスタ一覧の文字列
     */
    public String dumpRegisters() {
        StringBuilder sb = new StringBuilder();

        sb.append(formatRegister(0)).append(System.lineSeparator());
        // sb.append(formatRegister(1)).append(System.lineSeparator());
        // sb.append(formatRegister(2)).append(System.lineSeparator());
        // sb.append(formatRegister(3)).append(System.lineSeparator());
        // sb.append(formatRegister(4)).append(System.lineSeparator());
        // sb.append(formatRegister(5)).append(System.lineSeparator());
        // sb.append(formatRegister(6)).append(System.lineSeparator());
        // sb.append(formatRegister(7)).append(System.lineSeparator());
        sb.append(formatRegister(8)).append(System.lineSeparator());
        sb.append(formatRegister(9)).append(System.lineSeparator());
        sb.append(formatRegister(10)).append(System.lineSeparator());
        sb.append(formatRegister(11)).append(System.lineSeparator());
        // sb.append(formatRegister(12)).append(System.lineSeparator());
        // sb.append(formatRegister(13)).append(System.lineSeparator());
        // sb.append(formatRegister(14)).append(System.lineSeparator());
        // sb.append(formatRegister(15)).append(System.lineSeparator());
        // sb.append(formatRegister(16)).append(System.lineSeparator());
        // sb.append(formatRegister(17)).append(System.lineSeparator());
        // sb.append(formatRegister(18)).append(System.lineSeparator());
        // sb.append(formatRegister(19)).append(System.lineSeparator());
        // sb.append(formatRegister(20)).append(System.lineSeparator());
        // sb.append(formatRegister(21)).append(System.lineSeparator());
        // sb.append(formatRegister(22)).append(System.lineSeparator());
        // sb.append(formatRegister(23)).append(System.lineSeparator());
        // sb.append(formatRegister(24)).append(System.lineSeparator());
        // sb.append(formatRegister(25)).append(System.lineSeparator());
        // sb.append(formatRegister(26)).append(System.lineSeparator());
        // sb.append(formatRegister(27)).append(System.lineSeparator());
        // sb.append(formatRegister(28)).append(System.lineSeparator());
        // sb.append(formatRegister(29)).append(System.lineSeparator());
        // sb.append(formatRegister(30)).append(System.lineSeparator());
        // sb.append(formatRegister(31)).append(System.lineSeparator());

        return sb.toString();
    }
}
