package com.springbatch.demo.controller;

import java.io.File;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbatch.demo.entity.Student;

@RestController
public class JobController {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job;

	@PostMapping("/addStudents")
	public ResponseEntity<Void> insertAllStudent(@RequestBody List<Student> students) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			File tempFile = File.createTempFile("students", ".json");
			objectMapper.writeValue(tempFile, students);
			JobParameters parameters = new JobParametersBuilder().addString("students", tempFile.getAbsolutePath())
					.toJobParameters();
			jobLauncher.run(job, parameters);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
