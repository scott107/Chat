package chat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.json.JsonObject;

public class chatNetwork {
	
	 public static final int PortNumber = 8087;
	 
	 public void listen() throws IOException{
         ServerSocket ss = new ServerSocket(PortNumber);
         
         while(true){
         
         Socket clientSocket = ss.accept();
         
         System.out.println("Request from client:" + clientSocket);
         
         OutputStream out = clientSocket.getOutputStream();
         InputStream in = clientSocket.getInputStream();


         System.out.println("Client closed connection");
         
         if (in != null)
             in.close();
         if (out != null)
             out.close();
         if (clientSocket != null)
             clientSocket.close();     
         }
	 }
	 
	 public void sendChat(JsonObject message, String ipAddy) throws IOException{
		 		 Socket s = new Socket(ipAddy , PortNumber + 1);
		 		 try (OutputStreamWriter out = new OutputStreamWriter(
		 				s.getOutputStream(), StandardCharsets.UTF_8)) {
		 			 	out.write(message.toString());
		 }
		 		 s.close();
	 }
	 
}