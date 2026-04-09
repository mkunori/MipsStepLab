import java.util.List;

import cpu.Cpu;
import debug.StepRunner;
import debug.StepView;
import instruction.Instruction;
import parser.InstructionParser;

/**
 * MIPS風シミュレータの動作確認を行うメインクラス。
 * 
 * アセンブリ文字列をパースして命令列を生成し、
 * デバッグビュー風の表示で1ステップずつ実行結果を確認する。
 */
public class MSLMain {
    /**
     * アプリケーションのエントリーポイント。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        Cpu cpu = new Cpu();
        InstructionParser parser = new InstructionParser();

        // 実行するサンプルを選ぶ
        // List<String> source = createFullSample();
        // List<String> source = createBranchSample();
        // List<String> source = createMemorySample();
        // List<String> source = createCallSample();
        // List<String> source = createArithmeticSample();
        List<String> source = createLogicSample();

        List<Instruction> program = parser.parse(source);

        StepView view = new StepView();
        StepRunner runner = new StepRunner(cpu, program, view);
        runner.runInteractive();
    }

    /**
     * 分岐命令の動作確認用サンプルを返す。
     * 
     * {@code beq}、{@code bne}、{@code j} の動作を確認するためのサンプル。
     * 
     * @return 分岐サンプル
     */
    private static List<String> createBranchSample() {
        return List.of(
                "# 分岐サンプル",
                "# beq, bne, j の確認",

                "li $t0, 10",
                "li $t1, 10",

                "# beq成立",
                "beq $t0, $t1, equal",
                "li $v0, 0",
                "j afterEqual",

                "equal: li $v0, 1",
                "afterEqual: addi $v0, $v0, 10",

                "li $t0, 3",
                "li $t1, 5",

                "# bne成立",
                "bne $t0, $t1, notEqual",
                "li $v0, 100",
                "j end",

                "notEqual: addi $v0, $v0, 20",
                "end: addi $v0, $v0, 1");
    }

    /**
     * メモリ操作の動作確認用サンプルを返す。
     * 
     * {@code sw} でメモリへ格納し、{@code lw} で読み戻して
     * 値を加工する流れを確認するためのサンプル。
     * 
     * @return メモリサンプル
     */
    private static List<String> createMemorySample() {
        return List.of(
                "# メモリサンプル",
                "# lw, sw の確認",

                "li $t0, 10", // ベースアドレス
                "li $t1, 3", // 保存する値

                "sw $t1, 0($t0)", // mem[10] = 3
                "lw $t2, 0($t0)", // $t2 = 3
                "addi $t2, $t2, 7", // $t2 = 10
                "sw $t2, 1($t0)", // mem[11] = 10
                "lw $v0, 1($t0)" // $v0 = 10
        );
    }

    /**
     * 代表サンプルを返す。
     * 
     * 1から5までの値を順番にメモリへ保存するループ。
     * 分岐、加算、メモリ操作を組み合わせて、
     * 現在の命令セットでできることを自然な流れで確認できる。
     * 
     * @return 代表サンプル
     */
    private static List<String> createFullSample() {
        return List.of(
                "# 代表サンプル",
                "# 1から5までをメモリに順に保存する",
                "# 結果: mem[0] = 1, mem[1] = 2, ..., mem[4] = 5",

                "li $t0, 0", // 書き込み先アドレス
                "li $t1, 1", // 書き込む値
                "li $t2, 6", // 終了判定用（6になったら終了）

                "loop: sw $t1, 0($t0)",
                "addi $t0, $t0, 1",
                "addi $t1, $t1, 1",
                "bne $t1, $t2, loop",

                "# 最後に mem[4] を読んで確認",
                "lw $v0, 4($zero)");
    }

    /**
     * jal / jr の動作確認用サンプルを返す。
     * 
     * @return 関数呼び出しサンプル
     */
    private static List<String> createCallSample() {
        return List.of(
                "# jal / jr サンプル",
                "li $t0, 10",
                "jal func",
                "j end",
                "func: addi $t0, $t0, 5",
                "jr $ra",
                "end: li $v0, 1");
    }

    /**
     * add / sub / addi の動作確認用サンプルを返す。
     * 
     * @return 算術サンプル
     */
    private static List<String> createArithmeticSample() {
        return List.of(
                "# 算術サンプル",
                "li $t0, 10",
                "li $t1, 3",
                "add $t2, $t0, $t1",
                "sub $t3, $t0, $t1",
                "addi $t4, $t1, 7");
    }

    /**
     * and / or / xor / andi / nor の動作確認用サンプルを返す。
     * 
     * @return 論理サンプル
     */
    private static List<String> createLogicSample() {
        return List.of(
                "# 論理サンプル",
                "li $t0, 10",
                "li $t1, 5",
                "and $t2, $t0, $t1",
                "or $t3, $t0, $t1",
                "xor $t4, $t0, $t1",
                "nor $t2, $t0, $t1",
                "andi $t3, $t0, 6");
    }
}