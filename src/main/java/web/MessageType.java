package web;

/**
 * 画面に表示するメッセージの種類を表す列挙型。
 *
 * メッセージ種別を文字列で直接扱うと、
 * "success" や "error" のタイプミスに気づきにくくなる。
 *
 * enumとして定義することで、コンパイル時に誤りを見つけやすくする。
 */
public enum MessageType {

    /** 成功メッセージ。 */
    SUCCESS("success"),

    /** エラーメッセージ。 */
    ERROR("error"),

    /** 警告メッセージ。 */
    WARNING("warning"),

    /** 通常の情報メッセージ。 */
    INFO("info");

    /** CSSクラス名として使用する文字列。 */
    private final String cssClassName;

    /**
     * MessageTypeを生成する。
     *
     * @param cssClassName CSSクラス名として使用する文字列
     */
    MessageType(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    /**
     * CSSクラス名として使用する文字列を返す。
     *
     * @return CSSクラス名
     */
    public String getCssClassName() {
        return cssClassName;
    }
}