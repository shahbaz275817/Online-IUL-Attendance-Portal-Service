package com.sk;

/**
 * Created by shahbaz275817 on 10/2/17.
 */



import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class FetchHTMLData {
    public String getHTML(String enrollmentNo,String password) throws IOException {

        String htmlCode="";

        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

        
        password = password.replaceAll(" ", "%20");
        URL myURL = new URL("http://192.168.9.52/sms/login.aspx?ReturnUrl=%2fsms%2fAttendance.aspx%ctl00%24ScriptManager1=ctl00%24UpdatePanel1%7Cctl00%24ContentPlaceHolder%24btnSubmit&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=LSF88a26H1JcU57exvDNGsHjXQwpxxJbQ7bTkoNetX%2BGQgY9ye2j%2FpkPxpqD%2BhNtJC7rU9v%2BSPNpmLvNXoHqf1XR2G%2FOek%2Bo29kHh%2BTJ6iQvWnaE37%2B3f6IEXxQ8n57IccAfUwVchjyPVNvh6oMEz4UquQYvwxzdQTpUo2FIO%2BT3kMBz5g8mmKRNeadAA%2BUVCjlne6rqdV6xU3QyzR9Nkw%3D%3D&__EVENTVALIDATION=FxdYZhKVKZMJ8rlINHMr%2FSmtKjL2gP5pisOJ8cJ5DOKYRCwGKmb010QDfEg5rLy5Sro4M%2BkCodvkva1IUVkv9BoDtmcOuEucuGwLugpdXyhIhNaEm7zwtNJSOwWtgoWq2F2n4gEywf2FPBHqpQsoOvgUraY4qByr0nJfuVLOyYmfooUFeSyTDdi2svJl5a9pG2yj3cWjF9m42kxY%2FDlLhdWU1iQlUNYb1gXm9q9DkLk%3D&ctl00%24ContentPlaceHolder%24txtEnrollmentNo="+enrollmentNo+"&ctl00%24ContentPlaceHolder%24txtPassword="+password+"&__ASYNCPOST=true&ctl00%24ContentPlaceHolder%24btnSubmit=Submit");
        //URL myURL = new URL("http://124.30.5.130/sms/login.aspx?ReturnUrl=%2fsms%2fAttendance.aspx%ctl00%24ScriptManager1=ctl00%24UpdatePanel1%7Cctl00%24ContentPlaceHolder%24btnSubmit&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=LSF88a26H1JcU57exvDNGsHjXQwpxxJbQ7bTkoNetX%2BGQgY9ye2j%2FpkPxpqD%2BhNtJC7rU9v%2BSPNpmLvNXoHqf1XR2G%2FOek%2Bo29kHh%2BTJ6iQvWnaE37%2B3f6IEXxQ8n57IccAfUwVchjyPVNvh6oMEz4UquQYvwxzdQTpUo2FIO%2BT3kMBz5g8mmKRNeadAA%2BUVCjlne6rqdV6xU3QyzR9Nkw%3D%3D&__EVENTVALIDATION=FxdYZhKVKZMJ8rlINHMr%2FSmtKjL2gP5pisOJ8cJ5DOKYRCwGKmb010QDfEg5rLy5Sro4M%2BkCodvkva1IUVkv9BoDtmcOuEucuGwLugpdXyhIhNaEm7zwtNJSOwWtgoWq2F2n4gEywf2FPBHqpQsoOvgUraY4qByr0nJfuVLOyYmfooUFeSyTDdi2svJl5a9pG2yj3cWjF9m42kxY%2FDlLhdWU1iQlUNYb1gXm9q9DkLk%3D&ctl00%24ContentPlaceHolder%24txtEnrollmentNo="+enrollmentNo+"&ctl00%24ContentPlaceHolder%24txtPassword="+password+"&__ASYNCPOST=true&ctl00%24ContentPlaceHolder%24btnSubmit=Submit");
        //124.30.5.130   192.168.9.52
        URLConnection myURLConnection = myURL.openConnection();

        Object obj = myURLConnection.getContent();
        CookieStore store = manager.getCookieStore();
        List<HttpCookie> cookies = store.getCookies();
        
        
        boolean authCookieFound=false;
		for(HttpCookie cookie : cookies) {
			
			if(cookie.getName().equalsIgnoreCase(".ASPXAUTH")) {
                authCookieFound = true;
            }
		}
		if(!authCookieFound)
			return "You have entered wrong Enrollment Number and password. Kindly update your credentials to view your attendence data ";


        URL attURL = new URL("http://192.168.9.52/sms/CummulativeAttendance.aspx");
        //URL attURL = new URL("http://124.30.5.130/sms/CummulativeAttendance.aspx");
        myURLConnection = attURL.openConnection();

        //myURLConnection.connect();


        obj = myURLConnection.getContent();
        store = manager.getCookieStore();
        cookies = store.getCookies();

        BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
        String inputLine;
/*
        for(int i=1;i<289;i++) {

            inputLine=in.readLine();
            System.out.println(inputLine);
            if (inputLine.contains("<div class=\"Form\">"))
                System.out.println("found");
        }
        for(int i=1;i<=40;i++){
            //System.out.println(in.readLine());
            htmlCode+=in.readLine();
            htmlCode+="\n";
        }
*/

        while(true) {
            inputLine = in.readLine();
            if(inputLine==null)
                continue;
            if (inputLine.contains("<div class=\"Form\">")) {

                while (!inputLine.contains(" <td   colspan=\"4\" style=\"float:right\"> <b> Attendance Percentage : </b></td>")) {
                    htmlCode += inputLine;
                    htmlCode += "\n";
                    inputLine=in.readLine();
                }
                htmlCode += inputLine;
                htmlCode += "\n";
                for (int i = 0; i < 3; i++) {
                    inputLine = in.readLine();
                    htmlCode += inputLine;
                    htmlCode += "\n";
                }
                break;
            }
        }
        in.close();

        System.out.println(htmlCode);
        return htmlCode;
    }



}
