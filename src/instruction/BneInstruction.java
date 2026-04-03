package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * bne(branch if not equal)命令を表すクラス。
 * 
 * 2つのレジスタ値が等しくない場合に、指定のPCへ分岐する。
 * 
 * 例
 * {@code bne $t0, $t1, loop}
 * {@code $t0} と {@code $t1} が異なる場合、ラベル {@code loop} に対応する位置へ分岐する。
 */
public class BneInstruction implements Instruction {

    /** 比較する1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 比較する2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /** 分岐先のPC */
    private final int targetPc;

    /**
     * bne命令を生成する。
     * 
     * @param leftRegister  比較する1つ目のレジスタ番号
     * @param rightRegister 比較する2つ目のレジスタ番号
     * @param targetPc      分岐先のPC
     */
    public BneInstruction(int leftRegister, int rightRegister, int targetPc) {
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);

        if (leftValue != rightValue) {
            cpu.setPc(targetPc);
        }
    }

    @Override
    public String toAssembly() {
        return "bne " + RegisterNames.getName(leftRegister)
                + ", " + RegisterNames.getName(rightRegister)
                + ", " + targetPc;
    }
}
