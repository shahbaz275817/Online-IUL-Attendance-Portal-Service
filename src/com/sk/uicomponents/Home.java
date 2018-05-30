package com.sk.uicomponents;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Home {

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				HomeFrame homeFrame = new HomeFrame();	
				homeFrame.setTitle("Online IUL Attendance Portal service");
				homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				HomeFrame.addComponentsToPane(homeFrame.getContentPane());
				homeFrame.pack();
				homeFrame.setVisible(true);
				
			}
		});

	}

}
