package com.pullo.batch.config

import com.pullo.batch.model.PoetryDTO
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.ResultSet
import javax.sql.DataSource


/**
 * @author by liufucheng on 2020/9/2
 */
@EnableBatchProcessing
@Configuration
open class DbToDbConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource
) {
    @Bean
    open fun db2DbItemReader(originDatasource: DataSource): JdbcCursorItemReader<PoetryDTO> {
        return JdbcCursorItemReaderBuilder<PoetryDTO>()
            .dataSource(dataSource)
            .name("story_db_2_db")
            .sql("SELECT * FROM poetry ORDER BY id")
            .rowMapper { rs: ResultSet, _: Int ->
                PoetryDTO().apply {
                    author = rs.getString("author")
                    title = rs.getString("title")
                    text = rs.getString("text")
                    uuid = rs.getString("uuid")
                }
            }
            .build()
    }

    @Bean
    open fun db2DbWriter(targetDatasource: DataSource): ItemWriter<PoetryDTO> {
        val insertSql = "INSERT INTO poetry2 (uuid,author,title,text) VALUES (:uuid, :author, :title, :text)"
        return JdbcBatchItemWriterBuilder<PoetryDTO>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider<PoetryDTO>())
            .sql(insertSql)
            .dataSource(targetDatasource)
            .build()
    }

    @Bean
    open fun db2DbJob(db2DbStep: Step, db2DbListener: JobExecutionListener): Job {
        val funcName = Thread.currentThread().stackTrace[1].methodName
        return jobBuilderFactory[funcName]
            .listener(db2DbListener)
            .flow(db2DbStep)
            .end().build()
    }

    @Bean
    open fun db2DbStep(
        db2DbItemReader: ItemReader<PoetryDTO>,
        poetryProcessor: ItemProcessor<PoetryDTO, PoetryDTO>,
        db2DbWriter: ItemWriter<PoetryDTO>
    ): Step? {
        val funcName = Thread.currentThread().stackTrace[1].methodName
        return stepBuilderFactory[funcName]
            .chunk<PoetryDTO, PoetryDTO>(10)
            .reader(db2DbItemReader)
            .processor(poetryProcessor)
            .writer(db2DbWriter)
            .build()
    }
}
