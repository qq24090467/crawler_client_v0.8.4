

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.util.StringUtil;

public class testRegex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String content = "g_config.dynamicScript = function(f,c){var e=document,d=e.createElement(\"script\");d.src=f;if(c){for(var b in c){d[b]=c[b];}};e.getElementsByTagName(\"head\")[0].appendChild(d)};   g_config.dynamicScript(\"http://dsc.taobaocdn.com/i5/200/910/20291210056/TB1chrrFVXXXXaqaXXX8qtpFXXX.desc%7Cvar%5Edesc%3Bsign%5E1f387bd8a3d1b72d9985e22acf1e8df6%3Blang%5Egbk%3Bt%5E141044613\")";
		String imgsUrl = StringUtil.regMatcher(content, "dynamicScript\\(\"", "\"\\)");

		System.out.println(imgsUrl);

	}

}
