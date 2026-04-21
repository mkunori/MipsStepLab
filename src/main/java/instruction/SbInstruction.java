package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sb(store byte)命令を表すクラス。
 * 
 * レジスタの下位8ビットを
 * メモリへ1バイト書き込む。
 */
public class SbInstruction implements Instruction {

    /** 書き込み元レジスタ番号 */
    private final int srcRegister;

    /** オフセット値 */
    private final int offset;

    /** ベースレジスタ番号 */
    private final int baseRegister;

    /**
     * sb命令を生成する。
     * 
     * @param srcRegister  書き込み元レジスタ番号
     * @param offset       オフセット値
     * @param baseRegister ベースレジスタ番号
     */
    public SbInstruction(int srcRegister, int offset, int baseRegister) {
        this.srcRegister = srcRegister;
        this.offset = offset;
        this.baseRegister = baseRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int address = cpu.getRegister(baseRegister) + offset;
        int value = cpu.getRegister(srcRegister);
        cpu.storeByte(address, value);
    }

    @Override
    public String toAssembly() {
        return "sb "
                + RegisterNames.getName(srcRegister)
                + ", "
                + offset
                + "(" + RegisterNames.getName(baseRegister) + ")";
    }

    /**
     * 書き込み元レジスタ番号を取得する。
     * 
     * @return 書き込み元レジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
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