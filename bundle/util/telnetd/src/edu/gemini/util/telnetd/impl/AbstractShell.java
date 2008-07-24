package edu.gemini.util.telnetd.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.net.Connection;
import net.wimpi.telnetd.net.ConnectionEvent;
import net.wimpi.telnetd.shell.Shell;

/**
 * Abstract shell that factors out shell-specific I/O. It automatically trims
 * whitespace from commands, ignores empty commands, and supports backspace.
 * It also supports ^D for logout. Subclasses normally only need to implement
 * the abstract methods and the factory method: <code>public static Shell 
 * createShell()</code>.
 * @author rnorris
 */
public abstract class AbstractShell implements Shell {

	private static final Logger LOGGER = Logger.getLogger(AbstractShell.class.getName());
	
	protected static enum State {
		
		/** Not connected. I/O properties are null. */
		CLOSED(false), 
		
		/** Connected and active. */
		CONNECTED(true),
		
		/** Exiting normally (via ^D). */
		EXITING(true),
		
		/** In the process of timing out. */
		TIMING_OUT(true),
		
		/** Timed out. */
		TIMED_OUT(false),
		
		/** Client crashed, or other network issue. */
		BROKEN_PIPE(false);
		
		private final boolean ioAvailable;
		
		private State(boolean io) {
			ioAvailable = io;
		}
		
		/**
		 * Returns true if we can expect I/O to be available. Otherwise the
		 * I/O mechanisms should not be trusted.
		 */
		public boolean isIOAvailable() {
			return ioAvailable;
		}
	}

	// These guys are set and unset in batch; they will either all be null, or
	// all be non-null.
	private Connection connection;
	private BasicTerminalIO basicTerminalIO;
	private OutputStream outputStream;
	private PrintStream printStream;
	
	// The current command that the user is typing in. This gets cleared out 
	// when the user hits enter.
	private final StringBuilder command = new StringBuilder();
	
	// Current connection state. See enum above.
	private State state = State.CLOSED;
	
	/**
	 * The main loop. The doc is unclear on whether shells will be re-used, so
	 * this loop cleans itself up on exit. 
	 */
	public void run(Connection con) {
		open(con);
		try {
			
			// Say hi.
			welcome();
			prompt();

			// Character/Line handling all happens in read();
			while (state == State.CONNECTED) {				
				flush();
				read(basicTerminalIO.read());					
			}
		
		} catch (EOFException eofe) {
			
			// This means the remote user disconnected abnormally, or the pipe
			// was otherwise broken.
			LOGGER.warning("User disconnected abnormally.");			
			state = State.BROKEN_PIPE;
			
		} catch (SocketException se) {
			
			// If the connection times out, the blocking call to read() will 
			// throw a SocketException, which we can ignore.
			if (state != State.TIMED_OUT) {
				state = State.BROKEN_PIPE;
				LOGGER.log(Level.WARNING, "Socket was closed unexpectedly. Exiting.");
			}
			
		} catch (Throwable t) {
			
			// This means something totally unexpected happened.
			LOGGER.log(Level.WARNING, "Trouble in telnet read loop. Exiting.", t);
			
		} finally {
			try {
				
				// Say goodbye, unless we timed out, in which case we have
				// already cleaned up. Rather lame, but I can't figure out a
				// cleaner way to handle this.
				if (state != State.TIMED_OUT) {
					cleanup();
					flush();
				}
				
			} catch (Throwable t) {
				LOGGER.log(Level.WARNING, "Trouble in telnet shell cleanup.", t);
			}			
		}
		close(con);
	}

	/**
	 * Flush all the I/O objects, working down the chain to the basicTerminalIO.
	 * Does nothing if I/O is unavailable in the current state.
	 */
	private void flush() throws IOException {
		if (state.isIOAvailable()) {
			printStream.flush();
			outputStream.close();
			basicTerminalIO.flush();
		}
	}
	
	private void open(Connection con) {
		
		// Initialize members.
		command.setLength(0);
		connection = con;
		basicTerminalIO = con.getTerminalIO();
		
		// Subclasses may want to deal with IO at a higher level, so let's
		// provide an OutputStream.
		outputStream = new OutputStream() {				
			@Override
			public void write(int b) throws IOException {
				basicTerminalIO.write((char) b);
			}				
		};
		
		// And a PrintStream.
		printStream = new PrintStream(outputStream);

		// Ok, all set up. Listen for events.
		con.addConnectionListener(this);
		state = State.CONNECTED;
	}
	
