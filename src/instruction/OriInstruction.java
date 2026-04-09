package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * ori命令を表すクラス。
 * 
 * レジスタの値と即値に対してビット単位のOR演算を行い、
 * 結果を指定したレジスタへ格納する。
 * 
 * 例:
 * {@code ori $t0, $t1, 5}
 * {@code $t1 | 5} の結果を {@code $t0} に格納する。
 */
public class OriInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** OR元のレジスタ番号 */
    private final int srcRegister;

    /** ORする即値 */
    private final int immediateValue;

    /**
     * ori命令を生成する。
     * 
     * @param destRegister   結果を書き込む先のレジスタ番号
     * @param srcRegister    OR元のレジスタ番号
     * @param immediateValue ORする即値
     */
    public OriInstruction(int destRegister, int srcRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(srcRegister);
        int result = value | immediateValue;
        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "ori "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(srcRegister)
                + ", "
                + immediateValue;
    }

    /**
     * 結果を書き込む先のレジスタ番号を取得する。
     * 
     * @return 結果を書き込む先のレジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
    }

    /**
     * OR元のレジスタ番号を取得する。
     * 
     * @return OR元のレジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }

    /**
     * ORする即値を取得する。
     * 
     * @return ORする即値
     */
    public int getImmediateValue() {
        return immediateValue;
    }
}