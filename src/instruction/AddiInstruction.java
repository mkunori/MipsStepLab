package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * addi(add immediate)命令を表すクラス。
 * 
 * レジスタの値に即値を加算し、その結果をレジスタへ格納する。
 * 
 * 例
 * {@code addi $t3, $t2, 5}
 * {@code $t2 + 5} の結果を {@code $t3} に格納している。
 */
public class AddiInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 加算元のレジスタ番号 */
    private final int srcRegister;

    /** 加算する即値 */
    private final int immediateValue;

    /**
     * addi命令を生成する。
     * 
     * @param destRegister   結果の書き込み先レジスタ番号
     * @param srcRegister    加算元のレジスタ番号
     * @param immediateValue 加算する即値
     */
    public AddiInstruction(int destRegister, int srcRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int sourceValue = cpu.getRegister(srcRegister);
        int result = sourceValue + immediateValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "addi " + RegisterNames.getName(destRegister)
                + ", " + RegisterNames.getName(srcRegister)
                + ", " + immediateValue;
    }
}
