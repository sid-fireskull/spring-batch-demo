package com.springbatch.demo.config;

import org.springframework.batch.item.ItemProcessor;

import com.springbatch.demo.entity.Student;

public class StudentProcessor implements ItemProcessor<Student, Student> {

	@Override
	public Student process(Student Student) throws Exception {
		return Student;
	}
}
