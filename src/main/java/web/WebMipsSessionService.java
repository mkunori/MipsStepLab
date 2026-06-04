package web;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import execution.StepResult;
import instruction.Instruction;
import parser.InstructionParser;

/**
 * Web版MipsStepLabの実行状態を扱うServiceクラス。
 *
 * Serviceは、Controllerから呼び出されて、
 * アプリケーションの処理本体を担当するクラスである。
 *
 * HomeControllerは画面からのリクエストを受け取る役割に集中し、
 * MIPSプログラムの解析や実行状態の作成はこのクラスに任せる。
 */
@Service
public class WebMipsSessionService {

    /** run操作で一度に実行できる最大ステップ数。 */
    private static final int MAX_RUN_STEPS = 1000;

    /** 入力プログラム全体の最大文字数。 */
    private static final int MAX_PROGRAM_TEXT_LENGTH = 10_000;

    /** 入力プログラムの最大行数。 */
    private static final int MAX_PROGRAM_LINE_COUNT = 200;

    /** 入力プログラム1行あたりの最大文字数。 */
    private static final int MAX_PROGRAM_LINE_LENGTH = 200;

    /**
     * 入力されたプログラム文字列から、新しいWebMipsSessionを作成する。
     *
     * 入力サイズを検証してから、MIPS命令として解析する。
     *
     * @param programText ユーザーが入力したMIPSプログラム文字列
     * @return 新しい実行状態
     */
    public WebMipsSession createSession(String programText) {
        validateProgramText(programText);

        List<String> programLines = splitLines(programText);
        List<Instruction> instructions = parseProgram(programLines);

        return WebMipsSession.create(programText, instructions);
    }

    /**
     * 既存の実行状態を、同じプログラムで初期状態に戻す。
     *
     * @param oldSession 現在の実行状態
     * @return 初期化された新しい実行状態
     */
    public WebMipsSession resetSession(WebMipsSession oldSession) {
        return createSession(oldSession.getProgramText());
    }

    /**
     * 現在の実行状態を1ステップ進める。
     *
     * 実行したPCは、実行済みPCとしてWebMipsSessionに記録する。
     *
     * @param session 現在の実行状態
     * @return 1ステップ分の実行結果
     */
    public StepResult step(WebMipsSession session) {
        StepResult result = session.getStepRunner().step();

        session.markExecuted(result.getPcBefore());

        return result;
    }

    /**
     * 実行状態に、次に実行できる命令が残っているか判定する。
     *
     * @param session 現在の実行状態
     * @return 次に実行できる命令がある場合はtrue
     */
    public boolean canStep(WebMipsSession session) {
        return session.getStepRunner().hasNext();
    }

    /**
     * 入力されたプログラム文字列を行ごとのリストに変換する。
     *
     * 空行は命令として扱わないため、除外する。
     *
     * @param text 入力されたプログラム文字列
     * @return 空行を除いた命令行のリスト
     */
    public List<String> splitLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    /**
     * 入力されたプログラム文字列に含まれる命令数を返す。
     *
     * @param session 実行状態
     * @return 実行対象の命令数
     */
    public int getInstructionCount(WebMipsSession session) {
        return session.getProgram().size();
    }

    /**
     * 行ごとの文字列をInstructionのリストに変換する。
     *
     * InstructionParserは、プログラム全体をまとめて解析する。
     * これは、ラベル定義と分岐先を対応付けるためである。
     *
     * @param programLines MIPS命令を表す文字列のリスト
     * @return 解析されたInstructionのリスト
     */
    private List<Instruction> parseProgram(List<String> programLines) {
        InstructionParser parser = new InstructionParser();

        return parser.parse(programLines);
    }

    /**
     * ブレークポイントを追加する。
     *
     * @param session 現在の実行状態
     * @param pc      追加するPC番号
     */
    public void addBreakpoint(WebMipsSession session, int pc) {
        validateProgramPc(session, pc);

        session.getBreakpointManager().add(pc);
    }

