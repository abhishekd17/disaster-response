package com.disaster.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {"/reports", "/create", "/dashboard"})
    public String forward() {
        return "forward:/index.html";
    }
}
