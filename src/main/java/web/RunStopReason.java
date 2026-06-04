package web;

/**
 * run操作が停止した理由を表す列挙型。
 *
 * 停止理由を文字列だけで扱うと、Controller側で
 * メッセージ種別を判断しにくくなる。
 * enumとして保持することで、最大ステップ数到達などを
 * 警告表示に切り替えやすくする。
 */
public enum RunStopReason {

    /** 現在PCがすでにブレークポイントだったため停止した。 */
    CURRENT_PC_BREAKPOINT,

    /** ブレークポイントに到達したため停止した。 */
    BREAKPOINT_REACHED,

    /** 最大実行ステップ数に到達したため停止した。 */
    MAX_STEPS_REACHED,

    /** プログラムが終了したため停止した。 */
    PROGRAM_FINISHED
}
