package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * slti(set on less than immediate)命令を表すクラス。
 * 
 * レジスタの値と即値を比較し、
 * レジスタの値が即値より小さい場合は1、
 * そうでない場合は0を指定したレジスタへ格納する。
 * 
 * 例:
 * {@code slti $t0, $t1, 5}
 * {@code $t1 < 5} なら {@code $t0 = 1}、
 * そうでなければ {@code $t0 = 0} となる。
 */
public class SltiInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** 比較するレジスタ番号 */
    private final int srcRegister;

    /** 比較する即値 */
    private final int immediateValue;

    /**
     * slti命令を生成する。
     *
     * @param destRegister   結果の書き込み先レジスタ番号
     * @param srcRegister    比較するレジスタ番号
     * @param immediateValue 比較する即値
     */
    public SltiInstruction(int destRegister, int srcRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(srcRegister);
        int result = (value < immediateValue) ? 1 : 0;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "slti "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(srcRegister)
                + ", "
                + immediateValue;
    }
}