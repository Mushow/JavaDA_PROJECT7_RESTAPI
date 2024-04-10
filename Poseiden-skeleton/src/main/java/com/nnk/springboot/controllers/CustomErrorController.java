package com.nnk.springboot.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("errorMsg", "Forbidden access!");
        return "403";
    }

}
