package cpu;

import instruction.Instruction;

/**
 * MIPS風CPUを表すクラス。
 * 
 * このCPUは以下の要素を保持する。
 * - 32本の汎用レジスタ
 * - 256wordのメモリ領域
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
     * MIPSのメモリ領域。
     * 
     * 1要素を1wordとして扱う。
     */
    private final int[] memory = new int[256];

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
     * メモリから値を読み込む。
     * 
     * @param address メモリアドレス
     * @return 指定アドレスの値
     */
    public int loadWord(int address) {
        validateMemoryAddress(address);
        return memory[address];
    }

    /**
     * メモリへ値を書き込む。
     * 
     * @param address メモリアドレス
     * @param value   書き込む値
     */
    public void storeWord(int address, int value) {
        validateMemoryAddress(address);
        memory[address] = value;
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
     * @param pc 新しいPC
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

        // 分岐・ジャンプ命令向け対応。
        // 命令実行中にPCが変更されなかった場合のみ、自動で次の命令へ進める。
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
     * レジスタ内容を桁揃えした文字列で返す。
     * 
     * @param index レジスタ番号
     * @return 例: {@code $t0   = 10}
     */
    public String formatRegisterAligned(int index) {
        RegisterNames.validateRegisterIndex(index);
        return String.format("%-5s = %d", RegisterNames.getName(index), getRegister(index));
    }

    /**
     * 指定したメモリ位置の内容を表示文字列にする。
     * 
     * @param address メモリアドレス
     * @return 表示文字列
     */
    public String formatMemory(int address) {
        validateMemoryAddress(address);
        return "mem[" + address + "] = " + memory[address];
    }

    /**
     * メモリアドレスが有効かどうか検証する。
     * 
     * @param address メモリアドレス
     * @throws IllegalArgumentException メモリアドレスが0～255の範囲外の場合
     */
    public void validateMemoryAddress(int address) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException(
                    "メモリアドレスは0～255で指定してください: " + address);
        }
    }

    /**
     * 現在のレジスタ状態をコピーして返す。
     * 
     * @return レジスタ配列のコピー
     */
    public int[] copyRegisters() {
        return registers.clone();
    }

    /**
     * 現在のメモリ状態をコピーして返す。
     * 
     * @return メモリ状態のコピー
     */
    public int[] copyMemory() {
        return memory.clone();
    }
}
