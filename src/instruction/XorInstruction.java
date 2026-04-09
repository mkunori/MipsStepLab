package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * xor命令を表すクラス。
 * 
 * 2つのレジスタの値に対してビット単位のXOR演算を行い、
 * 結果を指定したレジスタへ格納する。
 * 
 * 例:
 * {@code xor $t0, $t1, $t2}
 * {@code $t1} と {@code $t2} のXOR演算結果を {@code $t0} に格納する。
 */
public class XorInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /**
     * xor命令を生成する。
     * 
     * @param destRegister  結果を書き込む先のレジスタ番号
     * @param leftRegister  1つ目の入力レジスタ番号
     * @param rightRegister 2つ目の入力レジスタ番号
     */
    public XorInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = leftValue ^ rightValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "xor "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(leftRegister)
                + ", "
                + RegisterNames.getName(rightRegister);
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
     * 1つ目の入力レジスタ番号を取得する。
     * 
     * @return 1つ目の入力レジスタ番号
     */
    public int getLeftRegister() {
        return leftRegister;
    }

    /**
     * 2つ目の入力レジスタ番号を取得する。
     * 
     * @return 2つ目の入力レジスタ番号
     */
    public int getRightRegister() {
        return rightRegister;
    }
}