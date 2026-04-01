package cpu;

/**
 * レジスタ番号とレジスタ名の対応を扱うユーティリティクラス
 */
public final class RegisterNames {

    /**
     * インスタンス化しない
     */
    private RegisterNames() {
    }

    /**
     * レジスタ番号が有効かどうか検証する。
     * 
     * @param index レジスタ番号
     * @throws IllegalArguementExeption レジスタ番号が0～31の範囲外の場合
     */
    public static void validateRegisterIndex(int index) {
        if (index < 0 || index > 31) {
            throw new IllegalArgumentException(
                    "レジスタ番号は0～31で指定してください: " + index);
        }
    }

    /**
     * レジスタの名前を返す。
     * 
     * @param index レジスタ番号
     * @return レジスタ名
     * @throws IllegalArgumentException レジスタ番号が0～31の範囲外の場合
     */
    public static String getName(int index) {
        validateRegisterIndex(index);

        return switch (index) {
            case 0 -> "$zero"; // 定数0
            case 1 -> "$at"; // 疑似命令で一時的に使用
            case 2 -> "$v0"; // 関数の戻り値や式の評価値
            case 3 -> "$v1";
            case 4 -> "$a0"; // 関数の引数
            case 5 -> "$a1";
            case 6 -> "$a2";
            case 7 -> "$a3";
            case 8 -> "$t0"; // 一時変数
            case 9 -> "$t1";
            case 10 -> "$t2";
            case 11 -> "$t3";
            case 12 -> "$t4";
            case 13 -> "$t5";
            case 14 -> "$t6";
            case 15 -> "$t7";
            case 16 -> "$s0"; // 一時変数(Saved)
            case 17 -> "$s1";
            case 18 -> "$s2";
            case 19 -> "$s3";
            case 20 -> "$s4";
            case 21 -> "$s5";
            case 22 -> "$s6";
            case 23 -> "$s7";
            case 24 -> "$t8"; // 一時変数
            case 25 -> "$t9";
            case 26 -> "$k0"; // OS用に予約
            case 27 -> "$k1";
            case 28 -> "$gp"; // グローバルポインタ
            case 29 -> "$sp"; // スタックポインタ
            case 30 -> "$fp"; // フレームポインタ
            case 31 -> "$ra"; // リターンアドレス
            default -> "$r" + index; // (アドレス検証しているので入らないはず)
        };
    }
}
