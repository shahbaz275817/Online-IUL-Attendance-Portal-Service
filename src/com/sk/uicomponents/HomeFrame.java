package com.sk.uicomponents;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Arrays;

import javax.swing.*;

import com.sk.*;
import javafx.scene.Parent;

public class HomeFrame extends JFrame{

	private static final int DEFAULT_WIDTH=400;
	private static final int DEFAULT_HEIGHT=600;
	private static JButton updateAttendanceButton;
	private static JTextArea statusArea;
	private static JScrollBar scrollBar;
	private static JLabel statusLabel;
	static ClickAction clickAction;
	private static JPanel netInterfacePanel;
	private static JComboBox ApnetInterfaceList;
    private static JComboBox DbnetInterfaceList;
    private static final JLabel attendencePortalInterfaceLabel=new JLabel("Select the network interface to interact with the IUL attendance portal");
    private static final JLabel databaseInteractionInterfaceLabel=new JLabel("Select the network interface to interact with the online database");

	public HomeFrame() {
		
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		clickAction=new ClickAction();

		
	}
	
	public static void addComponentsToPane(Container paneMain) {
		
		
		
		paneMain.setLayout(new BorderLayout());
		
		updateAttendanceButton = new JButton("Update user's attendance");
		updateAttendanceButton.addActionListener(clickAction);
		paneMain.add(updateAttendanceButton,BorderLayout.PAGE_END);
		
		statusLabel = new JLabel();
		statusLabel.setText("Press the update user's attendace button to start updating the attendance database");
		paneMain.add(statusLabel,BorderLayout.BEFORE_FIRST_LINE);
		
		
		statusArea=new JTextArea(20,35);
		statusArea.setText("Idle : : :");
		statusArea.setEditable(false);

		scrollBar=new JScrollBar();
        scrollBar.setOrientation(Adjustable.VERTICAL);
		statusArea.add(scrollBar);
		paneMain.add(statusArea,BorderLayout.CENTER);



		Enumeration<NetworkInterface> interfaces=
				null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
/*		for(NetworkInterface itf: Collections.list(interfaces)) {
			//networkInterfaces.
			//System.out.println(itf.getInetAddresses().nextElement());
		}*/

        //networkInterfaces=new String[Collections.list(interfaces).size()];

        Enumeration<NetworkInterface> infcount= null;
        try {
            infcount = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        int j=Collections.list(infcount).size();
        String networkInterfaces[]=new String[j];
        for(int i=0;interfaces.hasMoreElements();i++){
			networkInterfaces[i]=interfaces.nextElement().getDisplayName().toString();
		}

		netInterfacePanel = new JPanel();
        netInterfacePanel.setLayout(new BoxLayout(netInterfacePanel,BoxLayout.Y_AXIS));
        netInterfacePanel.setMaximumSize(new Dimension(100,500));
		ApnetInterfaceList=new JComboBox(networkInterfaces);
        DbnetInterfaceList=new JComboBox(networkInterfaces);
		ApnetInterfaceList.setMaximumSize(new Dimension(200,50));
		DbnetInterfaceList.setMaximumSize(new Dimension(200,50));
        netInterfacePanel.add(attendencePortalInterfaceLabel);
        netInterfacePanel.add(ApnetInterfaceList);
        netInterfacePanel.add(databaseInteractionInterfaceLabel);
        netInterfacePanel.add(DbnetInterfaceList);
        paneMain.add(netInterfacePanel,BorderLayout.LINE_START);
		
	}
	
	private class ClickAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
		    //updateAttendanceButton.setText("");
			updateAttendanceButton.setText("Updating... Please wait");
			updateAttendanceButton.update(updateAttendanceButton.getGraphics());
			updateAttendanceButton.setEnabled(false);
			statusLabel.setText("");
			statusLabel.updateUI();
			//statusLabel.setText("Processing user data and updating user attendance.....");
			statusLabel.update(updateAttendanceButton.getGraphics());
//			try {
//				Main.main();
//			} catch (ClassNotFoundException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
            String inf = ApnetInterfaceList.getItemAt(ApnetInterfaceList.getSelectedIndex()).toString();
            String inf1 = DbnetInterfaceList.getItemAt(DbnetInterfaceList.getSelectedIndex()).toString();


            statusArea.setText("Hold on.... ");

			statusArea.update(statusArea.getGraphics());
			statusLabel.update(statusLabel.getGraphics());

			try {
				UserDataDao userDataDao=new UserDataDao();
		        FetchHTMLData htmlData=new FetchHTMLData();
		        int count = userDataDao.getTotalUsers();
		        statusArea.append("\nTotal students registered for the service: "+count);
                statusArea.update(statusArea.getGraphics());
                int sleepTimer=0;
		        for(int i=546;i<=count;i++){
		            UserData user=userDataDao.getUser(i);
		            String html = htmlData.getHTML(user.getEnrollmentNo(),user.getPassword());
		            userDataDao.saveHTMLData(i,html);

		            statusArea.append("\n User: "+i+" attendance updated");
					System.out.println("User: "+i+" attendance updated");
					statusArea.setCaretPosition(statusArea.getText().length() - 1);
                    statusArea.update(statusArea.getGraphics());
                    /*if(sleepTimer==10){
                        Thread.sleep(10000);
                        sleepTimer=0;
                    }*/
                   Thread.sleep(3000);
                    //sleepTimer++;
		        }
			}
			catch(Exception err) {
				err.printStackTrace();
			}
			
			
			updateAttendanceButton.setEnabled(true);
			updateAttendanceButton.setText("Update user's attendance again");
			statusLabel.setText("Attendence Updated in the database. Users can now check their attendence.");

			
			
		}
		
	}
	

}