	private void close(Connection con) {
		
		// De-initialize everything.
		con.removeConnectionListener(this);
		printStream = null;
		outputStream = null;
		basicTerminalIO = null;
		connection = null;
		state = State.CLOSED;
		
	}
	
	/**
	 * Read a single character/control and build up a command. Execute the 
	 * command if the user hits enter. 
	 */
	private void read(int data) throws IOException {
		switch (data) {
		
		// Treat backspace and delete identically.
		case BasicTerminalIO.DELETE:
		case BasicTerminalIO.BACKSPACE:
			if (command.length() > 0) {
				command.setLength(command.length() - 1);						
				basicTerminalIO.moveLeft(1);
				basicTerminalIO.eraseToEndOfLine();
			}
			break;
		
		// Ignore cursor control keys. In the future we could use these for
		// more advanced editing.
		case BasicTerminalIO.UP:
		case BasicTerminalIO.DOWN:
		case BasicTerminalIO.LEFT:
		case BasicTerminalIO.RIGHT:
		case BasicTerminalIO.TABULATOR:
			break;

		// If the user hits enter, echo the character and then execute the 
		// command if it's not an empty string (after trimming).
		case BasicTerminalIO.ENTER:
			basicTerminalIO.write(BasicTerminalIO.CRLF);

			String cmd = command.toString().trim();										
			command.setLength(0);
			if (cmd.length() > 0) {
				exec(cmd);
			}
			
			prompt();
			break;
			
		// Otherwise just accept any alphanumeric character as part of the 
		// command. Ignore any control characters that we're not handling
		// above.
		default:
			if (data >= ' ' && data <= '~') {
				char c = (char) data;
				command.append(c);
				basicTerminalIO.write(c);
			} else {
				LOGGER.fine("Ignoring input character 0x" + Integer.toString(data, 16));				
			}
		}
	
	}
	
	public final void connectionIdle(ConnectionEvent ce) {
		LOGGER.info("Connection idle.");
	}

	public final void connectionTimedOut(ConnectionEvent ce) {
		try {
			state = State.TIMING_OUT;
			printStream.println();
			printStream.println("Connection timed out.");
			cleanup();
			flush();
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, "Trouble warning before closing timed out connection.", ioe);
		}
		state = State.TIMED_OUT;
		connection.close();			
	}

	public final void connectionLogoutRequest(ConnectionEvent ce) {
		try {
			state = State.EXITING;
			printStream.println("^D");
			flush();
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, "Trouble warning before closing connection on ^D.", ioe);
		}
	}

	public final void connectionSentBreak(ConnectionEvent ce) {
		LOGGER.fine("Got a break from the user. Ignoring.");
	}
	
	/**
	 * Returns the low-level terminal IO, which can be used for cursor movement,
	 * color changes, etc.
	 */
	protected BasicTerminalIO getBasicTerminalIO() {
		return basicTerminalIO;
	}

	/**
	 * Returns the connection object, from which you can query various things
	 * about the connection.
	 * @return
	 */
	protected Connection getConnection() {
		return connection;
	}

	/**
	 * Returns an output stream that writes to the low-level terminal IO.
	 * @return
	 */
	protected OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Returns a print stream that writes to the low-level terminal IO.
	 * @return
	 */
	protected PrintStream getPrintStream() {
		return printStream;
	}

	/**
	 * Return's the shell's connection state.
	 * @return
	 */
	protected State getState() {
		return state;
	}

	/**
	 * Implementations should print out a welcome message.
	 * @throws IOException
	 */
	protected abstract void welcome() throws IOException;
	
	/**
	 * Implementations should print out a prompt. It should be assumed that
	 * you are at the beginning of the line already, so you don't need to 
	 * start with a CRLF.
	 * @throws IOException
	 */
	protected abstract void prompt() throws IOException;
	
	/**
	 * Implementations should execute the passed command. Any output produced
	 * by execution should be terminated via a CRLF.
	 * @param command
	 * @throws IOException
	 */
	protected abstract void exec(String command) throws IOException;
	
	/**
	 * This will be called just before the shell exits. Cleanup should be done 
	 * in a way that is immune to broken pipes, normally by switching on the 
	 * value of getState(). 
	 */
	protected abstract void cleanup() throws IOException;
	
}
