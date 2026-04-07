package instruction;

import cpu.Cpu;
import cpu.RegisterNames;

/**
 * lw(load word)命令を表すクラス。
 * 
 * base + offset で求めたメモリ位置から値を読み込み、レジスタへ格納する。
 * 
 * 例:
 * {@code sw $t0, 4($t1)}
 * メモリ位置 {@code $t1 + 4} の値を {@code $t0} に読み込む。
 */
public class LwInstruction implements Instruction {

    /** 読み込み先のレジスタ番号 */
    private final int destRegister;

    /** メモリアドレス計算に使うオフセット値 */
    private final int offset;

    /** メモリアドレス計算に使うベースレジスタ番号 */
    private final int baseRegister;

    /**
     * lw命令を生成する。
     * 
     * @param destRegister 読み込み先レジスタ番号
     * @param offset       メモリアドレス計算に使うオフセット値
     * @param baseRegister メモリアドレス計算に使うベースレジスタ番号
     */
    public LwInstruction(int destRegister, int offset, int baseRegister) {
        this.destRegister = destRegister;
        this.offset = offset;
        this.baseRegister = baseRegister;
    }

    @Override
    public void execute(Cpu cpu) {
        int address = cpu.getRegister(baseRegister) + offset;
        int value = cpu.loadWord(address);
        cpu.setRegister(destRegister, value);
    }

    @Override
    public String toAssembly() {
        return "lw " + RegisterNames.getName(destRegister)
                + ", " + offset
                + "(" + RegisterNames.getName(baseRegister) + ")";
    }
}
