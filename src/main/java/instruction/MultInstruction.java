package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mult命令を表すクラス。
 * 
 * 2つのレジスタの値を符号付き32ビット整数として乗算し、
 * 結果の上位32ビットをHIレジスタへ、
 * 下位32ビットをLOレジスタへ格納する。
 */
public class MultInstruction implements Instruction {

    /** 左オペランドのレジスタ番号 */
    private final int leftRegister;

    /** 右オペランドのレジスタ番号 */
    private final int rightRegister;

    /**
     * mult命令を生成する。
     * 
     * @param leftRegister  左オペランドのレジスタ番号
     * @param rightRegister 右オペランドのレジスタ番号
     */
    public MultInstruction(int leftRegister, int rightRegister) {
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
    }

    @Override
    public String toAssembly() {
        return "mult "
                + RegisterNames.getName(leftRegister)
                + ", "
                + RegisterNames.getName(rightRegister);
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