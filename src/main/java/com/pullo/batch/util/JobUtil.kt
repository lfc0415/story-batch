package com.pullo.batch.util

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder

/**
 * @author by liufucheng on 2020/9/2
 */
object JobUtil {
    /**
     * 以当前时间作为参数，构建JobParameters
     */
    fun makeJobParameters(): JobParameters {
        return JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()
    }
}
