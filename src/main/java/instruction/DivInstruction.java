package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * div命令を表すクラス。
 * 
 * 2つのレジスタの値を符号付き32ビット整数として除算し、
 * 商をLOレジスタへ、余りをHIレジスタへ格納する。
 */
public class DivInstruction implements Instruction {

    /** 被除数のレジスタ番号 */
    private final int leftRegister;

    /** 除数のレジスタ番号 */
    private final int rightRegister;

    /**
     * div命令を生成する。
     * 
     * @param leftRegister  被除数のレジスタ番号
     * @param rightRegister 除数のレジスタ番号
     */
    public DivInstruction(int leftRegister, int rightRegister) {
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);

        // 0除算はJavaの仕様に任せる
        int quotient = leftValue / rightValue;
        int remainder = leftValue % rightValue;

        cpu.setLo(quotient);
        cpu.setHi(remainder);
    }

    @Override
    public String toAssembly() {
        return "div "
                + RegisterNames.getName(leftRegister)
                + ", "
                + RegisterNames.getName(rightRegister);
    }

    /**
     * 被除数のレジスタ番号を取得する。
     * 
     * @return 被除数のレジスタ番号
     */
    public int getLeftRegister() {
        return leftRegister;
    }

    /**
     * 除数のレジスタ番号を取得する。
     * 
     * @return 除数のレジスタ番号
     */
    public int getRightRegister() {
        return rightRegister;
    }
}