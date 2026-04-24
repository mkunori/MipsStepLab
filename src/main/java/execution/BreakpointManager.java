package execution;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ブレークポイントを管理するクラス。
 *
 * ブレークポイントとは、指定したPCに到達したときに
 * 自動実行を一時停止するための印である。
 *
 * このクラスは、ブレークポイントの追加・削除・確認だけを担当する。
 * コンソール表示やユーザー入力は担当しない。
 */
public class BreakpointManager {

    /** ブレークポイントとして登録されたPC番号の集合。 */
    private final Set<Integer> breakpoints = new HashSet<>();

    /**
     * ブレークポイントを追加する。
     * 
     * @param pc 追加するPC番号
     */
    public void add(int pc) {
        breakpoints.add(pc);
    }

    /**
     * ブレークポイントを削除する。
     * 
     * @param pc 削除するPC番号
     * @return 削除できた場合はtrue、登録されていなかった場合はfalse
     */
    public boolean remove(int pc) {
        return breakpoints.remove(pc);
    }

    /**
     * 指定したPCがブレークポイントか判定する。
     * 
     * @param pc 判定するPC番号
     * @return ブレークポイントとして登録されている場合はtrue
     */
    public boolean contains(int pc) {
        return breakpoints.contains(pc);
    }

    /**
     * ブレークポイントが1つも登録されていないか判定する。
     * 
     * @return ブレークポイントが空の場合はtrue
     */
    public boolean isEmpty() {
        return breakpoints.isEmpty();
    }

    /**
     * すべてのブレークポイントを削除する。
     */
    public void clear() {
        breakpoints.clear();
    }

    /**
     * 登録されているブレークポイント一覧を返す。
     * 
     * 外部から直接変更されないように、変更不可のSetとして返す。
     * 
     * @return ブレークポイント一覧
     */
    public Set<Integer> getAll() {
        return Collections.unmodifiableSet(breakpoints);
    }
}