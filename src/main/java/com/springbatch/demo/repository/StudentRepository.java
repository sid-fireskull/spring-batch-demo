package com.springbatch.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbatch.demo.entity.Student;



public interface StudentRepository  extends JpaRepository<Student,Long> {
}
