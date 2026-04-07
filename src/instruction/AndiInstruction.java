package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * andi命令を表すクラス。
 * 
 * レジスタの値と即値に対してビット単位のAND演算を行い、
 * 結果を指定したレジスタへ格納する。
 * 
 * 例:
 * {@code andi $t0, $t1, 5}
 * {@code $t1 & 5} の結果を {@code $t0} に格納する。
 */
public class AndiInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** AND元のレジスタ番号 */
    private final int srcRegister;

    /** ANDする即値 */
    private final int immediateValue;

    /**
     * andi命令を生成する。
     * 
     * @param destRegister   結果を書き込む先のレジスタ番号
     * @param srcRegister    AND元のレジスタ番号
     * @param immediateValue ANDする即値
     */
    public AndiInstruction(int destRegister, int srcRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(srcRegister);
        int result = value & immediateValue;
        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "andi "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(srcRegister)
                + ", "
                + immediateValue;
    }
}