package common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.bean.CommentData;
import common.bean.EbusinessData;

public class DataClassUtil {
	/**
	 * 根据属性名获取属性值
	 * */
	private static Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取属性名数组
	 * */
	private static String[] getFiledName(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			System.out.println(fields[i].getType());
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}

	/**
	 * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
	 * */
	private static List getFiledsInfo(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		List list = new ArrayList();
		Map infoMap = null;
		for (int i = 0; i < fields.length; i++) {
			infoMap = new HashMap();
			infoMap.put("type", fields[i].getType().toString());
			infoMap.put("name", fields[i].getName());
			infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
			list.add(infoMap);
		}
		return list;
	}

	/**
	 * 获取对象的所有属性值，返回一个对象数组
	 * */
	public static Object[] getFiledValues(Object o) {
		String[] fieldNames = getFiledName(o);
		Object[] value = new Object[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			value[i] = getFieldValueByName(fieldNames[i], o);
		}
		return value;
	}

	public static void print(Object o, int level) {
		List list = getFiledsInfo(o);
		for (Object object : list) {
			HashMap map = (HashMap) object;

			if (map.get("type").toString().contains("java.lang.String")) {
				for (int i = 0; i < level; i++)
					System.out.print("\t");
				System.out.println(map.get("name") + "\t" + map.get("value"));
			}

			else if (map.get("type").toString().contains("java.util.List")) {
				for (int i = 0; i < level; i++)
					System.out.print("\t");
				System.out.println(map.get("name"));
				
				level++;
				List l2 = (List) map.get("value");
				for (Object object2 : l2) {
					print(object2, level);
					
				}
				level--;
			}

		}
	}

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
		int lv = 0;
		print(ed, lv);
	}
}
