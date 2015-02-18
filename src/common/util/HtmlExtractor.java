package common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.util.URI;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Html Extractor <BR>
 * get content and image links <BR>
 * 
 * @author Ahui Wang
 * 
 */
public class HtmlExtractor {
	/** tags to be removed */
	public static String[] REMOVE_TAGS = null;
	private static String removeTags;
	/** tables that contains content */
	public static String[] TABLE_TAGS = null;
	private static String tableTags;
	public static boolean downloadImg = true;
	/**
	 * used for filter image
	 */
	public static String[] IMAGE_TYPE = null;
	private static String imageType;
	/**
	 * Filter images which are below this height threshold
	 */
	public static int imgHeight = 200;

	/**
	 * Filter images which are below this width threshold
	 */
	public static int imgWidth = 200;

	public static double positiveRatios = 0.2;
	
	public static int imgSize = 400;

	public static String[] INVALID_BEG;
	private static String invalidBeg;
	
	DOMUtil dom = new DOMUtil();
	
	private List<String> invalidWords;
	private List<String> invalidImgs;
	public void init() {
		REMOVE_TAGS = removeTags.split("\\s");
		TABLE_TAGS = tableTags.split("\\s");
		IMAGE_TYPE = imageType.split("\\s");
		INVALID_BEG = invalidBeg.split(",");
		invalidWords = StringUtil.contentList("config/invalid.dic");
		invalidImgs = StringUtil.contentList("config/img.dic");
	}
	
	/**
	 * extract content from html
	 * @param html
	 * @param charset
	 * @return content
	 */
	public String extractContent(String html, String charset) {
		Node domTree = dom.ini(html, charset);
		if (domTree == null) {
			return null;
		}
		int totalUnlinkWords = 1;
		//剔除无关标签
		removeTags(domTree);
		//统计无链接在内的字数
		totalUnlinkWords = countUnLinkWords(domTree);
		List<WeightedTable> tableNodes = new ArrayList<WeightedTable>();
		getTableNodes(domTree, tableNodes, totalUnlinkWords);
		
		WeightedTable wt = minWeithtTable(tableNodes);
		StringBuilder content = new StringBuilder();
		if (wt == null) {
			strictRemove(domTree);
			getText(domTree, content);
		} else {
			getText(wt.getTableNode(), content);
		}
		String con = content.toString();
		for(String s : invalidWords)
			con = con.replace(s, "");
		
		tableNodes.clear();
		tableNodes = null;
		domTree = null;
		return con.replaceAll("\n+\\s+", "\n  ");
	}

	/**
	 * extract content , image links , video blocks from html
	 * @param html
	 * @param charset
	 * @param url
	 * @return extract result{image links , content , video blocks};
	 */
	public ExtractResult extract(String html, String charset, String url) {
		Node domTree = dom.ini(html, charset);
		if (domTree == null) {
			return null;
		}
		Node purgeTable = null;
		int totalUnlinkWords = 1;

		//剔除无关标签
		removeTags(domTree);
		//抽取标题
		String title = getTitle(domTree);
		//统计文本中无链接的数量
		totalUnlinkWords = countUnLinkWords(domTree);
		List<WeightedTable> tableNodes = new ArrayList<WeightedTable>();
		getTableNodes(domTree, tableNodes, totalUnlinkWords);
		WeightedTable contentTable = minWeithtTable(tableNodes);
		List<String> imgs = new ArrayList<String>();
		if (contentTable != null)
			removeInputNode(contentTable.getTableNode());
		boolean index = false;
		StringBuilder content = new StringBuilder();
		do {
			index = false;
			purgeTable = null;
			if (contentTable != null)
				purgeTable = getPurge(contentTable.getTableNode());
			if (purgeTable == null) {
				getImages(url, domTree, charset, imgs);
				strictRemove(domTree);
				getText(domTree, content);
			} else if (purgeTable.getNodeName().equalsIgnoreCase("p")) {
				getPText(purgeTable, content);
			} else {
				getImages(url, purgeTable, charset, imgs);
				getText(purgeTable, content);
			}
			for (String str : INVALID_BEG) {
				if (content.toString().trim().indexOf(str) == 0) {
					index = true;
					tableNodes.remove(contentTable);
					contentTable = minWeithtTable(tableNodes);
					content.delete(0, content.length());
					break;
				}
			}
		} while (index);
		
		String con = content.toString();
		for(String s : invalidWords)
			con = con.replace(s, "");
		con = con.replaceAll("\n+\\s+", "\n  ");
		
		String img = "";
		for(String s : imgs) {
			if(s.contains("logo")) continue;
			img += s+", ";
		}
		if(img.length() > 2)
			img = img.substring(0, img.length()-2);
		for(String s : invalidImgs) {
			img = img.replace(s, "");
		}
		
		tableNodes.clear();
		tableNodes = null;
		imgs.clear();
		imgs = null;
		domTree = null;
		return new ExtractResult(title, con, img);
	}

