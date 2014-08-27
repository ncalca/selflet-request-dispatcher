package it.polimi.elet.selflet.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.jetty.util.log.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHConnection {

	private JSch jsch;
	private Session session;
	private Properties config;
	private Channel channel;
	private SftpTransferMonitor monitor;

	/**
	 * Costructor used with password
	 * 
	 * @param userID
	 *            username
	 * @param domain
	 *            host name or ip address
	 * @param port
	 *            port used
	 * @param password
	 *            password
	 */
	public SSHConnection(String userID, String domain, int port, String password) {
		jsch = new JSch();
		config = new Properties();
		monitor = new SftpTransferMonitor();
		try {
			session = jsch.getSession(userID, domain, port);
			session.setPassword(password);
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
		} catch (JSchException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Costructor used with private key
	 * 
	 * @param userID
	 *            username
	 * @param domain
	 *            host name or ip address
	 * @param pkey
	 *            private key
	 * @param port
	 *            port used
	 */
	public SSHConnection(String userID, String domain, String pkey, int port) {
		jsch = new JSch();
		config = new Properties();
		monitor = new SftpTransferMonitor();
		try {
			jsch.addIdentity(pkey);
			session = jsch.getSession(userID, domain, port);
			session.connect();
		} catch (JSchException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Disconnect the session
	 */
	public void disconnect() {
		this.session.disconnect();
	}

	/**
	 * Excecute a command over the connection
	 * 
	 * @param command
	 *            command to excecute
	 */
	public void executeWithoutOutput(String command) {
		try {
			channel = session.openChannel("exec");
			ChannelExec clientExec = (ChannelExec) channel;
			clientExec.setCommand(command);
			// clientExec.setInputStream(null); // < /dev/null
			// clientExec.setOutputStream(System.out);
			// clientExec.setErrStream(System.err); // forward stderr to JVM's
			clientExec.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			channel.disconnect();
		}

	}

	/**
	 * Excecute a command over the connection
	 * 
	 * @param command
	 *            command to excecute
	 */
	public void execute(String command) {
		try {
			channel = session.openChannel("exec");
			ChannelExec clientExec = (ChannelExec) channel;
			clientExec.setCommand(command);
			clientExec.setInputStream(null); // < /dev/null
			clientExec.setOutputStream(System.out);
			clientExec.setErrStream(System.err); // forward stderr to JVM's
			InputStream is = clientExec.getInputStream();
			clientExec.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (is.available() > 0) {
					int i = is.read(tmp, 0, 1024);
					if (i < 0)
						break;
					Log.debug(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					Log.debug("exit-status:" + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			channel.disconnect();
		}

	}

	/**
	 * Put a file from the source path to a remote path
	 * 
	 * @param srcFile
	 *            source path
	 * @param dstFile
	 *            destination path
	 */
	public void putFile(String srcFile, String dstFile) {
		FileInputStream fileInputStream = null;
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp clientFtp = (ChannelSftp) channel;
			File file = new File(srcFile);
			fileInputStream = new FileInputStream(file);
			clientFtp.put(fileInputStream, dstFile, monitor,
					ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			channel.disconnect();
			if (fileInputStream != null)
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	/**
	 * Get a file from a remote source to a local path
	 * 
	 * @param srcFile
	 *            source path
	 * @param dstFile
	 *            destination path
	 */
	public void getFile(String srcFile, String dstFile) {
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp clientFtp = (ChannelSftp) channel;
			clientFtp.get(srcFile, dstFile, monitor, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			channel.disconnect();
		}
	}

	public void getFiles(String filesFolder, String destFolder){
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp clientFtp = (ChannelSftp) channel;
			clientFtp.cd(filesFolder);
			Vector<ChannelSftp.LsEntry> list = clientFtp.ls("*.log");
			for(ChannelSftp.LsEntry entry : list) {
				clientFtp.get(entry.getFilename(), destFolder + entry.getFilename());
			}
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putFile(InputStream inputStream, String destinationFile) {
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp clientFtp = (ChannelSftp) channel;
			clientFtp.put(inputStream, destinationFile, monitor,
					ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		}

	}

}
