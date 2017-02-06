package com.esrichina.android.ext.drawtool;

import java.util.EventListener;

/**
 * 
 * @author ropp gispace@yeah.net
 *
 *	定义画图事件监听接口
 */
public interface DrawEventListener extends EventListener {

	void handleDrawEvent(DrawEvent event);
}
