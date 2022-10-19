import me.theentropyshard.netschoolapi.NetSchoolAPI;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        NetSchoolAPI netschool = new NetSchoolAPI(
                "https://giseo.rkomi.ru",
                env.get("USERNAME"),
                env.get("PASSWORD"),
                "МАОУ \"Лицей № 1\" г. Сыктывкара"
        );
        netschool.login();

        try {
            System.out.println(netschool.getJsonAnnouncements());
            System.out.println();
            //System.out.println(netschool.getJsonDiary("2022-10-3", "2022-10-9"));
            System.out.println(netschool.getAttachments(netschool.getDiary("2022-10-3", "2022-10-9")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            netschool.logout();
            try {
                netschool.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
