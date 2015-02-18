import java.util.Calendar;

import org.apache.hadoop.classification.InterfaceAudience.Public;

import sun.security.util.Length;

public class testThread extends Thread {

	public static int n = 0;

	public void run() {
		int m = n;
		yield();
		m++;
		n = m;
	}

	public static void main(String[] args) throws Exception {

		testThread myThread = new testThread();
		Thread threads[] = new Thread[100];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(myThread);
		}
		for(int i=0;i<threads.length;i++){
			threads[i].start();
		}
		
		for(int i=0;i<threads.length;i++){
			threads[i].join();
		}
		
		System.out.println("n: "+myThread.n);
	}

	private String getTime() {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTimeInMillis(System.currentTimeMillis());
		return rightNow.get(Calendar.HOUR) + ":" + rightNow.get(Calendar.MINUTE) + ":" + rightNow.get(Calendar.SECOND);
	}
}