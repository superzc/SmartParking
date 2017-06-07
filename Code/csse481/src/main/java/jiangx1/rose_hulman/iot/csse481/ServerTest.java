package jiangx1.rose_hulman.iot.csse481;

public class ServerTest {
	public static void main(String[] args) {
		Thread thr = new Thread(new SocketThread());
		thr.start();
		//App a = new App();
		//System.out.println(a.dataListFinal);
	}
}