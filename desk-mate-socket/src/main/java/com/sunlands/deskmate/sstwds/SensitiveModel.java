package com.sunlands.deskmate.sstwds;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Date;

public class SensitiveModel implements java.io.Serializable{

	private static final long serialVersionUID = -8460169049850996790L;

    private Integer id;

    private String type;

    private String keyword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFilterWord() {
        return keyword;
    }

    public void setFilterWord(String keyword) {
        this.keyword = keyword;
    }


    @Override
	public String toString() {
		try {
			return ToStringBuilder.reflectionToString(this,
					ToStringStyle.MULTI_LINE_STYLE);
		} catch (Exception e) {
			// NOTICE: 这样做的目的是避免由于toString()的异常导致系统异常终止
			// 大部分情况下，toString()用在日志输出等调试场景
			return StringUtils.EMPTY;
		}
	}

    private String replaceWord;
    private int level;
    private Date start;
    private Date end;


    public String getReplaceWord() {
        return replaceWord;
    }

    public void setReplaceWord(String replaceWord) {
        this.replaceWord = replaceWord;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