    /**
     * ブレークポイントを削除する。
     *
     * @param session 現在の実行状態
     * @param pc      削除するPC番号
     * @return 削除できた場合はtrue
     */
    public boolean removeBreakpoint(WebMipsSession session, int pc) {
        return session.getBreakpointManager().remove(pc);
    }

    /**
     * 登録されているブレークポイント一覧を返す。
     *
     * @param session 現在の実行状態
     * @return ブレークポイント一覧
     */
    public Set<Integer> getBreakpoints(WebMipsSession session) {
        return session.getBreakpointManager().getAll();
    }

    /**
     * 指定したPCがプログラム範囲内か検証する。
     *
     * @param session 現在の実行状態
     * @param pc      検証するPC番号
     */
    private void validateProgramPc(WebMipsSession session, int pc) {
        if (pc < 0 || pc >= session.getProgram().size()) {
            throw new IllegalArgumentException("PCがプログラム範囲外です: " + pc);
        }
    }

    /**
     * 現在の実行状態を、ブレークポイントまたはプログラム終了まで連続実行する。
     *
     * 無限ループ対策として、一度のrun操作で実行できる最大ステップ数を制限する。
     * 最大ステップ数に到達した場合は、そこで停止する。
     *
     * @param session 現在の実行状態
     * @return 連続実行の結果
     */
    public RunResult runUntilBreakpoint(WebMipsSession session) {
        int executedStepCount = 0;
        StepResult lastResult = null;

        while (canStep(session) && executedStepCount < MAX_RUN_STEPS) {
            int currentPc = session.getStepRunner().getPc();

            if (session.getBreakpointManager().contains(currentPc)) {
                String message = executedStepCount == 0
                        ? "現在のPCがブレークポイントです: PC " + currentPc
                        : "ブレークポイントに到達しました: PC " + currentPc;

                RunStopReason stopReason = executedStepCount == 0
                        ? RunStopReason.CURRENT_PC_BREAKPOINT
                        : RunStopReason.BREAKPOINT_REACHED;

                return new RunResult(lastResult, executedStepCount, stopReason, message);
            }

            lastResult = step(session);
            executedStepCount++;
        }

        if (canStep(session)) {
            return new RunResult(
                    lastResult,
                    executedStepCount,
                    RunStopReason.MAX_STEPS_REACHED,
                    "最大実行ステップ数に到達したため停止しました。必要に応じて、もう一度runを実行すると続きから実行できます。");
        }

        return new RunResult(
                lastResult,
                executedStepCount,
                RunStopReason.PROGRAM_FINISHED,
                "プログラムが終了しました。");
    }

    /**
     * 入力されたプログラム文字列が制限内か検証する。
     *
     * Webアプリでは、極端に大きい入力を受け付けると、
     * パース処理や実行処理でサーバーに負荷がかかる可能性がある。
     * そのため、パース前に入力サイズを確認する。
     *
     * @param programText ユーザーが入力したMIPSプログラム文字列
     */
    private void validateProgramText(String programText) {
        if (programText == null || programText.isBlank()) {
            throw new IllegalArgumentException("プログラムを入力してください。");
        }

        if (programText.length() > MAX_PROGRAM_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "プログラムが長すぎます。最大 "
                            + MAX_PROGRAM_TEXT_LENGTH
                            + " 文字までです。");
        }

        List<String> lines = splitLines(programText);

        if (lines.size() > MAX_PROGRAM_LINE_COUNT) {
            throw new IllegalArgumentException(
                    "プログラムの行数が多すぎます。最大 "
                            + MAX_PROGRAM_LINE_COUNT
                            + " 行までです。");
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.length() > MAX_PROGRAM_LINE_LENGTH) {
                throw new IllegalArgumentException(
                        "プログラムの "
                                + (i + 1)
                                + " 行目が長すぎます。1行は最大 "
                                + MAX_PROGRAM_LINE_LENGTH
                                + " 文字までです。");
            }
        }
    }
}