package com.pullo.batch.config;

import com.pullo.batch.listener.JobCompletionNotificationListener;
import com.pullo.batch.mapper.WrapperJsonLineMapper;
import com.pullo.batch.model.PoetryDTO;
import com.pullo.batch.processor.PoetryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author liufucheng
 */
@EnableBatchProcessing
@Configuration
public class JsonFileToDatabaseConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public DataSource dataSource;
    // begin reader, writer, and processor

    @Bean
    public FlatFileItemReader<PoetryDTO> csvAnimeReader() {
        FlatFileItemReader<PoetryDTO> reader = new FlatFileItemReader<>();
        FileInputStream in = null;
        String jsonFilePath = "/Users/liufucheng/work/study/chinese-poetry/json/poet.song.0.json";
        try {
            in = new FileInputStream(new File(jsonFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reader.setResource(new InputStreamResource(in));
        reader.setLineMapper(new WrapperJsonLineMapper(new JsonLineMapper()));
        return reader;
    }


    @Bean
    ItemProcessor<PoetryDTO, PoetryDTO> csvAnimeProcessor() {
        return new PoetryProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<PoetryDTO> csvAnimeWriter() {
        JdbcBatchItemWriter<PoetryDTO> csvAnimeWriter = new JdbcBatchItemWriter<>();
        csvAnimeWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        csvAnimeWriter.setSql("insert into poetry(author,title,text)values (:author, :title, :text)");
        csvAnimeWriter.setDataSource(dataSource);
        return csvAnimeWriter;
    }

    // end reader, writer, and processor
    // begin job info
    @Bean
    public Step csvFileToDatabaseStep() {
        return stepBuilderFactory.get("csvFileToDatabaseStep")
                .<PoetryDTO, PoetryDTO>chunk(1)
                .reader(csvAnimeReader())
                .processor(csvAnimeProcessor())
                .writer(csvAnimeWriter())
                .build();
    }

    @Bean
    Job csvFileToDatabaseJob(JobCompletionNotificationListener jobCompletionNotificationListener) {
        return jobBuilderFactory.get("csvFileToDatabaseJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionNotificationListener)
                .flow(csvFileToDatabaseStep())
                .end()
                .build();
    }
    // end job info
}
