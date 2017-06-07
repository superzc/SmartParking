package jiangx1.rose_hulman.iot.csse481;
import java.io.IOException;
import java.net.ServerSocket;

class SocketThread implements Runnable {
	public void run() {
		try {
			ServerSocket server = new ServerSocket(6543);
			while (true) {
				new SocketConnection(server.accept()).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}