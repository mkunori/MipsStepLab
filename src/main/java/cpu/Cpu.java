package cpu;

import instruction.Instruction;

/**
 * MIPS風CPUを表すクラス。
 * 
 * このCPUは以下の要素を保持する。
 * - 32本の汎用レジスタ
 * - 1024バイトのメモリ領域
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
     * byte単位で保持する。
     */
    private final byte[] memory = new byte[1024];

    /**
     * MIPSのプログラムカウンタ。
     * 
     * 実メモリアドレスではなく、
     * 「何番目の命令を実行しているか」を表す番号として扱う。
     */
    private int pc;

    /**
     * 命令実行中にPCが明示的に変更されたかどうか。
     * 
     * 分岐命令やジャンプ命令では true になる。
     */
    private boolean pcChanged;

    /**
     * 乗算・除算結果の上位32ビットを保持する特殊レジスタ。
     */
    private int hi;

    /**
     * 乗算・除算結果の下位32ビット、または商を保持する特殊レジスタ。
     */
    private int lo;

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
     * 指定したアドレスから1バイト読み込む。
     * 
     * 符号付きbyteとしてintへ拡張される。
     * 
     * @param address 読み込み元アドレス
     * @return 読み込んだ値
     */
    public int loadByte(int address) {
        validateMemoryRange(address, 1);
        return memory[address]; // 符号付きで返す（lb用）
    }

    /**
     * 指定したアドレスから1バイト読み込み、
     * 符号なし値として返す。
     * 
     * @param address 読み込み元アドレス
     * @return 0〜255 の値
     */
    public int loadByteUnsigned(int address) {
        validateMemoryRange(address, 1);
        return memory[address] & 0xFF;
    }

    /**
     * 指定したアドレスへ1バイト書き込む。
     * 
     * @param address 書き込み先アドレス
     * @param value   書き込む値
     */
    public void storeByte(int address, int value) {
        validateMemoryRange(address, 1);
        memory[address] = (byte) value;
    }

    /**
     * 指定したアドレスから2バイト読み込み、halfwordとして返す。
     * 
     * ビッグエンディアンで読み込む。
     * 結果は符号付き16ビット値としてintへ拡張される。
     * 
     * @param address 読み込み元アドレス
     * @return 読み込んだhalfword値
     */
    public int loadHalfWord(int address) {
        validateMemoryRange(address, 2);

        int upper = memory[address] & 0xFF;
        int lower = memory[address + 1] & 0xFF;
        int value = (upper << 8) | lower;

        if ((value & 0x8000) != 0) {
            value |= 0xFFFF0000;
        }

        return value;
    }

    /**
     * 指定したアドレスから2バイト読み込み、
     * 符号なしhalfwordとして返す。
     * 
     * ビッグエンディアンで読み込む。
     * 
     * @param address 読み込み元アドレス
     * @return 0〜65535 の値
     */
    public int loadHalfWordUnsigned(int address) {
        validateMemoryRange(address, 2);

        int upper = memory[address] & 0xFF;
        int lower = memory[address + 1] & 0xFF;
        return (upper << 8) | lower;
    }

    /**
     * 指定したアドレスへ2バイト書き込む。
     * 
     * ビッグエンディアンで格納する。
     * 
     * @param address 書き込み先アドレス
     * @param value   書き込む値
     */
    public void storeHalfWord(int address, int value) {
        validateMemoryRange(address, 2);

        memory[address] = (byte) (value >>> 8);
        memory[address + 1] = (byte) value;
    }

    /**
     * 指定したアドレスから4バイト読み込み、wordとして返す。
     * 
     * ビッグエンディアンで読み込む。
     * 
     * @param address 読み込み元アドレス
     * @return 読み込んだword値
     */
    public int loadWord(int address) {
        validateMemoryRange(address, 4);

        return ((memory[address] & 0xFF) << 24)
                | ((memory[address + 1] & 0xFF) << 16)
                | ((memory[address + 2] & 0xFF) << 8)
                | (memory[address + 3] & 0xFF);
    }

    /**
     * 指定したアドレスへ4バイト書き込む。
     * 
     * ビッグエンディアンで格納する。
     * 
     * @param address 書き込み先アドレス
     * @param value   書き込む値
     */
    public void storeWord(int address, int value) {
        validateMemoryRange(address, 4);

        memory[address] = (byte) (value >>> 24);
        memory[address + 1] = (byte) (value >>> 16);
        memory[address + 2] = (byte) (value >>> 8);
        memory[address + 3] = (byte) value;
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
        this.pcChanged = true;
    }

    /**
     * PCを次の命令へ進める。
     */
    public void incrementPc() {
        pc++;
    }

    /**
     * HIレジスタの値を取得する。
     * 
     * @return HIレジスタの値
     */
    public int getHi() {
        return hi;
    }

    /**
     * HIレジスタの値を設定する。
     * 
     * @param hi HIレジスタの値
     */
    public void setHi(int hi) {
        this.hi = hi;
    }

    /**
     * LOレジスタの値を取得する。
     * 
     * @return LOレジスタの値
     */
    public int getLo() {
        return lo;
    }

    /**
     * LOレジスタの値を設定する。
     * 
     * @return LOレジスタの値
     */
    public void setLo(int lo) {
        this.lo = lo;
    }

    /**
     * 命令を1つ実行する。
     * 
     * @param instruction 実行する命令
     */
    public void execute(Instruction instruction) {
        pcChanged = false;

        instruction.execute(this);

        // 分岐・ジャンプ命令向け対応。
        // 命令実行中にPCが変更されなかった場合のみ、自動で次の命令へ進める。
        if (!pcChanged) {
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
        validateMemoryRange(address, 1);
        return "mem[" + address + "] = " + (memory[address] & 0xFF);
    }

    /**
     * メモリアドレス範囲を検証する。
     * 
     * @param address 開始アドレス
     * @param size    使用バイト数
     */
    public void validateMemoryRange(int address, int size) {
        if (address < 0 || address + size > memory.length) {
            throw new IllegalArgumentException(
                    "メモリアドレス範囲が不正です: address=" + address + ", size=" + size);
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
    public byte[] copyMemory() {
        return memory.clone();
    }
}
