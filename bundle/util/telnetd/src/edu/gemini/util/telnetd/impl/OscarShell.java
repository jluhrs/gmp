package edu.gemini.util.telnetd.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import net.wimpi.telnetd.shell.Shell;

import org.ungoverned.osgi.service.shell.ShellService;

public class OscarShell extends AbstractShell {

	private static final Logger LOGGER = Logger.getLogger(OscarShell.class.getName());
	private static ShellService shellService;
	
	private String localHostName;
	private Handler handler;
	
	public synchronized static void setShellService(ShellService shellService) {
		OscarShell.shellService = shellService;
	}

	public static Shell createShell() {
		return new OscarShell();
	}

	@Override
	protected void welcome() throws IOException {
		
		PrintStream ps = getPrintStream();
		String remoteHostName = getConnection().getConnectionData().getHostName();

		localHostName = InetAddress.getLocalHost().getHostName();
		int pos = localHostName.indexOf('.');
		if (pos != -1)
			localHostName = localHostName.substring(0, pos);

		handler = new StreamHandler(getOutputStream(), new SimpleFormatter()) {
			@Override
			public synchronized void publish(java.util.logging.LogRecord record) {
				super.publish(record);
				flush();
			}
		};
				
		ps.println();
		ps.println("Welcome to Oscar");
		ps.println("================");
		ps.println();
		ps.println("Remote connection from " + remoteHostName);		
		ps.println("Local time is " + new Date());		
		ps.println("Be careful. Hit ^D to exit.");
		ps.println();
	
		Logger.getLogger("").addHandler(handler);

	}

	@Override
	protected void prompt() throws IOException {
		getPrintStream().print(localHostName + " -> ");
	}

	@Override
	protected void exec(String command) throws IOException {
		synchronized (OscarShell.class) {
			if (shellService != null) {
				try {
					PrintStream ps = getPrintStream();
					shellService.executeCommand(command, ps, ps);
				} catch (IOException ioe) {
					throw ioe;
				} catch (Throwable t) {
					LOGGER.log(Level.WARNING, "Trouble executing command: " + command, t);
				}
			} else {
				getPrintStream().println("No shell service is available, sorry.");
			}
		}		
	}

	@Override
	protected void cleanup() throws IOException {
		
		// Clean up log handler.
		handler.close();
		Logger.getLogger("").removeHandler(handler);
		handler = null;
		
		LOGGER.info("Remote access shell shutting down.");
		if (getState().isIOAvailable()) {
			getPrintStream().println("Good Bye.");
		}
		
	}

}
