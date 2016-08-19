package com.han.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloMvcController {

	@RequestMapping("/mvc")
	public String hello(){
		return "home";
	}
}
