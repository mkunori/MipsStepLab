package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * mtlo(move to LO)命令を表すクラス。
 * 
 * 指定したレジスタの値をLOレジスタへコピーする。
 */
public class MtloInstruction implements Instruction {

    /** 書き込み元レジスタ番号 */
    private final int srcRegister;

    /**
     * mtlo命令を生成する。
     * 
     * @param srcRegister 書き込み元レジスタ番号
     */
    public MtloInstruction(int srcRegister) {
        this.srcRegister = srcRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setLo(cpu.getRegister(srcRegister));
    }

    @Override
    public String toAssembly() {
        return "mtlo " + RegisterNames.getName(srcRegister);
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