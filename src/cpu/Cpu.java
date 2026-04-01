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
     */
    public int getRegister(int index) {
        RegisterNames.validateRegisterIndex(index);
        return registers[index];
    }

    /**
     * レジスタに値を設定する。
     * 
     * @param index レジスタ番号
     * @param value 設定する値
     */
    public void setRegister(int index, int value) {
        RegisterNames.validateRegisterIndex(index);

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
     * PCを指定した値に設定する。
     * 
     * @param pc 新しいOC
     * @throws IllegalArgumentException PCが負の値の場合
     */
    public void setPc(int pc) {
        if (pc < 0) {
            throw new IllegalArgumentException("PCは0以上で指定してください: " + pc);
        }
        this.pc = pc;
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
        int oldPc = pc;
        instruction.execute(this);

        // beq向け対応。
        // 分岐時は自動加算させずにpcを変える。
        if (pc == oldPc) {
            incrementPc();
        }
    }

    /**
     * 指定したレジスタの内容を文字列で返す。
     * 
     * @param index レジスタ番号
     * @return 例: {@code $t0 = 10}
     */
    public String formatRegister(int index) {
        return RegisterNames.getName(index) + " = " + getRegister(index);
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
        sb.append(formatRegister(2)).append(System.lineSeparator());
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
