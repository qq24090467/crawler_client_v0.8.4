import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.net.aso.l;
import common.bean.CommentData;
import common.bean.EbusinessData;
import common.util.DataClassUtil;

public class testClass {
	

	public static void main(String[] args) {
		EbusinessData ed = new EbusinessData();
		ed.setTitle("abc德国");
		ed.setBrand("三角");
		ed.setId(12341321);
		CommentData cd = new CommentData();
		cd.setComment_id("1");
		cd.setComment_info("好不好");
		CommentData cd2 = new CommentData();
		cd.setComment_id("2");
		cd.setComment_info("好");
		List<CommentData> l = new ArrayList<CommentData>();
		l.add(cd);
		l.add(cd2);
		ed.setComments(l);
		
		DataClassUtil.print(ed, 0);
	}
}
