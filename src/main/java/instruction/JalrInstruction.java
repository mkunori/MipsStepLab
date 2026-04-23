package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * jalr(jump and link register)命令を表すクラス。
 * 
 * 次に実行する命令位置を$raへ保存し、
 * 指定したレジスタが指すPCへジャンプする。
 */
public class JalrInstruction implements Instruction {

    /** ジャンプ先PCを保持するレジスタ番号 */
    private final int srcRegister;

    /**
     * jalr命令を生成する。
     * 
     * @param srcRegister ジャンプ先PCを保持するレジスタ番号
     */
    public JalrInstruction(int srcRegister) {
        this.srcRegister = srcRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int returnAddress = cpu.getPc() + 1;
        int targetPc = cpu.getRegister(srcRegister);

        cpu.setRegister(31, returnAddress); // $ra
        cpu.setPc(targetPc);
    }

    @Override
    public String toAssembly() {
        return "jalr " + RegisterNames.getName(srcRegister);
    }

    /**
     * ジャンプ先PCを保持するレジスタ番号を取得する。
     * 
     * @return ジャンプ先レジスタ番号
     */
    public int getSrcRegister() {
        return srcRegister;
    }
}