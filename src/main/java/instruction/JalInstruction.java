package instruction;

import cpu.Cpu;

/**
 * jal(jump and link)命令を表すクラス。
 * 
 * 次の命令位置を {@code $ra} に保存し、
 * 指定したPCへジャンプする。
 */
public class JalInstruction implements Instruction {

    /** 戻り先アドレスを保存するレジスタ番号 ({@code $ra}) */
    private static final int RETURN_ADDRESS_REGISTER = 31;

    /** ジャンプ先のPC */
    private final int targetPc;

    /**
     * jal命令を生成する。
     * 
     * @param targetPc ジャンプ先のPC
     */
    public JalInstruction(int targetPc) {
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        int returnPc = cpu.getPc() + 1;
        cpu.setRegister(RETURN_ADDRESS_REGISTER, returnPc);
        cpu.setPc(targetPc);
    }

    @Override
    public String toAssembly() {
        return "jal " + targetPc;
    }
}