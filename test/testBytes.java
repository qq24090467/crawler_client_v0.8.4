

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.hbase.util.Bytes;

public class testBytes {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String s="abcdefg";
		
		byte[] b=Bytes.toBytes(s);
		
		String n=new String(b);
		
		System.out.println(n);
	}

}
