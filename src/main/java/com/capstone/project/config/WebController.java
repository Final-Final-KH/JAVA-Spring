package com.capstone.project.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController implements ErrorController {
    private static final String PATH = "/error"; // 오류 경로

    @RequestMapping(value = PATH)
    public ModelAndView handleError() {
        // 모든 요청을 index.html로 포워딩
        return new ModelAndView("forward:/index.html");
    }

}
