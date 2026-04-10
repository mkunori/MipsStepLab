package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sltiu(set on less than immediate unsigned)命令を表すクラス。
 * 
 * レジスタの値と即値を符号なし整数として比較し、
 * レジスタの値が即値より小さい場合は1、
 * そうでない場合は0を指定したレジスタへ格納する。
 */
public class SltiuInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** 比較するレジスタ番号 */
    private final int srcRegister;

    /** 比較する即値 */
    private final int immediateValue;

    /**
     * sltiu命令を生成する。
     * 
     * @param destRegister   結果の書き込み先レジスタ番号
     * @param srcRegister    比較するレジスタ番号
     * @param immediateValue 比較する即値
     */
    public SltiuInstruction(int destRegister, int srcRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(srcRegister);
        int result = (Integer.compareUnsigned(value, immediateValue) < 0) ? 1 : 0;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "sltiu "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(srcRegister)
                + ", "
                + immediateValue;
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
     * 比較するレジスタ番号を取得する。
     *
     * @return 比較するレジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }

    /**
     * 比較する即値を取得する。
     *
     * @return 比較する即値
     */
    public int getImmediateValue() {
        return immediateValue;
    }
}