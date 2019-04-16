package com.sunlands.deskmate.vo.response;

import java.io.Serializable;
import java.util.List;


/**
 * <p></p>
 * <p>
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author zl
 * @version 1.0
 * @Date Created in 2017年10月18日 20:36
 * @since 1.0
 */
public class PageResultVO<T> implements Serializable {

    private static final long serialVersionUID = -6293650025117429528L;

    /**
     * 默认构造方法
     */
    public PageResultVO() {

    }

    /**
     * 分页查询结果构造器
     *
     * @param total 总记录数
     * @param rows  每页记录
     */
    public PageResultVO(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    /**
     * 总记录数
     */
    private long total;

    /**
     * 每页数据记录
     */
    private List<T> rows;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
