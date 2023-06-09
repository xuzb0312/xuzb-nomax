
package com.grace.frame.util.echarts.data;

import java.io.Serializable;

/**
 * KData
 *
 * @author yjc
 */
public class KData implements Serializable {

    private static final long serialVersionUID = 3593085721806381731L;

    private Double[] value;

    /**
     * 开盘，收盘，最低，最高
     *
     * @param open
     * @param close
     * @param min
     * @param max
     */
    public KData(Double open, Double close, Double min, Double max) {
        this.value = new Double[4];
        this.value[0] = open;
        this.value[1] = close;
        this.value[2] = min;
        this.value[3] = max;
    }

    /**
     * 获取value值
     */
    public Double[] value() {
        return this.value;
    }

    /**
     * 设置value值
     *
     * @param value
     */
    public KData value(Double[] value) {
        this.value = value;
        return this;
    }

    /**
     * 获取value值
     */
    public Double[] getValue() {
        return value;
    }

    /**
     * 设置value值
     *
     * @param value
     */
    public void setValue(Double[] value) {
        this.value = value;
    }
}
