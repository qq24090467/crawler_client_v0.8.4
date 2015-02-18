import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;
import common.system.Systemconfig;
import common.util.EncoderUtil;
import common.util.StringUtil;

public class testDown {

	public static void main(String[] args) {

		String sk = "轮胎";
		String url = "http://news.163.com/14/0922/08/A6O1CSFC00014SEH.html";
//		url = "http://mp.weixin.qq.com"
//				+ "/mp/getappmsgext?"
//				+ "__biz=MjM5ODE1NTMxMQ=="
//				+ "&mid=null&uin=MjAxNTY0MjU%3D&key=2f5eb01238e84f7ebf8bcb9bb078eb78bc9a14395a8d139332160d7a4e135336e54932130eec7d2ec589d1d323246c7f";
//		url = "http://mp.weixin.qq.com"
//				+ "/mp/getappmsgext?"
//				+ "__biz=MzAwODEzMzA2Ng=="
//				+ "&mid=202013470"
//				// + "&idx=1"
//				// + "&scene="
//				// + "&title=%E6%AC%A2%E8%BF%8E%E4%BD%BF%E7%94%A8kjson"
//				// + "&ct=1419257608"
//				// + "&devicetype=android-19"
//				// + "&version="
//				// + "&r=0.1664896688889712"
//				+ "&uin=MjAxNTY0MjU%3D"
//				+ "&key=2f5eb01238e84f7ebf8bcb9bb078eb78bc9a14395a8d139332160d7a4e135336e54932130eec7d2ec589d1d323246c7f"
//				// +
//				// "&pass_ticket=b3hV91xTLYZxRGKemRNz%2FAi4VKElPnwHYUNtoV8w4dE%3D"
//
//				// + "/mp/getappmsgext?"
//				// + "__biz=MjM5NDg5MzY1MA=="
//				// + "&mid=203906320"
//				// + "&uin=MjAxNTY0MjU%3D"
//				// // +
//				// //
//				// "&key=2f5eb01238e84f7e4393b7797da34c1edf9d4cf977a194764732d34288a30ab9368c6f2a7f5c5502fcf98e741aefc025"
//				// +
//				// "key=2f5eb01238e84f7e2d0d11114f636c87d6f90720cfc3522193023f488877ce982ebb180611c476b7bce73e57292202a1"
//				+ "";
		HtmlInfo html = new HtmlInfo();

		String charSet = "UTF-8";
		html.setType("DATA");
		html.setEncode(charSet);
		html.setOrignUrl(url);
//		html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
//		html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");

		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		StringUtil.writeFile("aaa/meat.htm", html.getContent(), charSet);

		System.out.println("content:" + html.getContent());

		String content = html.getContent();

		System.out.println(">>>\r\n" + content + "\r\n<<<");

	}
}
