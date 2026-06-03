package web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import execution.StepResult;

/**
 * StepResultをWeb画面表示用のデータに変換するクラス。
 *
 * StepResultは、1ステップ実行した結果そのものを表す。
 * 一方、Web画面では「変更されたレジスタだけ」「変更されたHI/LOだけ」
 * のように、表示しやすい形に加工したい。
 *
 * このクラスは、その変換処理を担当する。
 */
@Component
public class StepResultViewMapper {

    /** Web画面に表示するメモリの先頭バイト数。 */
    private static final int MEMORY_DISPLAY_SIZE = 96;

    /**
     * StepResultからレジスタ変更差分のリストを作成する。
     *
     * 実行前と実行後のレジスタ配列を比較し、
     * 値が変わったレジスタだけをRegisterDiffとして返す。
     *
     * @param result 1ステップ分の実行結果
     * @return 変更されたレジスタの差分リスト
     */
    public List<RegisterDiff> createRegisterDiffs(StepResult result) {
        int[] before = result.getRegistersBefore();
        int[] after = result.getRegistersAfter();

        List<RegisterDiff> diffs = new ArrayList<>();

        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                diffs.add(new RegisterDiff(i, before[i], after[i]));
            }
        }

        return diffs;
    }

    /**
     * StepResultからHI/LOレジスタ変更差分のリストを作成する。
     *
     * 実行前と実行後のHI/LOレジスタを比較し、
     * 値が変わったものだけをHiLoDiffとして返す。
     *
     * @param result 1ステップ分の実行結果
     * @return 変更されたHI/LOレジスタの差分リスト
     */
    public List<HiLoDiff> createHiLoDiffs(StepResult result) {
        List<HiLoDiff> diffs = new ArrayList<>();

        if (result.getHiBefore() != result.getHiAfter()) {
            diffs.add(new HiLoDiff("HI", result.getHiBefore(), result.getHiAfter()));
        }

        if (result.getLoBefore() != result.getLoAfter()) {
            diffs.add(new HiLoDiff("LO", result.getLoBefore(), result.getLoAfter()));
        }

        return diffs;
    }

    /**
     * 実行された命令を画面表示用の文字列として取得する。
     *
     * 現時点ではInstructionのtoString()を全命令に実装していないため、
     * StepResultに含まれるInstructionではなく、元の入力文字列から
     * 実行前PCに対応する行を取り出して表示する。
     *
     * @param result       1ステップ分の実行結果
     * @param programLines 行ごとに分割した入力プログラム
     * @return 実行された命令の表示文字列
     */
    public String getExecutedInstructionText(StepResult result, List<String> programLines) {
        int pc = result.getPcBefore();

        if (pc < 0 || pc >= programLines.size()) {
            return "";
        }

        return programLines.get(pc);
    }

    /**
     * StepResultから実行命令の表示用データを作成する。
     *
     * 画面では、実行ステップ番号、実行前後のPC、実行された命令文字列を
     * まとめて表示するため、専用の表示用データに変換する。
     *
     * @param result       1ステップ分の実行結果
     * @param programLines 行ごとに分割した入力プログラム
     * @return 実行命令表示用データ
     */
    public ExecutedInstructionView createExecutedInstructionView(
            StepResult result,
            List<String> programLines) {

        return new ExecutedInstructionView(
                result.getStep(),
                result.getPcBefore(),
                result.getPcAfter(),
                getExecutedInstructionText(result, programLines));
    }

    /**
     * StepResultからメモリ変更差分のリストを作成する。
     *
     * 実行前と実行後のメモリ配列を比較し、
     * 値が変わったアドレスだけをMemoryDiffとして返す。
     *
     * @param result 1ステップ分の実行結果
     * @return 変更されたメモリの差分リスト
     */
    public List<MemoryDiff> createMemoryDiffs(StepResult result) {
        byte[] before = result.getMemoryBefore();
        byte[] after = result.getMemoryAfter();

        List<MemoryDiff> diffs = new ArrayList<>();

        for (int address = 0; address < before.length; address++) {
            if (before[address] != after[address]) {
                diffs.add(new MemoryDiff(address, before[address], after[address]));
            }
        }

        return diffs;
    }

    /**
     * StepResultから実行後のレジスタ一覧を作成する。
     *
     * 実行後のレジスタ値に加えて、今回のステップで変更されたかどうかも設定する。
     *
     * @param result 1ステップ分の実行結果
     * @return 実行後のレジスタ一覧
     */
    public List<RegisterValue> createRegisterValues(StepResult result) {
        int[] before = result.getRegistersBefore();
        int[] after = result.getRegistersAfter();

        List<RegisterValue> values = new ArrayList<>();

        for (int i = 0; i < after.length; i++) {
            boolean changed = before[i] != after[i];

            values.add(new RegisterValue(i, after[i], changed));
        }

        return values;
    }

    /**
     * StepResultから実行後のHI/LOレジスタ一覧を作成する。
     *
     * 実行後のHI/LOの値に加えて、
     * 今回のステップで変更されたかどうかも設定する。
     *
     * @param result 1ステップ分の実行結果
     * @return 実行後のHI/LOレジスタ一覧
     */
    public List<HiLoValue> createHiLoValues(StepResult result) {
        List<HiLoValue> values = new ArrayList<>();

        values.add(new HiLoValue(
                "HI",
                result.getHiAfter(),
                result.getHiBefore() != result.getHiAfter()));

        values.add(new HiLoValue(
                "LO",
                result.getLoAfter(),
                result.getLoBefore() != result.getLoAfter()));

        return values;
    }

    /**
     * StepResultからWeb表示用データ一式を作成する。
     *
     * @param result 1ステップ分の実行結果
     * @return Web表示用データ一式
     */
    public StepResultViewData toViewData(StepResult result) {
        return new StepResultViewData(
                createRegisterDiffs(result),
                createRegisterValues(result),
                createHiLoDiffs(result),
                createHiLoValues(result),
                createMemoryDiffs(result),
                createMemoryValues(result));
    }

    /**
     * StepResultから実行後のメモリ現在値一覧を作成する。
     *
     * 現時点では簡易表示として、先頭から一定バイト数だけを表示する。
     * さらに、今回のステップで変更されたアドレスにはchanged=trueを設定する。
     *
     * @param result 1ステップ分の実行結果
     * @return 実行後のメモリ現在値一覧
     */
    public List<MemoryValue> createMemoryValues(StepResult result) {
        byte[] before = result.getMemoryBefore();
        byte[] after = result.getMemoryAfter();

        List<MemoryValue> values = new ArrayList<>();

        int displaySize = Math.min(MEMORY_DISPLAY_SIZE, after.length);

        for (int address = 0; address < displaySize; address++) {
            boolean changed = before[address] != after[address];

            values.add(new MemoryValue(address, after[address], changed));
        }

        return values;
    }
}