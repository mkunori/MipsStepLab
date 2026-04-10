package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sltu(set on less than unsigned)命令を表すクラス。
 * 
 * 2つのレジスタの値を符号なし整数として比較し、
 * 左辺が右辺より小さい場合は1、
 * そうでない場合は0を指定したレジスタへ格納する。
 */
public class SltuInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** 比較する左辺レジスタ番号 */
    private final int leftRegister;

    /** 比較する右辺レジスタ番号 */
    private final int rightRegister;

    /**
     * sltu命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  比較する左辺レジスタ番号
     * @param rightRegister 比較する右辺レジスタ番号
     */
    public SltuInstruction(int destRegister, int leftRegister, int rightRegister) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = (Integer.compareUnsigned(leftValue, rightValue) < 0) ? 1 : 0;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "sltu "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(leftRegister)
                + ", "
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
     * 比較する左辺レジスタ番号を取得する。
     *
     * @return 比較する左辺レジスタ番号
     */
    public int getLeftRegister() {
        return leftRegister;
    }

    /**
     * 比較する右辺レジスタ番号を取得する。
     *
     * @return 比較する右辺レジスタ番号
     */
    public int getRightRegister() {
        return rightRegister;
    }
}