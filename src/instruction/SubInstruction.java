package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sub命令を表すクラス。
 * 
 * 2つのレジスタの値を減算し、
 * その結果を別のレジスタへ格納する。
 * 
 * 例
 * {@code sub $t3, $t2, $t0}
 * {@code $t2 - $t0} の結果を {@code $t3} に格納している。
 */
public class SubInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /**
     * sub命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  1つ目のレジスタ番号
     * @param rightRegisger 2つ目のレジスタ番号
     */
    public SubInstruction(int destRegister, int leftRegister, int rightRegisger) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegisger;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = leftValue - rightValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "sub " + RegisterNames.getName(destRegister)
                + ", " + RegisterNames.getName(leftRegister)
                + ", " + RegisterNames.getName(rightRegister);
    }
}
