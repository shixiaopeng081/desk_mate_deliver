package com.sunlands.deskmate.util;

import com.p6spy.engine.spy.appender.MultiLineFormat;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

/**
 * @author liude
 * pretty format working form p6spy
 */
public class PrettySqlMultiLineFormat extends MultiLineFormat {
    private static final Formatter FORMATTER = new BasicFormatterImpl();

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
        return super.formatMessage(connectionId, now, elapsed, category, "", FORMATTER.format(sql));
    }
}