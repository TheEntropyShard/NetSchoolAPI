package me.theentropyshard.netschoolapi;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        NetschoolAPI netschool = new NetschoolAPI(
                "https://giseo.rkomi.ru",
                env.get("USERNAME"),
                env.get("PASSWORD"),
                "МАОУ \"Лицей № 1\" г. Сыктывкара"
        );
        netschool.login();
        netschool.printAnnouncements();
        netschool.logout();
    }
}
