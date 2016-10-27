import java.io.File;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.math.BigInteger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
/*
 *Steganography_Controller Class
 */
public class Steganography_Controller
{
	//Program Variables
	private Steganography_View	view;
	private Steganography	model;

	//Panel Displays
	private JPanel		decode_panel;
	private JPanel		encode_panel;
	//Panel Variables
	private JTextArea 	input;
	private JButton		encodeButton,decodeButton;
	private JLabel		image_input;
	//Menu Variables
	private JMenuItem 	encode;
	private JMenuItem 	decode;
	private JMenuItem 	exit;

	//action event classes
	private Encode			enc;
	private Decode			dec;
	private EncodeButton	encButton;
	private DecodeButton	decButton;

	//decode variable
	private String			stat_path = "";
	private String			stat_name = "";

	//RSA
	BigInteger E;
	BigInteger D;
	BigInteger N;

	/*
	 *Constructor to initialize view, model and environment variables
	 *@param aView  A GUI class, to be saved as view
	 *@param aModel A model class, to be saved as model
	 */
	public Steganography_Controller(Steganography_View aView, Steganography aModel,BigInteger e,BigInteger d,BigInteger N)
	{
	    //RSA
	    this.E=e;
	    this.D=d;
	    this.N=N;
		//program variables
		view  = aView;
		model = aModel;

		//assign View Variables
		//2 views
		encode_panel	= view.getTextPanel();
		decode_panel	= view.getImagePanel();
		//2 data options
		input			= view.getText();
		image_input		= view.getImageInput();
		//2 buttons
		encodeButton	= view.getEButton();
		decodeButton	= view.getDButton();
		//menu
		encode			= view.getEncode();
		decode			= view.getDecode();
		exit			= view.getExit();

		//assign action events
		enc = new Encode();
		encode.addActionListener(enc);
		dec = new Decode();
		decode.addActionListener(dec);
		exit.addActionListener(new Exit());
		encButton = new EncodeButton();
		encodeButton.addActionListener(encButton);
		decButton = new DecodeButton();
		decodeButton.addActionListener(decButton);

		//encode view as default
		encode_view();
	}

	/*
	 *Updates the single panel to display the Encode View.
	 */
	private void encode_view()
	{
		update();
		view.setContentPane(encode_panel);
		view.setVisible(true);
	}

	/*
	 *Updates the single panel to display the Decode View.
	 */
	private void decode_view()
	{
		update();
		view.setContentPane(decode_panel);
		view.setVisible(true);
	}

	/*
	 *Encode Class - handles the Encode menu item
	 */
	private class Encode implements ActionListener
	{
		/*
		 *handles the click event
		 *@param e The ActionEvent Object
		 */
		public void actionPerformed(ActionEvent e)
		{
			encode_view(); //show the encode view
		}
	}

