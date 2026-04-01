package instruction;

import cpu.Cpu;

/**
 * add命令を表すクラス。
 * 
 * 2つのレジスタの値を加算し、
 * その結果を別のレジスタへ格納する。
 * 
 * 例
 * {@code add $t2, $t0, $t1}
 * {@code $t0 + $t1} の結果を {@code $t2} に格納している。
 */
public class AddInstruction implements Instruction {

    /** 結果を書き込む先のレジスタ番号 */
    private final int destRegister;

    /** 1つ目の入力レジスタ番号 */
    private final int leftRegister;

    /** 2つ目の入力レジスタ番号 */
    private final int rightRegister;

    /**
     * add命令を生成する。
     * 
     * @param destRegister  結果の書き込み先レジスタ番号
     * @param leftRegister  1つ目のレジスタ番号
     * @param rightRegisger 2つ目のレジスタ番号
     */
    public AddInstruction(int destRegister, int leftRegister, int rightRegisger) {
        this.destRegister = destRegister;
        this.leftRegister = leftRegister;
        this.rightRegister = rightRegisger;
    }

    @Override
    public void execute(Cpu cpu) {
        int leftValue = cpu.getRegister(leftRegister);
        int rightValue = cpu.getRegister(rightRegister);
        int result = leftValue + rightValue;

        cpu.setRegister(destRegister, result);
    }

    @Override
    public String toAssembly() {
        return "add " + registerName(destRegister)
                + ", " + registerName(leftRegister)
                + ", " + registerName(rightRegister);
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
