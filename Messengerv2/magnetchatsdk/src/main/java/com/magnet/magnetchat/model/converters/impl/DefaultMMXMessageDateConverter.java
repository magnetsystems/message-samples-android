package com.magnet.magnetchat.model.converters.impl;

import com.magnet.magnetchat.ChatSDK;
import com.magnet.magnetchat.Constants;
import com.magnet.magnetchat.model.converters.BaseConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aorehov on 20.05.16.
 */
public class DefaultMMXMessageDateConverter extends BaseConverter<Date, String> {

    private final SimpleDateFormat FORMATTER;

    public DefaultMMXMessageDateConverter() {
        String patter = ChatSDK.getMMXBeanFactory().messageDateFormat();
        if (patter == null) patter = Constants.MMX_DATE_FORMAT;
        FORMATTER = new SimpleDateFormat(patter);
    }

    @Override
    public String convert(Date date) {
        return FORMATTER.format(date);
    }

}