	/**
	 * get image links from node
	 * 
	 * @param url page url
	 * @param node
	 * @param charset page encoding
	 * @param imgs
	 *            image links
	 */
	private void getImages(String url, Node node, String charset,
			List<String> imgs) {
		if (node == null) {
			return;
		}
		String nodeName = node.getNodeName();
		if (nodeName.equalsIgnoreCase("IMG")) {
			String src = filterImage(node);
			if (src != null) {
				if (src.indexOf("http://") == -1)
					try {
						src = getAbsoluteURLStr(url, src, charset);
					} catch (Exception e) {
						return;
					}
				imgs.add(src);
			}
		}
		Node child = node.getFirstChild();
		while (child != null) {
			getImages(url, child, charset, imgs);
			child = child.getNextSibling();
		}
	}

	/**
	 * get absolute url
	 * 
	 * @param baseURLStr
	 *            page url
	 * @param relativeURLStr
	 *            link url
	 * @param charset
	 *            page charset
	 * @return absolute url string
	 * @throws URIException
	 */
	private String getAbsoluteURLStr(String baseURLStr, String relativeURLStr,
			String charset) throws Exception {
		URI baseURL = new URI(baseURLStr, true);
		if (relativeURLStr.startsWith("?")) {
			baseURL.setQueryString(relativeURLStr.substring(1));
			return baseURL.toString();
		}
		URI newURL = new URI(new URI(relativeURLStr, false), baseURLStr);
		return newURL.toString();
	}

