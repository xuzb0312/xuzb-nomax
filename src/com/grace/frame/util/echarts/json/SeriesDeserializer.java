package com.grace.frame.util.echarts.json;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grace.frame.util.echarts.code.SeriesType;
import com.grace.frame.util.echarts.series.Bar;
import com.grace.frame.util.echarts.series.Boxplot;
import com.grace.frame.util.echarts.series.Candlestick;
import com.grace.frame.util.echarts.series.EffectScatter;
import com.grace.frame.util.echarts.series.Funnel;
import com.grace.frame.util.echarts.series.Gauge;
import com.grace.frame.util.echarts.series.Graph;
import com.grace.frame.util.echarts.series.Line;
import com.grace.frame.util.echarts.series.Lines;
import com.grace.frame.util.echarts.series.Parallel;
import com.grace.frame.util.echarts.series.Pie;
import com.grace.frame.util.echarts.series.Sankey;
import com.grace.frame.util.echarts.series.Scatter;
import com.grace.frame.util.echarts.series.Series;

/**
 * @author yjc
 */
@SuppressWarnings("unchecked")
public class SeriesDeserializer implements JsonDeserializer<Series>{
	/**
	 * 设置json,typeOfT,context值
	 * 
	 * @param json
	 * @param typeOfT
	 * @param context
	 */
	public Series deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		final JsonObject jsonObject = json.getAsJsonObject();
		String _type = jsonObject.get("type").getAsString();
		SeriesType type = SeriesType.valueOf(_type);
		Series series = null;
		switch (type) {
			case line:
				series = context.deserialize(jsonObject, Line.class);
				break;
			case bar:
				series = context.deserialize(jsonObject, Bar.class);
				break;
			case scatter:
				series = context.deserialize(jsonObject, Scatter.class);
				break;
			case funnel:
				series = context.deserialize(jsonObject, Funnel.class);
				break;
			case pie:
				series = context.deserialize(jsonObject, Pie.class);
				break;
			case gauge:
				series = context.deserialize(jsonObject, Gauge.class);
				break;
			case map:
				series = context.deserialize(jsonObject, Map.class);
				break;
			case lines:
				series = context.deserialize(jsonObject, Lines.class);
				break;
			case effectScatter:
				series = context.deserialize(jsonObject, EffectScatter.class);
				break;
			case candlestick:
				series = context.deserialize(jsonObject, Candlestick.class);
				break;
			case graph:
				series = context.deserialize(jsonObject, Graph.class);
				break;
			case boxplot:
				series = context.deserialize(jsonObject, Boxplot.class);
				break;
			case parallel:
				series = context.deserialize(jsonObject, Parallel.class);
				break;
			case sankey:
				series = context.deserialize(jsonObject, Sankey.class);
				break;
		}
		return series;
	}
}
