import me.theentropyshard.netschoolapi.NetSchoolAPI;
import me.theentropyshard.netschoolapi.mail.schemas.Mail;
import me.theentropyshard.netschoolapi.mail.schemas.MailBox;
import me.theentropyshard.netschoolapi.mail.schemas.Message;
import me.theentropyshard.netschoolapi.mail.schemas.SortingType;

import java.io.IOException;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        NetSchoolAPI netschool = new NetSchoolAPI(
                System.getenv("MAIN_DOMAIN"), // Адрес вашего электронного дневника, например https://giseo.rkomi.ru
                System.getenv("USERNAME"), // Логин, например ПупкинВ
                System.getenv("PASSWORD"), // Пароль, например superParol228
                System.getenv("SCHOOL_NAME") // Название школы, как оно отображается в дневнике
        );
        netschool.login();

        try {
            /*System.out.println(netschool.getAnnouncements());
            System.out.println();
            System.out.println(netschool.getJsonDiary("2022-10-3", "2022-10-9"));
            List<Attachment> attachments = netschool.getAttachments(netschool.getDiary("2022-10-3", "2022-10-9"));
            System.out.println(attachments);
            System.out.println(netschool.getMail(1, 0, 100, SortingType.SORT_DESC));
            System.out.println(netschool.getAvailableReports());
            System.out.println();
            System.out.println(netschool.getStudentGradesById(netschool.getAvailableReports().get(0).reports[0].id));
            Mail mail = netschool.getMail(MailBoxIds.BOX_INCOMING, 0, 10, SortingType.SORT_DESC);
            System.out.println(mail);
            System.out.println();
            System.out.println(netschool.readMail(mail.Records[0].MessageId));
            netschool.deleteMail(MailBoxIds.BOX_INCOMING, mail.Records[2].MessageId);
            System.out.println(netschool.getMail(MailBoxIds.BOX_INCOMING, 0, 10, SortingType.SORT_DESC));
            System.out.println(netschool.getAssignmentTypes(false));
            System.out.println();
            System.out.println(netschool.getAssignmentTypes(true));*/
            //System.out.println(netschool.getParentInfoLetter());
            netschool.sendMessage(new Message(
                    "Получатель",
                    "Тема",
                    "Текст"
            ), false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            netschool.logout();
        }
    }
}
