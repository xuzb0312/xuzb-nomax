package com.grace.frame.util.echarts.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grace.frame.util.echarts.axis.Axis;
import com.grace.frame.util.echarts.axis.CategoryAxis;
import com.grace.frame.util.echarts.axis.TimeAxis;
import com.grace.frame.util.echarts.axis.ValueAxis;
import com.grace.frame.util.echarts.code.AxisType;

/**
 * @author yjc
 */
@SuppressWarnings("unchecked")
public class AxisDeserializer implements JsonDeserializer<Axis>{
	/**
	 * 设置json,typeOfT,context值
	 * 
	 * @param json
	 * @param typeOfT
	 * @param context
	 */
	public Axis deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		final JsonObject jsonObject = json.getAsJsonObject();
		String _type = jsonObject.get("type").getAsString();
		AxisType type = AxisType.valueOf(_type);
		Axis axis = null;
		switch (type) {
			case category:
				axis = context.deserialize(jsonObject, CategoryAxis.class);
				break;
			case value:
				axis = context.deserialize(jsonObject, ValueAxis.class);
				break;
			case time:
				axis = context.deserialize(jsonObject, TimeAxis.class);
				break;
		}
		return axis;
	}
}
