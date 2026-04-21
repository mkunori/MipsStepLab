package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mflo(move from LO)命令を表すクラス。
 * 
 * LOレジスタの値を指定したレジスタへコピーする。
 */
public class MfloInstruction implements Instruction {

    /** 書き込み先レジスタ番号 */
    private final int destRegister;

    /**
     * mflo命令を生成する。
     * 
     * @param destRegister 書き込み先レジスタ番号
     */
    public MfloInstruction(int destRegister) {
        this.destRegister = destRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setRegister(destRegister, cpu.getLo());
    }

    @Override
    public String toAssembly() {
        return "mflo " + RegisterNames.getName(destRegister);
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