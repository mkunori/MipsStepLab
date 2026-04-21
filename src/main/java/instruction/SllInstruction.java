package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * sll(shift left logical)命令を表すクラス。
 * 
 * レジスタの値を左にシフトし、
 * 結果を指定したレジスタへ格納する。
 * 
 * 例:
 * {@code sll $t0, $t1, 2}
 * {@code $t1 << 2} の結果を {@code $t0} に格納する。
 */
public class SllInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** シフト元のレジスタ番号 */
    private final int srcRegister;

    /** シフト量 */
    private final int shiftAmount;

    /**
     * sll命令を生成する。
     *
     * @param destRegister 結果の書き込み先レジスタ番号
     * @param srcRegister  シフト元のレジスタ番号
     * @param shiftAmount  シフト量
     */
    public SllInstruction(int destRegister, int srcRegister, int shiftAmount) {
        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
        this.shiftAmount = shiftAmount;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(srcRegister);
        int result = value << shiftAmount;
        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "sll "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(srcRegister)
                + ", "
                + shiftAmount;
    }
}