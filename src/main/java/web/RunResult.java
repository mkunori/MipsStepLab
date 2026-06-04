package web;

import execution.StepResult;

/**
 * 連続実行の結果を表すクラス。
 *
 * run操作では、複数命令を連続で実行する可能性がある。
 * このクラスは、最後に実行した1ステップ分の結果と、
 * なぜ停止したのかをまとめて保持する。
 */
public class RunResult {

    /** 最後に実行した1ステップ分の結果。 */
    private final StepResult lastStepResult;

    /** run操作で実行したステップ数。 */
    private final int executedStepCount;

    /** 停止理由の種別。 */
    private final RunStopReason stopReason;

    /** 停止理由を表すメッセージ。 */
    private final String message;

    /**
     * RunResultを生成する。
     *
     * @param lastStepResult    最後に実行した1ステップ分の結果
     * @param executedStepCount run操作で実行したステップ数
     * @param stopReason        停止理由の種別
     * @param message           停止理由を表すメッセージ
     */
    public RunResult(
            StepResult lastStepResult,
            int executedStepCount,
            RunStopReason stopReason,
            String message) {

        this.lastStepResult = lastStepResult;
        this.executedStepCount = executedStepCount;
        this.stopReason = stopReason;
        this.message = message;
    }

    /**
     * 最後に実行した1ステップ分の結果を返す。
     *
     * @return 最後に実行した1ステップ分の結果
     */
    public StepResult getLastStepResult() {
        return lastStepResult;
    }

    /**
     * run操作で実行したステップ数を返す。
     *
     * @return run操作で実行したステップ数
     */
    public int getExecutedStepCount() {
        return executedStepCount;
    }

    /**
     * 停止理由の種別を返す。
     *
     * @return 停止理由の種別
     */
    public RunStopReason getStopReason() {
        return stopReason;
    }

    /**
     * 最大実行ステップ数に到達して停止したか判定する。
     *
     * @return 最大実行ステップ数に到達した場合はtrue
     */
    public boolean isMaxStepsReached() {
        return stopReason == RunStopReason.MAX_STEPS_REACHED;
    }

    /**
     * 停止理由を表すメッセージを返す。
     *
     * @return 停止理由を表すメッセージ
     */
    public String getMessage() {
        return message;
    }
}