	/*
	 *Decode Class - handles the Decode menu item
	 */
	private class Decode implements ActionListener
	{
		/*
		 *handles the click event
		 *@param e The ActionEvent Object
		 */
		public void actionPerformed(ActionEvent e)
		{
			decode_view(); //show the decode view

			//start path of displayed File Chooser
			JFileChooser chooser = new JFileChooser("./");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setFileFilter(new Image_Filter());
			int returnVal = chooser.showOpenDialog(view);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File directory = chooser.getSelectedFile();
				try{
					String image = directory.getPath();
					stat_name = directory.getName();
					stat_path = directory.getPath();
					stat_path = stat_path.substring(0,stat_path.length()-stat_name.length()-1);
					stat_name = stat_name.substring(0, stat_name.length()-4);
					image_input.setIcon(new ImageIcon(ImageIO.read(new File(image))));
				}
				catch(Exception except) {
				//msg if opening fails
				JOptionPane.showMessageDialog(view, "The File cannot be opened!",
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	/*
	 *Exit Class - handles the Exit menu item
	 */
	private class Exit implements ActionListener
	{
		/*
		 *handles the click event
		 *@param e The ActionEvent Object
		 */
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0); //exit the program
		}
	}

	/*
	 *Encode Button Class - handles the Encode Button item
	 */
	private class EncodeButton implements ActionListener
	{
		/*
		 *handles the click event
		 *@param e The ActionEvent Object
		 */
		public void actionPerformed(ActionEvent e)
		{
			//start path of displayed File Chooser
			JFileChooser chooser = new JFileChooser("./");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setFileFilter(new Image_Filter());
			int returnVal = chooser.showOpenDialog(view);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File directory = chooser.getSelectedFile();
				try{
					String text = input.getText();
					String ext  = Image_Filter.getExtension(directory);
					String name = directory.getName();
					String path = directory.getPath();
					path = path.substring(0,path.length()-name.length()-1);
					name = name.substring(0, name.length()-4);

					String stegan = JOptionPane.showInputDialog(view,
									"Enter output file name:", "File name",
									JOptionPane.PLAIN_MESSAGE);
//Writing private key to text
                    String note="key";
                    String dfile= stegan +"privateD";
                    String Nfile=stegan +"privateN";

                    String filename1=image_path(path,dfile,note);
                    String filename2=image_path(path,Nfile,note);
                    try{
                    saveKeys(filename1,D);
                    saveKeys(filename2,N);
                    }catch(Exception error){
                        error.printStackTrace();
                    }
					if(model.encode(path,name,ext,stegan,text,E,N))
					{
						JOptionPane.showMessageDialog(view, "The Image was encoded Successfully!",
							"Success!", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(view, "The Image could not be encoded!",
							"Error!", JOptionPane.INFORMATION_MESSAGE);
					}
					//display the new image
					decode_view();
					image_input.setIcon(new ImageIcon(ImageIO.read(new File(path + "/" + stegan + ".png"))));
				}
				catch(Exception except) {
				//msg if opening fails
				JOptionPane.showMessageDialog(view, "The File cannot be opened!",
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}

	}

	/*
	 *Decode Button Class - handles the Decode Button item
	 */
	private class DecodeButton implements ActionListener
	{
		/*
		 *handles the click event
		 *@param e The ActionEvent Object
		 */
		public void actionPerformed(ActionEvent e)
		{
		    String note="key";
            String dfile= stat_name +"privateD";
            String Nfile=stat_name +"privateN";
            String filename1=image_path(stat_path,dfile,note);
            String filename2=image_path(stat_path,Nfile,note);

            try{
            D=readPublicKeyFromFile(filename1);
            N=readPublicKeyFromFile(filename2);
                    }catch(Exception error){
                        error.printStackTrace();
                    }

			String message = model.decode(stat_path, stat_name,D,N);
			System.out.println(stat_path + ", " + stat_name);
			if(message != "")
			{
				encode_view();
				JOptionPane.showMessageDialog(view, "The Image was decoded Successfully!",
							"Success!", JOptionPane.INFORMATION_MESSAGE);
				input.setText(message);
			}
			else
			{
				JOptionPane.showMessageDialog(view, "The Image could not be decoded!",
							"Error!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/*
	 *Updates the variables to an initial state
	 */
	public void update()
	{
		input.setText("");			//clear textarea
		image_input.setIcon(null);	//clear image
		stat_path = "";				//clear path
		stat_name = "";				//clear name
	}


	/**
	 * Save Files
	 * @param fileName
	 * @param mod
	 * @param exp
	 * @throws IOException
	 */
	private void saveKeys(String fileName,BigInteger exp) throws IOException{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			System.out.println("Generating "+fileName + "...");
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));


			oos.writeObject(exp);

			System.out.println(fileName + " generated successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(oos != null){
				oos.close();

				if(fos != null){
					fos.close();
				}
			}
		}
	}

    /**
	 * read Public Key From File
	 * @param fileName
	 * @return PublicKey
	 * @throws IOException
	 */
	public BigInteger readPublicKeyFromFile(String fileName) throws IOException{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);


		    BigInteger exponent = (BigInteger) ois.readObject();

		    return exponent;

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(ois != null){
				ois.close();
				if(fis != null){
					fis.close();
				}
			}
		}
		return null;
	}

    private String image_path(String path, String name, String ext)
	{
		return path + "/" + name + "." + ext;
	}

	/*
	 *Main Method for testing
	 */
	public static void main(String args[])
	{
	    RSA rsa = new RSA();
	    BigInteger e= rsa.returne();
	    BigInteger d= rsa.returnd();
	    BigInteger N= rsa.returnN();

		new Steganography_Controller(
									new Steganography_View("Steganography"),
									new Steganography(),e,d,N
									);
	}
}
