package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * lh(load halfword)命令を表すクラス。
 * 
 * メモリから2バイト読み込み、
 * 符号拡張してレジスタへ格納する。
 */
public class LhInstruction implements Instruction {

    /** 読み込み先レジスタ番号 */
    private final int destRegister;

    /** オフセット値 */
    private final int offset;

    /** ベースレジスタ番号 */
    private final int baseRegister;

    /**
     * lh命令を生成する。
     * 
     * @param destRegister 読み込み先レジスタ番号
     * @param offset       オフセット値
     * @param baseRegister ベースレジスタ番号
     */
    public LhInstruction(int destRegister, int offset, int baseRegister) {
        this.destRegister = destRegister;
        this.offset = offset;
        this.baseRegister = baseRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int address = cpu.getRegister(baseRegister) + offset;
        int value = cpu.loadHalfWord(address);
        cpu.setRegister(destRegister, value);
    }

    @Override
    public String toAssembly() {
        return "lh "
                + RegisterNames.getName(destRegister)
                + ", "
                + offset
                + "(" + RegisterNames.getName(baseRegister) + ")";
    }

    /**
     * 読み込み先レジスタ番号を取得する。
     * 
     * @return 読み込み先レジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
    }

    /**
     * オフセット値を取得する。
     * 
     * @return オフセット値
     */
    public int getOffset() {
        return offset;
    }

    /**
     * ベースレジスタ番号を取得する。
     * 
     * @return ベースレジスタ番号
     */
    public int getBaseRegister() {
        return baseRegister;
    }
}