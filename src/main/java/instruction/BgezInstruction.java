package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * bgez(branch on greater than or equal to zero)命令を表すクラス。
 * 
 * 指定したレジスタの値が0以上なら、指定したPCへ分岐する。
 */
public class BgezInstruction implements Instruction {

    /** 比較対象のレジスタ番号 */
    private final int srcRegister;

    /** 分岐先PC */
    private final int targetPc;

    /**
     * bgez命令を生成する。
     *
     * @param srcRegister 比較対象のレジスタ番号
     * @param targetPc    分岐先PC
     */
    public BgezInstruction(int srcRegister, int targetPc) {
        this.srcRegister = srcRegister;
        this.targetPc = targetPc;
    }

    @Override
    public void execute(Cpu cpu) {
        if (cpu.getRegister(srcRegister) >= 0) {
            cpu.setPc(targetPc);
        }
    }

    @Override
    public String toAssembly() {
        return "bgez "
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