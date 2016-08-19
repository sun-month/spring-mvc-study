package com.han.springmvc.service;

import org.springframework.stereotype.Service;

import com.han.springmvc.model.Course;
@Service
public interface CourseService {
	Course getCourseById(Integer courseId);
}
