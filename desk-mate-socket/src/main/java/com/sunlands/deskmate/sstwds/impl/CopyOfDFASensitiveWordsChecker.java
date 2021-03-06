package com.sunlands.deskmate.sstwds.impl;

import com.sunlands.deskmate.sstwds.SensitiveModel;
import com.sunlands.deskmate.sstwds.SensitiveWordsCheck;
import com.sunlands.deskmate.sstwds.SensitiveWordsReader;
import com.sunlands.deskmate.sstwds.SensitiveWordsWriter;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DFA实现的敏感词过滤
 * 
 * @author sunyx
 * 
 */
public class CopyOfDFASensitiveWordsChecker implements SensitiveWordsCheck {

	private char[] IGNORE_WORD = new char[] { '@', '#', '$', '!', '%', '^',
			'&', '~', ' ' };
	private Node rootNode = new Node('R');
	
	// 缓存，业务需求必须加入。需要考虑占内存大小
	private Map<String, SensitiveModel> cache = new HashMap<String, SensitiveModel>();
	
	private SensitiveWordsReader reader;

	private SensitiveWordsWriter writer;

	private boolean useIgnoreWord = false;
	
	// register type
	private String type;
	
	public void init() {
		Assert.notNull(reader,
                "reader must not be null if you want to use this class. check it!");
		createTree();
		// 注册到工具类
		DFAcheckUtil.register(type, this);
	}

	@Override
	public void reloadWord() {
		// clear node tree
		rootNode = new Node('R');
		createTree();
	}
	
	@Override
	public void addSenstiveWord(SensitiveModel word) {
		Assert.notNull(writer,
				"writer must not be null if you want to use this method. check it!");
		writer.writeWord(word);
		char[] chars = word.getFilterWord().toCharArray();
		if (chars.length > 0)
			insertNode(rootNode, chars, 0);
		cache.put(word.getFilterWord(), word);
	}

//	@Override   此方法需要保证过滤词必须和替换词长度一致，如 '上访' 替换成 '??', '公安局' 替换成 '???'
//	public String replaceSenstiveWords(String content, String replaceWord) {
//		int index = 0;
//		while (true) {
//			StringBuilder sb = new StringBuilder();
//			int[] result = checkHelper(content, index);
//			if (result[0] >= 0) {
//				sb.append(content.substring(0, result[0]));
//				for (int i = 0; i < result[1]; i++) {
//					sb.append(replaceWord);
//				}
//				index = result[0] + result[1];
//				if (index < content.length()) {
//					sb.append(content.substring(index, content.length()));
//				}
//				content = sb.toString();
//			} else {
//				break;
//			}
//		}
//		return content;
//	}
	
	@Override
	public String replaceSenstiveWords(String content, String replaceWord) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			int index = 0;
			int[] result = checkHelper(content, index);
			if (result[0] >= 0) {
				sb.append(content.substring(0, result[0]));
				String sensitiveWord = content.substring(result[0], result[0] + result[1]);
				String rpcwd = formatReplaceWord(cache.get(sensitiveWord), result[0], result[1], replaceWord);
//				String rpcwd = formatReplaceWord(sensitiveWord);
				sb.append(rpcwd);
				index = result[0] + result[1];
				if (index < content.length()) {
					content = content.substring(index);
				} else {
					break;
				}
			} else {
				sb.append(content);
				break;
			}
		}
		return sb.toString();
	}
	
	@Override
	public boolean hasSenstiveWords(String content) {
		return checkHelper(content, 0)[0] >= 0;
	}

	@Override
	public String findSenstiveWords(String content) {
		int[] result = checkHelper(content, 0);
		if(result[0]<0){
			return null;
		}else{
			return content.substring(result[0], result[0]+result[1]);
		}
	}

	private void createTree() {
		List<SensitiveModel> sensitives = reader.read();
		for (SensitiveModel model : sensitives) {
			String str = model.getFilterWord();
			char[] chars = str.toCharArray();
			if (chars.length > 0)
				insertNode(rootNode, chars, 0);
			cache.put(model.getFilterWord(), model);
		}
	}
	@Override
	public void reloadSenstiveWords() {
		this.createTree();
	}

	private void insertNode(Node node, char[] cs, int index) {
		Node n = findNode(node, cs[index]);
		if (n == null) {
			n = new Node(cs[index]);
			node.nodes.add(n);
		}

		if (index == (cs.length - 1))
			n.flag = 1;

		index++;
		if (index < cs.length)
			insertNode(n, cs, index);
	}

	private Node findNode(Node node, char c) {
		List<Node> nodes = node.nodes;
		Node rn = null;
		for (Node n : nodes) {
			if (n.c == c) {
				rn = n;
				break;
			}
		}
		return rn;
	}

	// 返回数组中第一个表示在content中的位置，第二个表示匹配的长度
	private int[] checkHelper(String content, int index) {
		char[] chars = content.toCharArray();
		Node node = rootNode;
		int offset = 0;
		while (index < chars.length) {
			if (!isIgnoreChar(chars[index])) {
				node = findNode(node, chars[index]);
				if (node == null) {// 只有部分匹配，不算
					node = rootNode;
					index = index - offset + 1;// 从上次匹配到的第一个字的下一个位置继续
					offset = 0;
					continue;
				} else if (node.flag == 1) {
					return new int[] { index - offset, ++offset };
				}
			}
			// 匹配上一部分了，查找位置a的下一个位置，同时偏移量offset加1
			offset++;
			index++;
		}
		return new int[] { -1, 0 };
	}

	private boolean isIgnoreChar(char c) {
		return useIgnoreWord && ArrayUtils.contains(IGNORE_WORD, c);
	}

	private static class Node {
		public char c;
		public int flag; // 1：表示终结，0：延续
		public List<Node> nodes = new ArrayList<Node>();

		public Node(char c) {
			this.c = c;
			this.flag = 0;
		}
	}

	public void setReader(SensitiveWordsReader reader) {
		this.reader = reader;
	}

	public void setWriter(SensitiveWordsWriter writer) {
		this.writer = writer;
	}

	public void setUseIgnoreWord(boolean useIgnoreWord) {
		this.useIgnoreWord = useIgnoreWord;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected String formatReplaceWord(SensitiveModel sensitiveWord, int index, int length, String replaceWord){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(replaceWord);
		}
		return sb.toString();
	}
	protected String formatReplaceWord( String replaceWord){
		StringBuffer sb = new StringBuffer();
		sb.append("<red>").append(replaceWord).append("</red>");
		return sb.toString();
	}
	public static void main(String[] args) {
		CopyOfDFASensitiveWordsChecker checker = new CopyOfDFASensitiveWordsChecker();
		checker.setReader(new SensitiveWordsReader() {
			@Override
			public List<SensitiveModel> read() {
				List<SensitiveModel> list = new ArrayList<SensitiveModel>();
				SensitiveModel m = new SensitiveModel();
				m.setFilterWord("abcd");
				list.add(m);
				SensitiveModel m2 = new SensitiveModel();
				m2.setFilterWord("abc");
				list.add(m2);
				return list;
			}
		});
		checker.init();
		String a = checker.replaceSenstiveWords("12abcd12", "*");
		System.out.println(a);
	}
	
}
