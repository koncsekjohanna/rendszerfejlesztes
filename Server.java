import java.io.*;
import java.net.*;

public class Server {
    public static void main(String args[]) {
		int port = 7777;
		Server server = new Server( port );
		server.startServer();
    }

    ServerSocket echoServer = null;
    Socket clientSocket = null;
    int numConnections = 0;
	
    int port;
	
    public Server( int port ) {
		this.port = port;
    }

    public void stopServer() {
		System.out.println( "A Szerver leallt!" );
		System.exit(0);
    }

    public void startServer() {	
        try {
			echoServer = new ServerSocket(port);
        }
        catch (IOException e) {
			System.out.println(e);
        }   
	
		System.out.println( "A szerver fut es varja a csatlakozokat!" );
		System.out.println( "Barmelyik kliens meg tudja allitani a szervert az 'stop' gombbal." );
	
		while ( true ) {
			try {
				clientSocket = echoServer.accept();
				numConnections ++;
				ServerConnection oneconnection = new ServerConnection(clientSocket, numConnections, this);
				new Thread(oneconnection).start();
			}   
			catch (IOException e) {
				System.out.println(e);
			}
		}
    }	
}

class ServerConnection implements Runnable {
    BufferedReader is;
    PrintStream os;
    Socket clientSocket;
    int id;
    Server server;
	static int sumConnections = 0;
	
    public ServerConnection(Socket clientSocket, int id, Server server) {
		this.clientSocket = clientSocket;
		this.id = id;
		this.server = server;
		sumConnections++;
		System.out.println("Kapcsolatok szama: " + sumConnections);
		System.out.println( "Kapcsolat letesult a(z) " + id + ". szamu klienssel: " + clientSocket );
		try {
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e);
		}
    }

    public void run() {
        String line;
		try {
			boolean serverStop = false;

			while (true) {
                line = is.readLine();
				System.out.println( "Uzenet a(z) " + id + ". klienstol: " + line );
				if ( line.equals("stop") ) {
					serverStop = true;
					break;
				}
				if ( line.equals("quit")){
					sumConnections--;
					System.out.println("Kapcsolatok szama: " + sumConnections);
					break;
				}	
				//os.println("I got the message"); 				
			}
			
			System.out.println( "Az " + id + ". kapcsolat lezarult." );
            is.close();
            os.close();
            clientSocket.close();

			if ( serverStop ) server.stopServer();
		} catch (IOException e) {
			System.out.println(e);
		}
    }
}