	/**
	 * filter small images
	 * 
	 * @param node
	 * @return
	 */
	private String filterImage(Node node) {
		if (node == null) {
			return null;
		}
		if (!node.getNodeName().equalsIgnoreCase("IMG")) {
			return null;
		}
		NamedNodeMap nnm = node.getAttributes();
		if (nnm == null) {
			return null;
		}
		Node width = nnm.getNamedItem("width");
		Node height = nnm.getNamedItem("height");
		Node src = nnm.getNamedItem("src");
		if (src == null) {
			return null;
		}
		String imgUrl = src.getNodeValue();
		if (isImageSrc(imgUrl) == false) {
			return null;
		}
		if (width == null || height == null) {
			return imgUrl;
		}
		String strWidth = width.getNodeValue();
		String strHeight = height.getNodeValue();
		if (strWidth == null || strHeight == null || strWidth.length() == 0
				|| strHeight.length() == 0 || strWidth.indexOf("%") > 0
				|| strHeight.indexOf("%") > 0) {
			return imgUrl;
		}
		try {
			if (Integer.parseInt(strWidth.replaceAll("[pP][xX]", "")) < HtmlExtractor.imgWidth
					|| Integer.parseInt(strHeight.replaceAll("[pP][xX]", "")) < HtmlExtractor.imgHeight) {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return imgUrl;
	}

	//删除不必要标签
	private boolean isRemoveTag(String tag) {
		if (tag == null)
			return false;
		for (String remove_tag : REMOVE_TAGS) 
			if (tag.equalsIgnoreCase(remove_tag))
				return true;
		return false;
	}

	//
	private boolean isTableTag(String tag) {
		if (tag == null)
			return false;
		for (String remove_tag : TABLE_TAGS) 
			if (tag.equalsIgnoreCase(remove_tag))
				return true;
		return false;
	}

	private boolean isImageSrc(String srcUrl) {
		if (srcUrl == null || srcUrl.trim().length() < 4) {
			return false;
		}
		srcUrl = srcUrl.trim();
		int dotIndex = srcUrl.lastIndexOf(".");
		if (dotIndex < 0) {
			return false;
		}
		String imgType = srcUrl.substring(dotIndex + 1, srcUrl.length());
		for (String img : IMAGE_TYPE) {
			if (imgType.equalsIgnoreCase(img)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * get 'table' nodes from a root node
	 * @param node
	 * @param tableNodes
	 * @param totalUnlinkWords
	 */
	private void getTableNodes(Node node, List<WeightedTable> tableNodes,
			int totalUnlinkWords) {
		if (isTableTag(node.getNodeName())) {
			tableNodes.add(new WeightedTable(node, totalUnlinkWords));
		}
		Node child = node.getFirstChild();
		while (child != null) {
			getTableNodes(child, tableNodes, totalUnlinkWords);
			child = child.getNextSibling();
		}
	}

	/**
	 * remove tags we don't need
	 * @param node
	 */
	public void removeTags(Node node) {
		if (node == null) {
			return;
		}
		String nodeName = node.getNodeName();
		if (node.getNodeType() == Node.COMMENT_NODE || isRemoveTag(nodeName)) {
			node.getParentNode().removeChild(node);
			return;
		}
		Node child = node.getFirstChild();
		while (child != null) {
			Node next = child.getNextSibling();
			removeTags(child);
			child = next;
		}
	}

	private String getTitle(Node node) {
		String title = null;
		if (node == null)
			return title;
		if (node.getNodeName().equalsIgnoreCase("title")) {
			title = node.getTextContent();
			node.getParentNode().removeChild(node);
			return title;
		}
		Node child = node.getFirstChild();
		while (child != null) {
			Node next = child.getNextSibling();
			title = getTitle(child);
			if (title != null && title.length() > 0) {
				return title;
			}
			child = next;
		}
		return title;
	}

	/**
	 * remove all link text and some noisy text
	 * 
	 * @param node
	 */
	private void strictRemove(Node node) {
		if (node == null) {
			return;
		}
		String nodeName = node.getNodeName();
		if (nodeName != null && nodeName.equalsIgnoreCase("A")) {
			node.getParentNode().removeChild(node);
			return;
		}
		if (node.getNodeType() == Node.TEXT_NODE) {
			String text = node.getNodeValue().trim();
			Pattern p = Pattern
					.compile("[\\||版\\s*权\\s*所\\s*有|all\\s+rights\\s+reserved]");
			Matcher m = p.matcher(text);
			if (m.find()) {
				node.getParentNode().removeChild(node);
			}
			return;
		}
		Node child = node.getFirstChild();
		while (child != null) {
			Node next = child.getNextSibling();
			strictRemove(child);
			child = next;
		}
	}

	/**
	 * get the min weight table from a group of weighted table nodes
	 * @param tableNodes
	 * @return
	 */
	private WeightedTable minWeithtTable(List<WeightedTable> tableNodes) {
		if (tableNodes == null)
			return null;
		int size = tableNodes.size();
		if (size == 0) return null;
		
		int min = 0;
		for (int i = 1; i < tableNodes.size(); i++) {
			if (tableNodes.get(i).getWeight() < tableNodes.get(min).getWeight()) {
				min = i;
			}
		}
		if (tableNodes.get(min).getWeight() == 1) {
			return null;
		} else {
			return tableNodes.get(min);
		}
	}

	/**
	 * count unlinked words of a node
	 * @param node
	 * @return
	 */
	private int countUnLinkWords(Node node) {
		int count = 0;
		if (node == null) return count;
		String nodeName = node.getNodeName();
		if (nodeName == null || nodeName.equalsIgnoreCase("a")) {
			return count;
		}
		if (node.getNodeType() == Node.TEXT_NODE) {
			for (String str : INVALID_BEG) {
				if (node.getNodeValue().trim().indexOf(str) == 0) {
					return count;
				}
			}
			count += node.getNodeValue().trim().length();
		}
		Node child = node.getFirstChild();
		while (child != null) {
			count += countUnLinkWords(child);
			child = child.getNextSibling();
		}
		return count;
	}

	/**
	 * count lables of a node
	 * 
	 * @param node
	 * @return
	 */
	private int countLable(Node node) {
		int count = 0;
		if (node == null) {
			return count;
		}
		if (node.getNodeType() != Node.TEXT_NODE) {
			count++;
		}
		Node child = node.getFirstChild();
		while (child != null) {
			count += countLable(child);
			child = child.getNextSibling();
		}
		return count;
	}

	private void removeInputNode(Node node) {
		if (node == null)
			return;
		if (node.getNodeName().equalsIgnoreCase("input")) {
			node = removeParent(node);
			return;
		}
		Node child = node.getFirstChild();
		while (child != null) {
			removeInputNode(child);
			child = child.getNextSibling();
		}
	}

	private Node removeParent(Node node) {
		if (node == null) {
			return null;
		}
		String nodename = node.getNodeName();
		for (String table_tag : HtmlExtractor.TABLE_TAGS) {
			if (nodename.equalsIgnoreCase(table_tag)) {
				Node parent = node.getParentNode();
				if (parent == null) {
					return null;
				}
				parent.removeChild(node);
				return parent;
			}
		}
		return removeParent(node.getParentNode());
	}

	private Node getPurge(Node node) {
		if (node == null)
			return null;
		// if (!(node.getNodeName().equalsIgnoreCase("table") || node
		// .getNodeName().equalsIgnoreCase("div")))
		// return node;
		NodeList count = node.getChildNodes();
		// has target node
		if (node.getNodeName().equalsIgnoreCase("div")) {
			boolean Have = false;
			for (int i = 0; i < count.getLength(); i++) {
				String nodeName = count.item(i).getNodeName();
				for (String table_tag : HtmlExtractor.TABLE_TAGS) {
					if (nodeName.equalsIgnoreCase(table_tag)) {
						Have = true;
						break;
					}
				}
			}
			if (!Have)
				return node;
		} else if (!node.getNodeName().equalsIgnoreCase("table")) {
			return node;
		}
		ArrayList<ContentNode> contentList = new ArrayList<ContentNode>();
		for (int i = 0; i < count.getLength(); i++) {
			Node nodeTemp = count.item(i);
			double weight = 0;
			int nodeLength = getNodeLength(nodeTemp);
			if (nodeLength > 0) {
				weight = (double) getLinkword(nodeTemp) / (double) nodeLength;
			}

			contentList.add(new ContentNode(nodeTemp, nodeLength, weight));
		}
		Node result = null;
		int length = 0;
		for (int i = 0; i < contentList.size(); i++) {
			if (contentList.get(i).getLength() > length
					&& contentList.get(i).getWeight() < 0.5) {
				result = contentList.get(i).getNode();
				length = contentList.get(i).getLength();
			}
		}
		if (result != null && result.getNodeType() == Node.TEXT_NODE) {
			return node;
		} else {
			return result;
		}

	}

	private int getNodeLength(Node node) {
		int count = 0;
		if (node == null)
			return count;
		if (node.getNodeType() == Node.TEXT_NODE) {
			String content = node.getNodeValue();
			content = content.replace((char) 160, ' ');
			count += content.toString().trim().length();
		}
		Node child = node.getFirstChild();
		while (child != null) {
			count += getNodeLength(child);
			child = child.getNextSibling();
		}
		return count;
	}

	private int getLinkword(Node node) {
		int count = 0;
		if (node == null)
			return count;
		if (node.getNodeName().equalsIgnoreCase("a")) {
			count += node.getTextContent().length();
		}
		Node child = node.getFirstChild();
		while (child != null) {
			count += getLinkword(child);
			child = child.getNextSibling();
		}
		return count;
	}

	private void getPText(Node node, StringBuilder content) {
		Node parent = node.getParentNode();
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("p")) {
				content.append(child.getTextContent() + "\r\n");
			}
		}
	}

	class ContentNode {
		private Node node;
		private int length;
		private double weight;

		public ContentNode(Node node, int length, double weight) {
			this.node = node;
			this.length = length;
			this.weight = weight;
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public Node getNode() {
			return this.node;
		}

		public int getLength() {
			return this.length;
		}

		public double getWeight() {
			return this.weight;
		}
	}

	/**
	 * get text from a node
	 * 
	 * @param node
	 * @param sb
	 */
	private void getText(Node node, StringBuilder sb) {
		if (node == null) {
			return;
		}
		String nodeName = node.getNodeName();

		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null && nnm.getNamedItem("style") != null) {// 对于隐藏的内容不抽取
			if (nnm.getNamedItem("style").getTextContent().matches(
					"(?i).*?display\\s*:\\s*none.*|.*?size\\s*:\\s*0[^\\d]+.*")) {
				return ;
			}
		}
		if (node.getNodeType() == Node.TEXT_NODE) {
			String content = node.getNodeValue();
			content = content.replaceAll("\n", "").replaceAll("\t", "")
					.replace((char) 160, ' ').replaceAll("\\s\\s+", " ");
			sb.append(content);
		} else if (nodeName != null) {
			if (nodeName.equalsIgnoreCase("P")
					|| nodeName.equalsIgnoreCase("TABLE")) {
				sb.append("\n  ");
			}
			if (nodeName.equalsIgnoreCase("BR")
					|| nodeName.equalsIgnoreCase("UL")
					|| nodeName.equalsIgnoreCase("LI")
					|| nodeName.equalsIgnoreCase("TR")
					|| nodeName.equalsIgnoreCase("DL")
					|| nodeName.equalsIgnoreCase("DD")
					|| nodeName.equalsIgnoreCase("DT")) {
				sb.append("\n");
			} else if (nodeName.equalsIgnoreCase("TD")) {
				sb.append(" ");
			}
		}
		Node child = node.getFirstChild();
		while (child != null) {
			getText(child, sb);
			child = child.getNextSibling();
		}
	}

	/**
	 * Weighted Table
	 * @author Ahui Wang
	 */
	class WeightedTable {

		private Node tableNode;
		private double weight;

		public WeightedTable(Node tableNode, int totalUnlinkWords) {
			this.tableNode = tableNode;
			int unlinkWords = countUnLinkWords(tableNode);
			int lables = countLable(tableNode);//统计text节点的数量
			double ratios = (double) unlinkWords / (double) totalUnlinkWords;
			if (ratios < positiveRatios) {
				this.weight = 1;
			} else {
				this.weight = (double) lables / (double) unlinkWords;
			}
		}

		public Node getTableNode() {
			return tableNode;
		}
		public void setTableNode(Node tableNode) {
			this.tableNode = tableNode;
		}
		public double getWeight() {
			return weight;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		
	}

	public String getRemoveTags() {
		return removeTags;
	}

	public void setRemoveTags(String removeTags) {
		HtmlExtractor.removeTags = removeTags;
	}

	public String getTableTags() {
		return tableTags;
	}

	public void setTableTags(String tableTags) {
		HtmlExtractor.tableTags = tableTags;
	}
	public boolean getDownloadImg() {
		return downloadImg;
	}
	public void setDownloadImg(boolean downloadImg) {
		HtmlExtractor.downloadImg = downloadImg;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		HtmlExtractor.imageType = imageType;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		HtmlExtractor.imgHeight = imgHeight;
	}
	public int getImgWidth() {
		return imgWidth;
	}
	public void setImgWidth(int imgWidth) {
		HtmlExtractor.imgWidth = imgWidth;
	}
	public double getPositiveRatios() {
		return positiveRatios;
	}
	public void setPositiveRatios(double positiveRatios) {
		HtmlExtractor.positiveRatios = positiveRatios;
	}
	public int getImgSize() {
		return imgSize;
	}
	public void setImgSize(int imgSize) {
		HtmlExtractor.imgSize = imgSize;
	}
	public String getInvalidBeg() {
		return invalidBeg;
	}
	public void setInvalidBeg(String invalidBeg) {
		HtmlExtractor.invalidBeg = invalidBeg;
	}

	public HtmlExtractor() {
	}
	public HtmlExtractor(String confFile) {
		Properties properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(confFile);
			properties.load(in);
			in.close();
		} catch (IOException e) {
			System.out.println(e);
			return;
		}
		REMOVE_TAGS = properties.getProperty("remove_tags").trim().split("\\s");
		TABLE_TAGS = properties.getProperty("table_tags").trim().split("\\s");
		IMAGE_TYPE = properties.getProperty("image_type").trim().split("\\s");
		imgHeight = Integer.parseInt(properties
				.getProperty("img_min_height"));
		imgWidth = Integer.parseInt(properties
				.getProperty("img_min_width"));
		imgSize = Integer.parseInt(properties
				.getProperty("img_local_size"));
		positiveRatios = Double.parseDouble(properties
				.getProperty("table_positive_ratios"));
		downloadImg = Boolean.parseBoolean(properties.getProperty("download_img"));

		try {
			in = new FileInputStream("config/invalid_beg");
			InputStreamReader isr = new InputStreamReader(in, "utf8");
			BufferedReader bf = new BufferedReader(isr);
			String temp = "";
			String str;
			while ((str = bf.readLine()) != null) {
				temp += str + " ";
			}
			INVALID_BEG = temp.trim().split("\\s");
			in.close();
		} catch (IOException e) {
			System.out.println(e);
			return;
		}
	}
	
}