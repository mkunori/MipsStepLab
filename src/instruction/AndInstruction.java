package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * and命令を表すクラス。
 *
 * 2つのレジスタの値に対してビット単位のAND演算を行い、
 * 結果を指定したレジスタへ格納する。
 *
 * 例:
 * {@code and $t0, $t1, $t2}
 * {@code $t1} と {@code $t2} のAND演算結果を {@code $t0} に格納する。
 */
public class AndInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /**
     * and命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  1つ目の入力レジスタ番号
     * @param rightRegister 2つ目の入力レジスタ番号
     */
    public AndInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = leftValue & rightValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "and "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(leftRegister)
                + ", "
                + RegisterNames.getName(rightRegister);
    }
}