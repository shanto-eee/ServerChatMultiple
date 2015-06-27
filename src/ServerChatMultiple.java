import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ServerChatMultiple extends JFrame 
{
	
	private JTextField enterField;
	private JTextArea displayArea; 
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private int counter = 1;
	
	// constructor (setting GUI)
	public ServerChatMultiple()
	{
		this.setTitle("Server");
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(290, 460);
		this.setResizable(false);
		
		enterField = new JTextField();
		enterField.setBounds(0, 0, 284, 40);
		enterField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				sendData(event.getActionCommand());
				enterField.setText("");
				
			}
		});
		getContentPane().setLayout(null);
		enterField.setEditable(false);
		getContentPane().add(enterField);
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.setBounds(0, 40, 284, 392);
		getContentPane().add(scrollPane);
		
		repaint();
		
	}
	
	private void runServer()
	{
		try   // set up server to receive connection
		{
			server = new ServerSocket(12346,100);
			
			while(true)
			{
				try
				{
					waitForConnection();
					getStream();
					processConnection();
				} 
				catch(EOFException eofException)
				{
					displayMessage("\nServer Terminated Connection");
				}
				catch(IOException ioException)
				{
					ioException.printStackTrace();
				}  // end catch
				
				finally
				{
					closeConnection();
					++counter;
				}
			} //end while
			
		} // end first try
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}  // end catch
	} // end method runServer()
	
	private void waitForConnection() throws IOException
	{
		displayMessage("Waiting for connection ...\n");
		connection = server.accept();
		displayMessage("Connection" + counter + " receiver from " + connection.getInetAddress().getHostName());
		
	} // end method waitForConnection()
	
	private void getStream() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		
		displayMessage("\nGot I/O streams\n");
		System.out.println(Inet4Address.getLocalHost().getHostAddress());
	} // end of method getStream()
	
	private void processConnection() throws IOException
	{
		String message = "Connection Scuccessful";
		displayMessage(message);
		
		// TEST FLAG
		//displayMessage("\nin processcontrol, before seteditable = TRUE\n");
		
		setTextFieldEdiable(true);
		
		do
		{
			// TEST FLAG
			//displayMessage("\nin processcontrol, aftrer seteditable = TRUE\n");
			
			try
			{
				// TEST FLAG
				//displayMessage("\nin processcontrol: try, aftrer seteditable = TRUE\n");
				message = (String) input.readObject();
				displayMessage("\n" + message);
			}
			catch (ClassNotFoundException classNotFoundException)
			{
				displayMessage("\nUnknown object type received");
			}
		} while(!message.equals("CLIENT>>> TERMINATE"));
	} // end method processConnection()
	
	private void closeConnection() 
	{
		displayMessage("\nTerminating Connection ...\n");
		setTextFieldEdiable(false);
		
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
			System.out.println("TEST-io-io");
		}
		
	} // end method closeConnection()
	
	private void sendData(String message)
	{
		try
		{
			output.writeObject("SERVER>>> " + message);
			output.flush();
			displayMessage("\nSERVER>>> " + message);
		}
		catch(IOException ioException)
		{
			displayArea.append("\nError writing object");
		}
	} // end method sendData()
	
	private void displayMessage(final String messageToDisplay)
	{
		SwingUtilities.invokeLater(
				
				new Runnable()
				{
					public void run()
					{
						displayArea.append(messageToDisplay);
					}
				}
				
		);
	} // end method displayMessage(final String messageToDisplay)
	
	private void setTextFieldEdiable(final boolean editable)
	{
		SwingUtilities.invokeLater(
		
				new Runnable()
				{
					public void run()
					{
						// TEST FLAG
						//displayMessage("\nin SET EITABLE\n");
						enterField.setEditable(editable);
					}
				}
				
		);
	}
	
	public static void main(String[] args)
	{
		ServerChatMultiple appliServer = new ServerChatMultiple();
		appliServer.runServer();
	}
	
	
	

}
