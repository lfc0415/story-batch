package com.pullo.batch

import com.pullo.batch.model.PoetryDTO
import com.pullo.batch.service.JobLauncherService
import com.pullo.batch.util.JobUtil
import com.pullo.batch.util.JsonConverter
import org.junit.jupiter.api.Test
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.beans.factory.annotation.Autowired


/**
 * @author by liufucheng on 2020/9/2
 */
class StoryDbTest : StoryApplicationTests() {
    @Autowired
    lateinit var storyDbReader: JdbcCursorItemReader<PoetryDTO>
    @Autowired
    lateinit var jobLauncherService: JobLauncherService
    @Autowired
    lateinit var db2DbJob: Job

    @Test
    fun `test story db reader`() {
        val executionContext = ExecutionContext()
        storyDbReader.open(executionContext)
        println(JsonConverter.serialize(storyDbReader.read()))
        //{"author":"陳嘉言","title":"晦日宴高氏林亭","text":"[\"公子申敬愛，攜朋翫物華。\",\"人是平陽客，地即石崇家。\",\"水文生舊浦，風色滿新花。\",\"日暮連歸騎，長川照晚霞。\"]"}
    }

    @Test
    @Throws(
        JobParametersInvalidException::class,
        JobExecutionAlreadyRunningException::class,
        JobRestartException::class,
        JobInstanceAlreadyCompleteException::class
    )
    fun testDb2DbJob() { //构建job参数
        val jobParameters: JobParameters = JobUtil.makeJobParameters()
        //运行job
        val stringObjectMap: Map<String, Any> =
            jobLauncherService.startJob(db2DbJob, jobParameters)
        println(JsonConverter.serialize(stringObjectMap))
    }
}
