package com.logFileAnalyser.srvice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.logFileAnalyser.Dto.LogFilesReqDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class Logfileservice2 {
	
	
	

	@Autowired
	private JavaMailSender javaMailSender;
	
	static String localPath = "C:/partwalaStg";
	static String remotePath = "/applications/logs/";
	static String remotePathArchived = "/applications/logs/archived/";
	
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	 static String todayDate = df.format(new Date());
	static File  outfile = new File(localPath+"/"+todayDate+".log");;
	
	
	 
	 
	 
	private String search;
		private static String host;
		private static Integer port;
		private static String user;
		private static String password;
		
		private static JSch jsch;
		private static Session session;
		private static Channel channel;
		private static ChannelSftp sftpChannel;
		
		/*public LogfileService(String host, Integer port, String user, String password) {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
		}*/
		
	//public LogfileService() { }

		public static void connect() {
			
			System.out.println("connecting..."+host);
			try {
				jsch = new JSch();
				session = jsch.getSession(user, host,port);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect();

				channel = session.openChannel("sftp");
				channel.connect();
				sftpChannel = (ChannelSftp) channel;

			} catch (JSchException e) {
				e.printStackTrace();
			}

		}
		
		public static void disconnect1() {
			System.out.println("disconnecting...");
			sftpChannel.disconnect();
			channel.disconnect();
			session.disconnect();
		}
		
		
	public void LogfileService(LogFilesReqDto req) throws IOException {
	

		
	//LogfileService ftp = new LogfileService("192.168.68.100", 22, "santhosh.kumar", "setar@123");
		//ftp.connect();//ftp.download(req, localPath);
		
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		
		for(String date : req.getDate()) {
			if(date !=null && !date.contentEquals(todayDate)) {
				
				remotePath=" /applications/logs/";
				
			}
			else {
				remotePath=" /applications/logs/archived/";
			}
		}
		
		//connect();
		download(req, localPath);
		
		
		
		
		
	}
	
	//ls
	public void download(LogFilesReqDto reqDto, String localDir) throws IOException {

		
		connect();
		
		//outfile = new File(localPath+"/"+todayDate+".log");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			
		try {
			
			Vector<ChannelSftp.LsEntry> entries =sftpChannel.ls("/applications/logs/archived/");
	        System.out.println(entries);
	       
	        for (ChannelSftp.LsEntry en : entries) {
	        
	        	for(String ServerName :reqDto.getServerName()) {
	        		for(String date :reqDto.getDate()) {
	        			 if(date !=null && ServerName!=null)this.search=ServerName;
	     	    		else if(date ==null)this.search=ServerName;
	     	    		else if(ServerName==null)this.search=date;
	        			
	        			//if((en.getFilename().contains(ServerName))	&& (en.getFilename().contains(date))) {
	            if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
	            	
	                continue;
	            }
	           
	          
	           //else if(en.getFilename().contains("titan-user-server.2024-01-25.41.log"))
	            
	          // else if((en.getFilename().contains(ServerName))	&& (en.getFilename().contains(date)))
	            
	            
	         //  else if((en.getFilename().contains(ServerName))	&& (en.getFilename().contains(date)))
	            else if((en.getFilename().contains(search)))
	            {
	            	if(date != null && en.getFilename().contains(date) ) {
	            		System.out.println("check date");
	            		 String fileName="/applications/logs/archived/"+en.getFilename();
	            		 sftpChannel.get("/applications/logs/archived/"+en.getFilename(), en.getFilename());
	     	            downloadmultipule(writer,fileName , localDir);
	            	
	            	}else if(date == null || date.isEmpty()) {
	            		
	            	System.out.println(en.getFilename());
	            System.out.println("server : "+ServerName +" , date "+date);
	            String fileName="/applications/logs/archived/"+en.getFilename();
	            sftpChannel.get("/applications/logs/archived/" + en.getFilename(), en.getFilename());
	            downloadmultipule(writer,fileName , localDir);
	        }}}}
	        }//}
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
			writer.close();
		this.sendMailWithAttachment(outfile, reqDto.getEmail());

		disconnect1();
	}

	
	public static void downloadmultipule(BufferedWriter writer, String rfilePath, String localDir) throws SftpException, IOException {
		
		System.out.println("fileName :: " +rfilePath);
		// Change to output directory
		String cdDir = rfilePath.substring(0, rfilePath.lastIndexOf("/") + 1);
		System.out.println("cdDir :: " +cdDir);
		System.out.println( "\n");
		sftpChannel.cd(cdDir);
		 
	   
		
		System.out.println("rfilePath  :: "+rfilePath);
		Path path=Paths.get(rfilePath);
		Path fname =path.getFileName();
		String fileName=fname.toString();
		System.out.println("fname ::"+fname);
		writer.write("\n**********"+fileName+"**********\n\n");
		
		InputStream stream = sftpChannel.get(rfilePath);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
            	
            	if (line.contains("ERROR")) {
            		 System.out.println("line  "+line);
                	//foundPattern = true;
            		System.out.println(" TRUE");
       			 
                    String[] words = line.split(" ");
                    writer.write( "Time : "+words[0]+"\n"
                    +"UserId : "+words[3]+"\n" +"URL : "+words[4]+"\n" +"Error : "+reader.readLine()+"\n" +reader.readLine()+"\n"+"\n"); 
                   
            	}
            	}
         
            reader.close();
            }finally {
        }
		
        

	
//	disconnect1();
		
	}
	
	
	public void sendMailWithAttachment(File file, String[] emails) {
		
		try {
			MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			//mimeMessageHelper.setTo("sneha.vk@dikshatech.com");
			mimeMessageHelper.setTo(emails[0]);
		
			//String[] cc = {emails[1],emails[2]};
			//String[] cc = {"mail1@gmail.com","mail2@gmail.com"};
			//for(String cc:emails) { mimeMessageHelper.setCc(cc);}
			// mimeMessageHelper.setCc(cc);
			mimeMessageHelper.setSubject("Log File Analyser RD9 : "+todayDate);
			mimeMessageHelper.setText("Hi ,\n\n"
					+ "Please find attachment of LogFile Analyser for  "+todayDate
					+ "\n\nThanks & Regards\n sneha.vk");
			
			FileSystemResource fileSystemResource = new FileSystemResource(file);
			mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
			javaMailSender.send(mimeMessage);
			System.out.println("mail sent");
			
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("not sent");
		}
	}

}
