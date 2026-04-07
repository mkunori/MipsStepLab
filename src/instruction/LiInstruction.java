package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * li(load immediate)命令を表すクラス。
 * 
 * {@code li} は厳密にはMIPSの疑似命令だが、
 * とりあえず学習用として「レジスタに値を入れる」
 * 目的で用意しておくと便利なので実装している。
 * 
 * 例:
 * {@code li $t0, 10}
 * {@code $t0} に10を格納している。
 */
public class LiInstruction implements Instruction {

    /** 値を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** レジスタに格納する即値 */
    private final int immediateValue;

    /**
     * li命令を生成する。
     * 
     * @param destRegister   書き込み先レジスタ番号
     * @param immediateValue 設定する即値
     */
    public LiInstruction(int destRegister, int immediateValue) {
        this.destRegister = destRegister;
        this.immediateValue = immediateValue;
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setRegister(destRegister, immediateValue);
    }

    @Override
    public String toAssembly() {
        return "li " + RegisterNames.getName(destRegister) + ", " + immediateValue;
    }
}
