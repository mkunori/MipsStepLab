package instruction;

import cpu.Cpu;

/**
 * j(jump)命令を表すクラス。
 *
 * 指定したPCへ無条件にジャンプする。
 */
public class JumpInstruction implements Instruction {

    /** ジャンプ先のPC */
    private final int targetPc;

    /**
     * j命令を生成する。
     * 
     * @param targetPc ジャンプ先のPC
     */
    public JumpInstruction(int targetPc) {
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setPc(targetPc);
    }

    @Override
    public String toAssembly() {
        return "j " + targetPc;
    }
}