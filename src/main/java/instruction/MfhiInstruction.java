package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mfhi(move from HI)命令を表すクラス。
 * 
 * HIレジスタの値を指定したレジスタへコピーする。
 */
public class MfhiInstruction implements Instruction {

    /** 書き込み先レジスタ番号 */
    private final int destRegister;

    /**
     * mfhi命令を生成する。
     * 
     * @param destRegister 書き込み先レジスタ番号
     */
    public MfhiInstruction(int destRegister) {
        this.destRegister = destRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setRegister(destRegister, cpu.getHi());
    }

    @Override
    public String toAssembly() {
        return "mfhi " + RegisterNames.getName(destRegister);
    }

    /**
     * 書き込み先レジスタ番号を取得する。
     * 
     * @return 書き込み先レジスタ番号
     */
    public int getDestRegister() {
        return destRegister;
    }
}