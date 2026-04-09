package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sw(store word)命令を表すクラス。
 * 
 * レジスタの値を、base + offset で求めたメモリ位置へ格納する。
 * 
 * 例:
 * {@code sw $t0, 4($t1)}
 * {@code $t0} の値をメモリ位置 {@code $t1 + 4} に格納している。
 */
public class SwInstruction implements Instruction {

    /** 書き込み元のレジスタ番号 */
    private final int srcRegister;

    /** メモリアドレス計算に使うオフセット値 */
    private final int offset;

    /** メモリアドレス計算に使うベースレジスタ番号 */
    private final int baseRegister;

    /**
     * sw命令を生成する。
     * 
     * @param srcRegister  書き込み元レジスタ番号
     * @param offset       メモリアドレス計算に使うオフセット値
     * @param baseRegister メモリアドレス計算に使うベースレジスタ番号
     */
    public SwInstruction(int srcRegister, int offset, int baseRegister) {
        this.srcRegister = srcRegister;
        this.offset = offset;
        this.baseRegister = baseRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int address = cpu.getRegister(baseRegister) + offset;
        int value = cpu.getRegister(srcRegister);
        cpu.storeWord(address, value);
    }

    @Override
    public String toAssembly() {
        return "sw " + RegisterNames.getName(srcRegister)
                + ", " + offset
                + "(" + RegisterNames.getName(baseRegister) + ")";
    }

    /**
     * 書き込み元のレジスタ番号を取得する。
     * 
     * @return 書き込み元のレジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }

    /**
     * ベースレジスタ番号を取得する。
     * 
     * @return ベースレジスタ番号
     */
    public int getBaseRegister() {
        return baseRegister;
    }

    /**
     * オフセット値を取得する。
     * 
     * @return オフセット値
     */
    public int getOffset() {
        return offset;
    }
}
