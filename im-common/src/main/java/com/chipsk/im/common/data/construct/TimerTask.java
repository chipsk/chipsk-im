package com.chipsk.im.common.data.construct;

import java.io.Serializable;

abstract public class TimerTask implements Serializable {

    private static final long serialVersionUID = -8639671839184198860L;

    public abstract void run();
}
