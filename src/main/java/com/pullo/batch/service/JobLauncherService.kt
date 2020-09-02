package com.pullo.batch.service

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import java.util.HashMap
import java.util.function.Consumer

@Service
class JobLauncherService {
    @Autowired
    private val jobLauncher: JobLauncher? = null

    /**
     * 启动spring batch任务
     */
    @Throws(
        JobParametersInvalidException::class,
        JobExecutionAlreadyRunningException::class,
        JobRestartException::class,
        JobInstanceAlreadyCompleteException::class
    )
    fun startJob(
        job: Job,
        jobParameters: JobParameters?
    ): Map<String, Any> {
        val resultMap: MutableMap<String, Any> =
            HashMap()
        //计时
        val stopWatch = StopWatch()
        stopWatch.start(job.name)
        //执行任务
        val run = jobLauncher!!.run(job, jobParameters)
        //返回结果
        val stringBuffer = StringBuffer()
        val stepExecutions =
            run.stepExecutions
        stepExecutions.forEach(Consumer { stepExecution: StepExecution ->
            stringBuffer.append("readCount:").append(stepExecution.commitCount)
            stringBuffer.append("filterCount:").append(stepExecution.filterCount)
            stringBuffer.append("commitCount:").append(stepExecution.commitCount)
            stringBuffer.append("writeCount:").append(stepExecution.writeCount)
        })
        stopWatch.stop()
        val exitStatus = run.exitStatus
        val returnStr = (System.lineSeparator() + "resultCount: " + stringBuffer.toString()
                + System.lineSeparator() + "exitStatus: " + exitStatus
                + System.lineSeparator() + "timeInfo: " + stopWatch.prettyPrint())
        resultMap["result"] = returnStr
        resultMap["stauts"] = exitStatus
        return resultMap
    }
}
