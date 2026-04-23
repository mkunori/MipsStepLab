package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mthi(move to HI)命令を表すクラス。
 * 
 * 指定したレジスタの値をHIレジスタへコピーする。
 */
public class MthiInstruction implements Instruction {

    /** 書き込み元レジスタ番号 */
    private final int srcRegister;

    /**
     * mthi命令を生成する。
     * 
     * @param srcRegister 書き込み元レジスタ番号
     */
    public MthiInstruction(int srcRegister) {
        this.srcRegister = srcRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setHi(cpu.getRegister(srcRegister));
    }

    @Override
    public String toAssembly() {
        return "mthi " + RegisterNames.getName(srcRegister);
    }

    /**
     * 書き込み元レジスタ番号を取得する。
     * 
     * @return 書き込み元レジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }
}