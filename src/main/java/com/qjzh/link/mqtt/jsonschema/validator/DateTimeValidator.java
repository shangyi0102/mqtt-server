package com.qjzh.link.mqtt.jsonschema.validator;


import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Optional;

import org.everit.json.schema.FormatValidator;

/**
 * @DESC: 自定义日期时间格式化器
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午3:06:50
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class DateTimeValidator implements FormatValidator {

	final static DateTimeFormatter SECONDS_FRACTION_FORMATTER = new DateTimeFormatterBuilder()
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true).toFormatter();
	
	static final String ZONE_OFFSET_PATTERN = "XXX";
	
	private static final String PARTIAL_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String FORMATS_ACCEPTED = Arrays.asList(
			PARTIAL_DATETIME_PATTERN).toString();
	
	
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(PARTIAL_DATETIME_PATTERN)
            //.appendOptional(SECONDS_FRACTION_FORMATTER)
            //.appendPattern(ZONE_OFFSET_PATTERN)
            .toFormatter();
	
    @Override 
    public Optional<String> validate(String subject) {
    	try {
    		FORMATTER.parse(subject);
            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.of(String.format("[%s] is not a valid %s. Expected %s", subject, formatName(), FORMATS_ACCEPTED));
        }
    }
    
    @Override
    public String formatName() {
        return "datetime-c";
    }

}