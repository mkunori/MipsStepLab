package instruction;

import cpu.Cpu;

/**
 * li命令を表すクラス。
 * 
 * {@code li} は厳密にはMIPSの議事命令だが、
 * とりあえず学習用として「レジスタに値を入れる」
 * 目的で用意しておくと便利なので実装している。
 * 
 * 例
 * {@code $t0 = 10}
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
        return "li " + registerName(destRegister) + ", " + immediateValue;
    }

    /**
     * レジスタ番号を簡易的な名前へ変換する。
     *
     * @param index レジスタ番号
     * @return レジスタ名
     */
    private String registerName(int index) {
        return switch (index) {
            case 0 -> "$zero";
            case 8 -> "$t0";
            case 9 -> "$t1";
            case 10 -> "$t2";
            case 11 -> "$t3";
            default -> "$r" + index;
        };
    }
}
