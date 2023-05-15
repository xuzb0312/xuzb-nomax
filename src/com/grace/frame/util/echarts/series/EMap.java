package com.grace.frame.util.echarts.series;

/**
 * 和Map完全相同，只是为了避免和java.util.Map重名
 *
 * @author yjc
 */
public class EMap extends Map {
    /**
	 * 
	 */
	private static final long serialVersionUID = -463755094426290071L;

	public EMap() {
    }

    public EMap(String name) {
        super(name);
    }
}
