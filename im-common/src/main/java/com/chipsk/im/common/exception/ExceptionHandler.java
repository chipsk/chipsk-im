package com.chipsk.im.common.exception;

import com.chipsk.im.common.pojo.Task;

import java.io.Serializable;

public interface ExceptionHandler extends Serializable {
    void handle(Task task, Throwable e);
}
