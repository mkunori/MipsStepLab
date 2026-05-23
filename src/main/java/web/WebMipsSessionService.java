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

    /**
     * 入力されたプログラム文字列から、新しいWebMipsSessionを作成する。
     *
     * @param programText ユーザーが入力したMIPSプログラム文字列
     * @return 新しい実行状態
     */
    public WebMipsSession createSession(String programText) {
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
}