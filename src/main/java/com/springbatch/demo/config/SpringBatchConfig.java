package com.springbatch.demo.config;

import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbatch.demo.entity.Student;
import com.springbatch.demo.repository.StudentRepository;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private StudentRepository customerRepository;


//    @StepScope
//    @Bean
//    JsonItemReader<Student> jsonItemReader(@Value("#{jobParameters['students']}") String jsonFilePath) {
//    	System.out.println("[+] File Path: "+ jsonFilePath);
//        return new JsonItemReaderBuilder<Student>()
//                .jsonObjectReader(new JacksonJsonObjectReader<>(Student.class))
//                .resource(new FileSystemResource(jsonFilePath))
//                .name("studentJsonItemReader")
//                .build();
//    }
    
    @StepScope
    @Bean
    ListItemReader<Student> listItemReader(@Value("#{jobParameters['students']}") String jsonFilePath)
    {
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {
			List<Student> students = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Student>>() {});
			return new ListItemReader<>(students);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ListItemReader<Student>(new ArrayList<>());
    }

    @Bean
    StudentProcessor processor() {
        return new StudentProcessor();
    }

    @Bean
    RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    Step step1() {
        return stepBuilderFactory.get("import-step").<Student, Student>chunk(10)
                .reader(listItemReader(null))
                .processor(processor())
                .writer(writer())
              //  .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    Job runJob() {
        return jobBuilderFactory.get("importCustomers")
                .flow(step1()).end().build();

    }


    @Bean
    TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}
