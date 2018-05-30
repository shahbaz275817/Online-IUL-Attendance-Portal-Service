package com.sk;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by shahbaz275817 on 10/2/17.
 */
 class Main {
    public static void main() throws IOException, ClassNotFoundException, SQLException {
        UserDataDao userDataDao=new UserDataDao();
        FetchHTMLData htmlData=new FetchHTMLData();
        int count = userDataDao.getTotalUsers();
        System.out.print(count);
        for(int i=1;i<=count;i++){
            UserData user=userDataDao.getUser(i);
            String html = htmlData.getHTML(user.getEnrollmentNo(),user.getPassword());
            userDataDao.saveHTMLData(i,html);
        }

    }
}
