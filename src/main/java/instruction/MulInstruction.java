package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mul擬似命令を表すクラス。
 * 
 * 2つのレジスタの値を符号付き32ビット整数として乗算し、
 * 結果の下位32ビットを指定レジスタへ格納する。
 * あわせて、HI/LOレジスタも更新する。
 */
public class MulInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** 左オペランドのレジスタ番号 */
    private final int leftRegister;

    /** 右オペランドのレジスタ番号 */
    private final int rightRegister;

    public MulInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        long leftValue = cpu.getRegister(leftRegister);
        long rightValue = cpu.getRegister(rightRegister);
        long result = leftValue * rightValue;

        cpu.setHi((int) (result >>> 32));
        cpu.setLo((int) result);

        cpu.setRegister(destRegister, (int) result);
    }

    @Override
    public String toAssembly() {
        return "mul "
                + RegisterNames.getName(destRegister) + ", "
                + RegisterNames.getName(leftRegister) + ", "
                + RegisterNames.getName(rightRegister);
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
     * 左オペランドのレジスタ番号を取得する。
     * 
     * @return 左オペランドのレジスタ番号
     */
    public int getLeftRegister() {
        return leftRegister;
    }

    /**
     * 右オペランドのレジスタ番号を取得する。
     * 
     * @return 右オペランドのレジスタ番号
     */
    public int getRightRegister() {
        return rightRegister;
    }
}