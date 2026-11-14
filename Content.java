
import java.util.ArrayList;
import java.util.List;

public class Content {

    public record Conditions(
        String residence,
        String work,
        String car,
        boolean winter
    ) {}

    public record Preview(
        List<String> life,
        List<String> checks
    ) {}

    public static Preview build(Conditions c) {

        List<String> life = new ArrayList<>();
        List<String> checks = new ArrayList<>();

        // ① 金山町で暮らした経験
        if (c.residence().equals("return")) {

            life.add(
                "以前暮らした経験を活かしながら、" +
                "現在の金山町の仕事や住宅事情を調べる生活です。"
            );

            checks.add(
                "以前と比べて仕事や住宅、交通事情が" +
                "どのように変わったか確認する。"
            );

        } else {

            life.add(
                "金山町で初めて暮らす生活を想定します。" +
                "買い物や通院など、日常生活の環境を知ることが重要です。"
            );

            checks.add(
                "買い物、病院、交通手段などの" +
                "基本的な生活環境を確認する。"
            );
        }

        // ② 働き方
        if (c.work().equals("remote")) {

            life.add(
                "平日は自宅でリモートワークをする生活を想定します。"
            );

            checks.add(
                "住宅のインターネット環境や" +
                "通信速度を確認する。"
            );

        } else {

            life.add(
                "町内や周辺地域に通勤する生活を想定します。"
            );

            checks.add(
                "希望する仕事の種類、収入、勤務時間、" +
                "通勤距離を確認する。"
            );
        }

        // ③ 車の有無
        if (c.car().equals("yes")) {

            life.add(
                "車を使って買い物や通勤をする生活を想定します。"
            );

            checks.add(
                "駐車場所や買い物先までの距離、" +
                "冬の道路状況を確認する。"
            );

        } else {

            life.add(
                "車を使わずに公共交通などを利用する生活を想定します。"
            );

            checks.add(
                "バスや鉄道の運行時間、" +
                "買い物や通勤の移動手段を確認する。"
            );
        }

        // ④ 冬の暮らし
        if (c.winter()) {

            life.add(
                "冬は積雪への備えが必要です。" +
                "暖房や除雪を考えた生活を想定します。"
            );

            checks.add(
                "住宅の暖房設備、除雪方法、" +
                "冬の移動手段を確認する。"
            );

        } else {

            life.add(
                "冬への不安がなくても、" +
                "積雪時の生活環境について確認が必要です。"
            );

            checks.add(
                "冬の暖房や除雪について" +
                "最低限の情報を確認する。"
            );
        }

        return new Preview(life, checks);
    }
}
