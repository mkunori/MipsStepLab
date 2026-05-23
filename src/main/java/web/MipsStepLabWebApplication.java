package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MipsStepLabのWebアプリ版を起動するクラス。
 *
 * このクラスはSpring Bootアプリケーションの入口になる。
 * mainメソッドを実行すると、内蔵Webサーバーが起動し、
 * ブラウザからアクセスできるようになる。
 */
@SpringBootApplication
public class MipsStepLabWebApplication {

    /**
     * Webアプリケーションを起動する。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(MipsStepLabWebApplication.class, args);
    }
}