package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * add命令を表すクラス。
 * 
 * 2つのレジスタの値を加算し、
 * その結果を別のレジスタへ格納する。
 * 
 * 例:
 * {@code add $t2, $t0, $t1}
 * {@code $t0 + $t1} の結果を {@code $t2} に格納している。
 */
public class AddInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /**
     * add命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  1つ目のレジスタ番号
     * @param rightRegister 2つ目のレジスタ番号
     */
    public AddInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = leftValue + rightValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "add " + RegisterNames.getName(destRegister)
                + ", " + RegisterNames.getName(leftRegister)
                + ", " + RegisterNames.getName(rightRegister);
    }

    /**
     * 結果の書き込み先レジスタ番号を取得する。
     * 
     * @return 結果の書き込み先レジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
    }

    /**
     * 1つ目のレジスタ番号を取得する。
     * 
     * @return 1つ目のレジスタ番号
     */
    public int getLeftRegister() {
        return leftRegister;
    }

    /**
     * 2つ目のレジスタ番号を取得する。
     * 
     * @return 2つ目のレジスタ番号
     */
    public int getRightRegister() {
        return rightRegister;
    }
}
