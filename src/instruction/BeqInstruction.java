package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * Beq(branch on equal)命令を表すクラス。
 * 
 * 2つのレジスタ値が等しい場合に、指定のPCへ分岐する。
 * 
 * 例
 * {@code beq $t0, $t1, 5}
 * {@code $t0} と {@code $t1} が等しい場合 PC=5 へ分岐する。
 */
public class BeqInstruction implements Instruction {

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /** 分岐先のPC */
    private final int targetPc;

    /**
     * beq命令を生成する。
     * 
     * @param leftRegister  1つ目のレジスタ番号
     * @param rightRegisger 2つ目のレジスタ番号
     * @param targetPc      分岐先のPC
     */
    public BeqInstruction(int leftRegister, int rightRegister, int targetPc) {
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);

        if (leftValue == rightValue) {
            cpu.setPc(targetPc);
        }
    }

    @Override
    public String toAssembly() {
        return "beq " + RegisterNames.getName(leftRegister)
                + ", " + RegisterNames.getName(rightRegister)
                + ", " + targetPc;
    }
}
