package jiangx1.rose_hulman.iot.csse481;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

class SocketConnection extends Thread {
	InputStream input;
	PrintWriter output;
	Socket socket;
	ArrayList<String> occ = new ArrayList<String>();
	HashMap<Integer, String> parkSlot = new HashMap<Integer, String>();
	int number = 0;
	public SocketConnection(Socket socket) {
		super("Thread 1");
		this.socket = socket;
		try {
			input = socket.getInputStream();
			output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String[] elementgener(int num, String startst, String commandReads) {
		String[] element = new String[num];
		int count=0;
		int pre=startst.length();
		for (int i = startst.length(); i < commandReads.length();i++){
			if(commandReads.charAt(i)=='|'){
				element[count]=commandReads.substring(pre, i);
				pre=i;
				count++;
			}
		}
		return element;
	}
	HashMap<String,String> database = new HashMap<String,String>();
	
	public void initialmap() {
		ArrayList<String> dataList = new ArrayList<String>();
		App a = new App();
		dataList=a.dataListFinal;
		for (int i = 0; i<dataList.size();i++) {
			String st=dataList.get(i);
			database.put(st.substring(0, st.indexOf('[')), st.substring(st.indexOf('[')));
		}
	}
	
	public String matchup(String des){
		String ans = "";
		des = des.substring(des.indexOf(":")+1);
		double des_x = Double.parseDouble(des.substring(1, des.indexOf(',')));
		double des_y = Double.parseDouble(des.substring(des.indexOf(',') + 1, des.indexOf(')')));
		double minmin=Integer.MAX_VALUE;
		String index ="";
		for (String i:database.keySet()) {
			if (!occ.contains(i)){
				String st=database.get(i);
				double x = Double.parseDouble(st.substring(1, st.indexOf(',')));
				double y = Double.parseDouble(st.substring(st.indexOf(',')+1,st.indexOf(']')));
				
				if (minmin>Math.sqrt((x-des_x)*(x-des_x)+(y-des_y)*(y-des_y))) {
					minmin=Math.sqrt((x-des_x)*(x-des_x)+(y-des_y)*(y-des_y));
					index=i;
				}
			}
		}
		occ.add(index);
		parkSlot.put(number, index);
		number++;
		return index+database.get(index);
	}
	@Override
	public void run() {
		try {
			
			initialmap();
			while (true) {
			//	do {
				    byte array[] = new byte[1024];
					int readed = input.read(array);
					String sendString = "";
					System.out.println("readed == " + readed + " " + new String(array).trim());
					String commandReads = new String(array).trim();
					if (commandReads.startsWith("Request_parkinglot|")) {
						String[] elements = new String[3];
						elements = elementgener(3, "Request_parkinglot|", commandReads);
						String ans = matchup(elements[2]);
						sendString=ans;
					} else if (commandReads.startsWith("Cancel_parkinglot|")) {
						String[] elements = new String[1];
						elements = elementgener(1, "Cancel_parkinglot|", commandReads);
						String ans = cancel(elements[0]);
						sendString=ans;
					} else if(commandReads.startsWith("Change_destination|")){
						String[] elements = new String[2];
						elements = elementgener(2, "Change_destination|", commandReads);
						String ans = change(elements[0], elements[1]);
						//System.out.println(ans);
						sendString=ans;
					} else {
						System.err.println("Unknown command");
						input.close();
						socket.close();
						continue;
					}
					sendString = new String(sendString.getBytes(), Charset.forName("UTF-8"));
					output.write(sendString);
					output.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String change(String request, String des2) {
		
		return cancel(request)+"your new destination has been changed to "+matchup(des2);
	}
	public String cancel(String request) {
		// TODO Auto-generated method stub
		String st = request.substring(request.indexOf(':')+1);
		parkSlot.remove(Integer.valueOf(st));
		return "Your Request has been cancelled";
	}
}
