package chat;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.*;
import javax.swing.border.Border;

public class chatUI extends JFrame {
	private static final int LARGE_FONT_SIZE = 28;
	private static final int MEDIUM_FONT_SIZE = 20;

	// defined at the class level so addAMessage()
	//   can update it with incoming messages.
	private JPanel messagesReceivedPanel;
	private chatNetwork networkStuff;

    public chatUI() {
    	setTitle("Chit-Chat");
    	
       // The default layout for the content pane
        // of a JFrame is BorderLayout
        Container contentPane = getContentPane();

        // Note, to find our IP address under Windows
        //   execute ipconfig from cmd line.
        String addressIP4 = null;
        try {
			addressIP4 = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

		JLabel ipLabel = new JLabel(addressIP4);
        ipLabel.setFont(new Font("SansSerif", Font.PLAIN, LARGE_FONT_SIZE));
        ipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(ipLabel,BorderLayout.NORTH);

        JComponent scrollableMessagesReceived = buildMessagesReceivedPanel();
        contentPane.add(scrollableMessagesReceived,BorderLayout.CENTER);

        JComponent sendMessagePanel = buildSendMessagePanel();
        contentPane.add(sendMessagePanel,BorderLayout.SOUTH);
    }

	private JComponent buildMessagesReceivedPanel() {
      	   messagesReceivedPanel = new JPanel();

    	   messagesReceivedPanel.setLayout(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(messagesReceivedPanel);

           Border titledBorder = BorderFactory.createTitledBorder("Receive");
		Border compoundBorder = BorderFactory.createCompoundBorder(
                titledBorder,
                scrollPane.getBorder());
		scrollPane.setBorder(compoundBorder);
		scrollPane.repaint();
		return scrollPane;
	}

    private void addAMessage(String msg) {
		GridBagConstraints constraints = new GridBagConstraints();

		// Defaults
		constraints.gridx = 0;
		constraints.gridy = GridBagConstraints.RELATIVE;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(2,2,2,2);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;

		
		JTextArea messageTextArea = new JTextArea(msg,4,20);
		messageTextArea.setEditable(false);
    	messageTextArea.setLineWrap(true);
    	messageTextArea.setWrapStyleWord(true);
        messageTextArea.setSize(200,200);
    	messageTextArea.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
    	JScrollPane sourceScrollPane = new JScrollPane(messageTextArea);
    	messagesReceivedPanel.add(sourceScrollPane,constraints);
    	this.validate();
	}

	private JComponent buildSendMessagePanel() {
    	JPanel messagePanel = new JPanel();

    	messagePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();

		// Defaults
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(2,2,2,2);
		constraints.anchor = GridBagConstraints.NORTHWEST;
    	   	
    	JLabel ipLabel = new JLabel("Receiver's IP:");
        ipLabel.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		messagePanel.add(ipLabel,constraints);

		constraints.gridy = 1;
    	JLabel senderNameLabel = new JLabel("Sender's Name:");
    	senderNameLabel.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		messagePanel.add(senderNameLabel,constraints);

		constraints.weighty = 1;
		constraints.gridy = 2;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		messagePanel.add(messageLabel,constraints);
 
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		JTextField ipTextField = new JTextField(20);
    	ipTextField.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		messagePanel.add(ipTextField,constraints);

		constraints.gridy = 1;
    	JTextField senderNameTextField = new JTextField(20);
    	senderNameTextField.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		messagePanel.add(senderNameTextField,constraints);

		constraints.gridy = 2;
		constraints.weighty = 1;
		JTextArea messageTextArea = new JTextArea(5,20);
    	messageTextArea.setLineWrap(true);
    	messageTextArea.setWrapStyleWord(true);
        
    	messageTextArea.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
    	JScrollPane sourceScrollPane = new JScrollPane(messageTextArea);
		messagePanel.add(sourceScrollPane,constraints);

		constraints.weighty = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.NONE;
    	JButton sendButton = new JButton("Send");
        
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				String IPnum = ipTextField.getText();
				String Sender = senderNameTextField.getText();
				String Message = messageTextArea.getText();
				JsonObject messageObject = Json.createObjectBuilder().add("name", Sender).add("message", Message)
						.build();
				
				class Send implements Runnable {
					String ip;
					JsonObject message;
					
					Send (JsonObject Mess, String IP){
						message = Mess;
						ip = IP;
					}

					@Override
					public void run() {
						try {
							networkStuff.sendChat(message, ip);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				Thread SendThread = new Thread(new Send(messageObject, IPnum));
				SendThread.start();
			}

		});

    	sendButton.setFont(new Font("SansSerif", Font.PLAIN, MEDIUM_FONT_SIZE));
		messagePanel.add(sendButton,constraints);
      
        Border titledBorder = BorderFactory.createTitledBorder("Send");
		messagePanel.setBorder(titledBorder);
		return messagePanel;
	}

	public static void main(String[] args) throws Exception {
        chatUI frame = new chatUI();
        frame.pack();
        frame.setVisible(true);
        frame.networkStuff = new chatNetwork();
        frame.networkStuff.listen();

        
        /*
   	   ((chatUI) frame).addAMessage("From:Larry @ 134.193.12.34\nHey Jimmy! What's up? Want to go play frisbee?");
  	   ((chatUI) frame).addAMessage("From:Mindy @ 134.193.52.77\nJimmy, can you help me with my Java homework?");
  	   */
    }
}