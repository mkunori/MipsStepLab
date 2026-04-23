package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * rem擬似命令を表すクラス。
 * 
 * 2つのレジスタの値を符号付き32ビット整数として除算し、
 * 余りを指定したレジスタへ格納する。
 * あわせて、div命令と同様に商をLO、余りをHIへ設定する。
 */
public class RemInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** 被除数のレジスタ番号 */
    private final int leftRegister;

    /** 除数のレジスタ番号 */
    private final int rightRegister;

    /**
     * rem擬似命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  被除数のレジスタ番号
     * @param rightRegister 除数のレジスタ番号
     */
    public RemInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);

        if (rightValue == 0) {
            throw new ArithmeticException("0で割ることはできません");
        }

        int quotient = leftValue / rightValue;
        int remainder = leftValue % rightValue;

        cpu.setLo(quotient);
        cpu.setHi(remainder);
        cpu.setRegister(destRegister, remainder);
    }

    @Override
    public String toAssembly() {
        return "rem "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(leftRegister)
                + ", "
                + RegisterNames.getName(rightRegister);
    }

    /**
     * 結果の書き込み先レジスタ番号を取得する。
     * 
     * @return 書き込み先レジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
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