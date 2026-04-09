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
    private final int sourceRegister;

    /**
     * jr命令を生成する。
     * 
     * @param sourceRegister ジャンプ先PCを保持するレジスタ番号
     */
    public JrInstruction(int sourceRegister) {
        this.sourceRegister = sourceRegister;
    }

    /**
     * ジャンプ先を保持するレジスタ番号を返す。
     * 
     * @return レジスタ番号
     */
    public int getSourceRegister() {
        return sourceRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int targetPc = cpu.getRegister(sourceRegister);
        cpu.setPc(targetPc);
    }

    @Override
    public String toAssembly() {
        return "jr " + RegisterNames.getName(sourceRegister);
    }
}