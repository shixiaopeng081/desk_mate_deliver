package com.sunlands.deskmate.sstwds.impl.sstwdsInit;

import com.sunlands.deskmate.entity.InputKeywords;
import com.sunlands.deskmate.entity.InputKeywordsExample;
import com.sunlands.deskmate.mapper.InputKeywordsMapper;
import com.sunlands.deskmate.sstwds.SensitiveModel;
import com.sunlands.deskmate.sstwds.SensitiveWordsReader;
import com.sunlands.deskmate.sstwds.SensitiveWordsWriter;
import com.sunlands.deskmate.utils.DozerMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * SensitiveWordServiceImpl
 * 
 * @author mbn
 * @version mbn3.0
 */
@Component
public class SensitiveWordServiceImpl implements SensitiveWordsReader,SensitiveWordsWriter {

    @Autowired
    private InputKeywordsMapper inputKeywordsMapper;

	private final String LOW_LEVEL = "low";
	private final String HIGH_LEVEL = "high";
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<SensitiveModel> read() {
		List<SensitiveModel> list = new ArrayList<SensitiveModel>();
		List<Integer> levels = new ArrayList<Integer>();
		if(StringUtils.equals(type, LOW_LEVEL)){
			levels.add(2);
		} else if(StringUtils.equals(type, HIGH_LEVEL)){
			levels.add(0);
			levels.add(1);
			levels.add(3);
		} else {// 全部都取出
			levels.add(-1);
		}
        List<InputKeywords> alInputKeywords = sensitiveWordReadHelper("filter");
        if(alInputKeywords != null && alInputKeywords.size() > 0){
            DozerMapper.copyPropertiesList(alInputKeywords,list,SensitiveModel.class);
        }
		return list;
	}

    // 敏感词重新刷新，由数据库主从同步引起的数据不一致问题，可能会导致敏感词修改后不生效
    // 先使用主库查询，敏感词可以从树中删除时，就可以取消此方法
    // 因为敏感词只在系统启动时全加载一次，其他变化时是增量更改
    @SuppressWarnings("unchecked")
    private List<InputKeywords> sensitiveWordReadHelper(String type) {

        InputKeywordsExample example = new InputKeywordsExample();
        InputKeywordsExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(type);
        List<InputKeywords> alInputKeywords = inputKeywordsMapper.selectByExample(example);
        return alInputKeywords;

    }

    @Override
    public void writeWord(SensitiveModel oneWord) {
        // dummy
    }


    private Date getDefaultValidStart(){
//		Calendar cal =Calendar.getInstance();
//		cal.set(1970, 0, 1, 0, 0, 0);
        return new Date();
    }

    private Date getDefaultValidEnd(){
        Calendar cal =Calendar.getInstance();
        cal.set(2099, 11, 31, 0, 0, 0);
        return cal.getTime();
    }
}