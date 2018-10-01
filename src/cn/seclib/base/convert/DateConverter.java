package cn.seclib.base.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date>{

	@Override
	public Date convert(String source) {
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf1.parse(source);
		}catch(ParseException e){
			try{
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				return sdf2.parse(source);
			}catch(ParseException ex){
				//写入日志
			}
		}
		return null;
	}

}
