package com.sunlands.deskmate.sstwds.impl;

import com.sunlands.deskmate.sstwds.SensitiveModel;
import com.sunlands.deskmate.sstwds.SensitiveWordsReader;
import com.sunlands.deskmate.sstwds.SensitiveWordsWriter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件形式实现的敏感词的读写持久化
 * 
 * @author sunyx
 * 
 */
public class SensitiveWordFileReaderAndWriter implements SensitiveWordsReader,
		SensitiveWordsWriter {
	protected final Log logger = LogFactory.getLog(this.getClass());

	private String path;
	
	private File f;

	private List<SensitiveModel> sensitives = Collections
			.synchronizedList(new ArrayList<SensitiveModel>());

	public void init() {
		Assert.notNull(path,
				"file path for save or read senstive words cann't be null. check it!");
		try {
			initFile();
			if(f == null || !f.exists()){
				logger.error("file is not exist. path=" + path);
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
				String str = null;
				while ((str = br.readLine()) != null) {
					if(StringUtils.isNotBlank(str)){
						SensitiveModel model = new SensitiveModel();
						model.setFilterWord(str);
						sensitives.add(model);	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("load sensitive word has error=" + e, e);
		}
	}

	private void saveFile(String line) {
		try {
			if(f != null && f.exists()){
				RandomAccessFile randomFile = new RandomAccessFile(f, "rw");
				long fileLength = randomFile.length();
				randomFile.seek(fileLength);
//				randomFile.writeBytes(new String(line.getBytes(), "UTF-8") + System.getProperty("line.separator"));
//				randomFile.writeUTF(line);
//				randomFile.writeBytes(System.getProperty("line.separator"));
				randomFile.write(line.getBytes());
				randomFile.write(System.getProperty("line.separator").getBytes());
				randomFile.close();
			} else {
				logger.error("file is not exist, can not to write. path=" + path);
			}
		} catch (Exception e) {
			logger.error("load sensitive word has error=" + e, e);
		}
	}

	@Override
	public void writeWord(SensitiveModel oneWord) {
		if (!sensitives.contains(oneWord)) {
			sensitives.add(oneWord);
			saveFile(oneWord.getFilterWord());
		}
	}

	@Override
	public List<SensitiveModel> read() {
		return sensitives;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	private String getRealPath(){
		return this.getClass().getClassLoader().getResource(path).getPath();
	}

	private void initFile(){
		f = new File(path);
		if(!f.exists()){
			f = new File(getRealPath());
		}
	}
	
	public static void main(String[] args) {
		SensitiveWordFileReaderAndWriter instance = new SensitiveWordFileReaderAndWriter();
		instance.setPath("src/test/resources/words.txt");
		instance.init();
		for (int i = 0; i < 10; i++) {
			SensitiveModel model = new SensitiveModel();
			model.setFilterWord("shibada" + i);
			instance.writeWord(model);
		}
	}
}
