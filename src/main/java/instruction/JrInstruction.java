package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * jr(jump register)命令を表すクラス。
 * 
 * 指定したレジスタの値をPCとしてジャンプする。
 */
public class JrInstruction implements Instruction {

    /** ジャンプ先を保持するレジスタ番号 */
    private final int srcRegister;

    /**
     * jr命令を生成する。
     * 
     * @param srcRegister ジャンプ先PCを保持するレジスタ番号
     */
    public JrInstruction(int srcRegister) {
        this.srcRegister = srcRegister;
    }

    /**
     * ジャンプ先を保持するレジスタ番号を返す。
     * 
     * @return レジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int targetPc = cpu.getRegister(srcRegister);
        cpu.setPc(targetPc);
    }

    @Override
    public String toAssembly() {
        return "jr " + RegisterNames.getName(srcRegister);
    }
}