package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * lui(load upper immediate)命令を表すクラス。
 * 
 * 即値を左に16ビットシフトし、
 * 上位16ビットに設定する。
 */
public class LuiInstruction implements Instruction {

    /** 書き込み先レジスタ番号 */
    private final int destRegister;

    /** 即値 */
    private final int immediateValue;

    /**
     * lui命令を生成する。
     * 
     * @param destRegister   書き込み先レジスタ番号
     * @param immediateValue 即値
     */
    public LuiInstruction(int destRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        int result = immediateValue << 16;
        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "lui "
                + RegisterNames.getName(destRegister)
                + ", "
                + immediateValue;
    }

    /**
     * 書き込み先レジスタ番号を取得する。
     * 
     * @return 書き込み先レジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
    }

    /**
     * 即値を取得する。
     * 
     * @return 即値
     */
    public int getImmediateValue() {
        return immediateValue;
    }
}