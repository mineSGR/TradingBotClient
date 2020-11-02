package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Start {

	public static class stock {
		
		public String name;
		public BigDecimal value;
		public boolean newAktie;
		
		public stock(String name, BigDecimal value) {
			this.name = name;
			this.value = value;
			newAktie = true;
		}
	}
	
	public static class Recipt {
		public BigDecimal boughtPrice;
		public BigDecimal sellPrice;
	}
	
	public class TradingBot {

		public ArrayList<ArrayList<main.Start.stock>> aktier;
		public ArrayList<main.Start.stock[]> boughtAktie;
		public ArrayList<Recipt> recipts;
		public BigDecimal money;
		
		public TradingBot(ArrayList<ArrayList<stock>> aktier, ArrayList<stock[]> boughtAktie, ArrayList<Recipt> recipts, BigDecimal money) {
			this.aktier = aktier;
			this.boughtAktie = boughtAktie;
			this.recipts = recipts;
			this.money = money;
		}
	}

	
	public static JFrame frame;
	public static GridBagLayout layout;
	public static final String ipadress = "localhost";
	public static final int port = 8989;
	
	private static String username;
	private static String password;
	
	private static Socket s;
	private static PrintWriter writer;
	private static Scanner txtReader;
	private static ObjectInputStream objectReader;
	private static TradingBot tb;
	
	public static void main(String[] args) {
		factory.FrameFactory.frameReturner fr = factory.FrameFactory.createJFrame("TradingBot");
		frame = fr.frame;
		layout = fr.layout;
		startmenu();
		frame.setMinimumSize(new Dimension(300, 200));
		factory.FrameFactory.finalize(frame);
	}
	
	public static void startmenu() {
		tb = null;
		JTextField username = (JTextField) factory.FrameFactory.constrainComponent(new JTextField("Användarnamn"), layout, 1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		JTextField password = (JTextField) factory.FrameFactory.constrainComponent(new JPasswordField(), layout, 1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		JButton login = (JButton) factory.FrameFactory.constrainComponent(new JButton("Logga in"), layout, 1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		JButton createAccount = (JButton) factory.FrameFactory.constrainComponent(new JButton("Create account"), layout, 0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		username.setToolTipText("Skriv ditt användarnamn här");
		password.setToolTipText("Skriv ditt lösenord här");
		login.setToolTipText("Klicka här för att logga in");
		createAccount.setToolTipText("Klicka här för att skapa ett konto");
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String uName = username.getText();
				String psw = password.getText();
				if(uName == null || uName.equals("") || uName.equals(" ") || psw == null || psw.equals("") || psw.equals(" ")) {
					JOptionPane.showMessageDialog(frame, "Innehållet i fälten får inte vara tomma");
				} else {
					removeComponents(new Component[] {username, password, login, createAccount});
					getInfo("CHECKLOGIN", uName, psw);
				}
			}
		});
		createAccount.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String uName = username.getText();
				String psw = password.getText();
				if(uName == null || uName.equals("") || uName.equals(" ") || psw == null || psw.equals("") || psw.equals(" ")) {
					JOptionPane.showMessageDialog(frame, "Innehållet i fälten får inte vara tomma");
				} else {
					removeComponents(new Component[] {username, password, login, createAccount});
					getInfo("CREATEACCOUNT", uName, psw);
				}
			}
		});
		frame.add(username);
		frame.add(password);
		frame.add(login);
		frame.add(createAccount);
		frame.repaint();
	}
	
	public static void usermenu() {
		JLabel namn = (JLabel) factory.FrameFactory.constrainComponent(new JLabel("Inloggad som: " + Start.username), layout, 0, 0, 1, 1, 0.2, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		JButton getMoney = (JButton) factory.FrameFactory.constrainComponent(new JButton("Kolla saldo"), layout, 1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		JButton update = (JButton) factory.FrameFactory.constrainComponent(new JButton("Hämta info igen"), layout, 2, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		getMoney.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMoney(tb.money);
			}
		});
		update.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getInfo("LOGIN", Start.username, Start.password);
				JOptionPane.showMessageDialog(frame, "Information hämtad");
			}
		});
		
		frame.add(namn);
		frame.add(getMoney);
		frame.repaint();
	}
	
	private static void displayMoney(BigDecimal money) {
		JLabel showMoney = (JLabel) factory.FrameFactory.constrainComponent(new JLabel("Saldo: " + money.toString()), layout, 1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		JButton moveBack = (JButton) factory.FrameFactory.constrainComponent(new JButton("Tillbaka"), layout, 0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		moveBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeComponents(new Component[] {showMoney, moveBack});
				usermenu();
			}
		});
		frame.add(showMoney);
		frame.add(moveBack);
		frame.repaint();
	}
	
	private static void getInfo(String choose, String uName, String psw) {
		boolean loggedIn = false;
		try {
			s = new Socket(ipadress, port);
			writer = new PrintWriter(s.getOutputStream());
			txtReader = new Scanner(s.getInputStream());
			objectReader = new ObjectInputStream(s.getInputStream());
			writer.println(choose);
			writer.println(uName);
			writer.println(psw);
			writer.flush();
			if(choose.equals("LOGIN")) {
				String boll = txtReader.nextLine();
				if(boll.equals("TRUE")) {
					try {
					loggedIn = true;
					ArrayList<ArrayList<stock>> tmpAktier = (ArrayList<ArrayList<stock>>) objectReader.readObject();
					ArrayList<stock[]> tmpBought = (ArrayList<stock[]>) objectReader.readObject();
					ArrayList<Recipt> tmpRecipt = (ArrayList<Recipt>) objectReader.readObject();
					BigDecimal tmpMoney = (BigDecimal) objectReader.readObject();
					JOptionPane.showMessageDialog(frame, "INFO: " + tmpAktier.toString() + ", " + tmpBought.toString() + ", " + tmpRecipt.toString() + ", " + tmpMoney.toString());
					//tb = new TradingBot(tmpAktier, tmpBought, tmpRecipt, tmpMoney);
					} catch(Throwable t) {
						JOptionPane.showMessageDialog(frame, t.toString());
					}
				}
			} else if(choose.equals("CREATEACCOUNT")) {
				String boll = txtReader.nextLine();
				if(boll.equals("TRUE")) {
					JOptionPane.showMessageDialog(frame, "Kontot är nu skapat");
				} else {
					JOptionPane.showMessageDialog(frame, "Kontot kunde inte skapas");
				}
			}
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(frame, "Kunde inte logga in, felmeddelande: " + t.toString());
			startmenu();
		}
		try {s.close();} catch (Throwable t) {} s = null; writer = null; txtReader = null; objectReader = null;
		if(loggedIn) {
			Start.username = uName;
			Start.password = psw;
			usermenu();
		} else {
			startmenu();
		}
	}
	
	private static void logout(Component[] comp) {
		removeComponents(comp);
		s = null; writer = null; txtReader = null; Start.username = null; Start.password = null;
		startmenu();
	}
	
	private static void removeComponents(Component[] comp) {
		for(int i = 0; i < comp.length; i++) {
			frame.remove(comp[i]);
		}
		frame.repaint();
	}
}
