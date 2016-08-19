package com.han.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.han.springmvc.model.Course;
import com.han.springmvc.service.CourseService;

@Controller
@RequestMapping("/courses")
public class CourseController {

	private static Logger log = LoggerFactory.getLogger(CourseController.class);

	private CourseService courseService;

	@Autowired
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}

	// url：/courses/view?courseId=123
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String viewCourse(@RequestParam("courseId") Integer courseId, Model model) {
		log.debug("=================================================");
		log.debug("In viewCourse, courseId={}", courseId);
		log.debug("=================================================");
		Course course = courseService.getCourseById(courseId);
		model.addAttribute(course);

		return "course_overview";
	}

	// url:/courses/viewrestful/{courseId}
	@RequestMapping(value = "/viewrestful/{courseId}", method = RequestMethod.GET)
	public String viewCourseRestful(@PathVariable("courseId") Integer courseId, Map<String, Object> model) {
		log.debug("=================================================");
		log.debug("In viewCourseRestful, courseId={}", courseId);
		log.debug("=================================================");
		Course course = courseService.getCourseById(courseId);
		model.put("course", course);
		return "course_overview";
	}

	// url:/courses/viewrequest?courseId=123
	@RequestMapping(value = "/viewrequest", method = RequestMethod.GET)
	public String viewCourseRequest(HttpServletRequest request) {

		Integer courseId = Integer.valueOf(request.getParameter("courseId"));
		log.debug("=================================================");
		log.debug("In viewCourseRequest, courseId={}", courseId);
		log.debug("=================================================");
		Course course = courseService.getCourseById(courseId);
		request.setAttribute("course", course);

		return "course_overview";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET, params = "add")
	public String createCourse() {
		return "course_admin/edit";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String doSave(@ModelAttribute Course course) {
		log.debug("=================================================");
		log.debug("Info of Course:");
		log.debug(ReflectionToStringBuilder.toString(course));
		log.debug("=================================================");

		// 数据库持久化操作
		course.setCourseId(123);
		return "redirect:viewrestful/" + course.getCourseId();
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String showUploadPage(@RequestParam(value = "multi", required = false) Boolean multi) {

		if (multi != null && multi) {
			return "course_admin/multifile";
		}

		return "course_admin/file";
	}

	@RequestMapping(value = "/doUpload", method = RequestMethod.POST)
	public String doUploadFile(@RequestParam("file") MultipartFile file) throws IOException {

		if (!file.isEmpty()) {
			log.debug("=================================================");
			log.debug("Process file: {}", file.getOriginalFilename());
			log.debug("=================================================");
			FileUtils.copyInputStreamToFile(file.getInputStream(),
					new File("c:\\temp\\han\\", System.currentTimeMillis() + file.getOriginalFilename()));
		}
		return "success";
	}

	@RequestMapping(value = "/doUploadMore", method = RequestMethod.POST)
	public String doUploadMoreFile(MultipartHttpServletRequest multiRequest) throws IOException {
		Iterator<String> fileNames = multiRequest.getFileNames();
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile file = multiRequest.getFile(fileName);
			if (!file.isEmpty()) {
				log.debug("=================================================");
				log.debug("Process file: {}", file.getOriginalFilename());
				log.debug("=================================================");
				FileUtils.copyInputStreamToFile(file.getInputStream(),
						new File("c:\\temp\\han\\", System.currentTimeMillis() + file.getOriginalFilename()));

			}
		}

		return "success";
	}

	@RequestMapping(value = "/{courseId}", method = RequestMethod.GET)
	@ResponseBody
	public Course getCourseInJson(@PathVariable Integer courseId) {
		return courseService.getCourseById(courseId);
	}

	@RequestMapping(value = "/jsonType/{courseId}", method = RequestMethod.GET)
	public ResponseEntity<Course> getCourseInEntity(@PathVariable Integer courseId) {
		return new ResponseEntity<Course>(courseService.getCourseById(courseId), HttpStatus.OK);
	}

}
