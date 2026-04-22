package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * bltz(branch on less than zero)命令を表すクラス。
 * 
 * 指定したレジスタの値が0未満なら、指定したPCへ分岐する。
 */
public class BltzInstruction implements Instruction {

    /** 比較対象のレジスタ番号 */
    private final int srcRegister;

    /** 分岐先PC */
    private final int targetPc;

    public BltzInstruction(int srcRegister, int targetPc) {
        this.srcRegister = srcRegister;
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        if (cpu.getRegister(srcRegister) < 0) {
            cpu.setPc(targetPc);
        }
    }

    @Override
    public String toAssembly() {
        return "bltz "
                + RegisterNames.getName(srcRegister)
                + ", "
                + targetPc;
    }

    /**
     * 比較対象のレジスタ番号を取得する。
     * 
     * @return 比較対象のレジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }

    /**
     * 分岐先PCを取得する。
     * 
     * @return 分岐先PC
     */
    public int getTargetPc() {
        return targetPc;
    }
}