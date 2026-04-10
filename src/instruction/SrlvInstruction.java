package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * srlv(shift right logical variable)命令を表すクラス。
 * 
 * 指定したレジスタの値を、
 * 別のレジスタで指定したビット数だけ論理右シフトする。
 */
public class SrlvInstruction implements Instruction {

    /** 結果の書き込み先レジスタ番号 */
    private final int destRegister;

    /** シフト対象のレジスタ番号 */
    private final int valueRegister;

    /** シフト量を保持するレジスタ番号 */
    private final int shiftRegister;

    /**
     * srlv命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param valueRegister シフト対象のレジスタ番号
     * @param shiftRegister シフト量を保持するレジスタ番号
     */
    public SrlvInstruction(int destRegister, int valueRegister, int shiftRegister) {
        this.destRegister = destRegister;
        this.valueRegister = valueRegister;
        this.shiftRegister = shiftRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int value = cpu.getRegister(valueRegister);
        int shiftAmount = cpu.getRegister(shiftRegister);
        int result = value >>> shiftAmount;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "srlv "
                + RegisterNames.getName(destRegister)
                + ", "
                + RegisterNames.getName(valueRegister)
                + ", "
                + RegisterNames.getName(shiftRegister);
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
     * シフト対象のレジスタ番号を取得する。
     * 
     * @return シフト対象のレジスタ番号
     */
    public int getValueRegister() {
        return valueRegister;
    }

    /**
     * シフト量を保持するレジスタ番号を取得する。
     * 
     * @return シフト量を保持するレジスタ番号
     */
    public int getShiftRegister() {
        return shiftRegister;
    }
}