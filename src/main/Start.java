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

	//@author Sebastian Green
	
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
	
	public static class TradingBot {

		public ArrayList<main.Start.stock[]> boughtAktie;
		public ArrayList<Recipt> recipts;
		public BigDecimal money;
		
		public TradingBot(ArrayList<stock[]> boughtAktie, ArrayList<Recipt> recipts, BigDecimal money) {
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
	private static PrintWriter txtWriter;
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
	
	//Methoden skapar startmenyn på skärmen
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
					getInfo("LOGIN", uName, psw);
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
		frameFixer();
	}
	
	//Methoden skapar menyn man får när man loggat in
	public static void usermenu() {
		JLabel namn = (JLabel) factory.FrameFactory.constrainComponent(new JLabel("Inloggad som: " + Start.username), layout, 1, 0, 1, 1, 0.2, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
		JButton getMoney = (JButton) factory.FrameFactory.constrainComponent(new JButton("Kolla saldo"), layout, 1, 1, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		JButton update = (JButton) factory.FrameFactory.constrainComponent(new JButton("Hämta info igen"), layout, 1, 2, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		JButton money = (JButton) factory.FrameFactory.constrainComponent(new JButton("Lägg in pengar"), layout, 1, 3, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		JButton logout = (JButton) factory.FrameFactory.constrainComponent(new JButton("Logga ut"), layout, 2, 0, 1, 1, 0.5, 0.5, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		getMoney.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int köptAktieSize = 0;
				for(int i = 0; i < tb.boughtAktie.size(); i++) {
					for(int j = 0; j < tb.boughtAktie.get(i).length; j++) {
						if(tb.boughtAktie.get(i)[j] != null) {
							köptAktieSize++;
						}
					}
				}
				JOptionPane.showMessageDialog(frame, "Antal köpta aktier: " + köptAktieSize + "\nAntal kvitton: " + tb.recipts.size() + "\nPengar kvar: " + tb.money);
			}
		});
		update.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeComponents(new Component[] {namn, getMoney, update, money, logout});
				getInfo("LOGIN", Start.username, Start.password);
				JOptionPane.showMessageDialog(frame, "Information hämtad");
			}
		});
		money.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeComponents(new Component[] {namn, getMoney, update, money, logout});
				getInfo("MONEY", Start.username, Start.password);
				JOptionPane.showMessageDialog(frame, "Pengarna är tillagda");
			}
		});
		logout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeComponents(new Component[] {namn, getMoney, update, money, logout});
				username = null;
				password = null;
				tb = null;
				startmenu();
			}
		});
		frame.add(namn);
		frame.add(getMoney);
		frame.add(update);
		frame.add(money);
		frame.add(logout);
		frameFixer();
	}
	
	//Methoden hanterar uppkopplingen till servern
	private static void getInfo(String choose, String uName, String psw) {
		boolean loggedIn = false;
		try {
			s = new Socket(ipadress, port);
			txtWriter = new PrintWriter(s.getOutputStream());
			txtReader = new Scanner(s.getInputStream());
			objectReader = new ObjectInputStream(s.getInputStream());
			txtWriter.println(choose);
			txtWriter.println(uName);
			txtWriter.println(psw);
			txtWriter.flush();
			if(choose.equals("LOGIN")) {
				String boll = txtReader.nextLine();
				if(boll.equals("TRUE")) {
					try {
						loggedIn = true;
						ArrayList<stock[]> tmpBought = (ArrayList<stock[]>) objectReader.readObject();
						ArrayList<Recipt> tmpRecipt = (ArrayList<Recipt>) objectReader.readObject();
						BigDecimal tmpMoney = (BigDecimal) new BigDecimal(txtReader.nextLine());
						tb = new TradingBot(tmpBought, tmpRecipt, tmpMoney);
						if(tmpBought == null || tmpRecipt == null || tmpMoney == null) {
							tb = null;
						}
					} catch(Throwable t) {
						JOptionPane.showMessageDialog(frame, "Fel när informationen skulle laddas ner: " + t.toString());
					}
				}
			} else if(choose.equals("CREATEACCOUNT")) {
				String boll = txtReader.nextLine();
				if(boll.equals("TRUE")) {
					JOptionPane.showMessageDialog(frame, "Kontot är nu skapat");
				} else {
					JOptionPane.showMessageDialog(frame, "Kontot kunde inte skapas");
				}
			} else if(choose.equals("MONEY")) {
				String input = JOptionPane.showInputDialog(frame, "Skriv hur mycket du vill lägga in");
				int tal = 0;
				try {
					tal = Integer.parseInt(input);
				} catch(Throwable t) {
					JOptionPane.showMessageDialog(frame, "Felmeddelande: " + t.toString());
				}
				txtWriter.println(tal);
				txtWriter.flush();
			}
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(frame, "Kunde inte logga in, felmeddelande: " + t.toString());
			startmenu();
		}
		try {s.close();} catch (Throwable t) {} s = null; txtWriter = null; txtReader = null; objectReader = null;
		if(loggedIn && tb != null) {
			Start.username = uName;
			Start.password = psw;
			usermenu();
		} else if(choose.equals("MONEY")) {
			usermenu();
		} else {
			startmenu();
		}
	}
	
	//Methoden loggar ut användaren
	private static void logout(Component[] comp) {
		removeComponents(comp);
		s = null; txtWriter = null; txtReader = null; Start.username = null; Start.password = null;
		startmenu();
	}
	
	//Methoden ser till att fönstret är rent så att nytt innehåll kan målas på den
	private static void removeComponents(Component[] comp) {
		for(int i = 0; i < comp.length; i++) {
			frame.remove(comp[i]);
		}
	}
	
	//Methoden uppdaterar fönstret
	private static void frameFixer() {
		frame.repaint();
		frame.setVisible(true);
	}
}