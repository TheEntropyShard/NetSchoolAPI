import me.theentropyshard.netschoolapi.NetSchoolAPI;
import me.theentropyshard.netschoolapi.schemas.Attachment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        NetSchoolAPI netschool = new NetSchoolAPI(
                "https://адрес.вашего.дневника",
                "логин",
                "пароль",
                "Название школы"
        );
        netschool.login();

        try {
            System.out.println(netschool.getJsonAnnouncements());
            System.out.println();
            //System.out.println(netschool.getJsonDiary("2022-10-3", "2022-10-9"));
            List<Attachment> attachments = netschool.getAttachments(netschool.getDiary("2022-10-3", "2022-10-9"));
            System.out.println(attachments);
            netschool.downloadAttachment(new File("attachment.docx"), attachments.get(0));
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
