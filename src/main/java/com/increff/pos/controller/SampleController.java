package com.increff.pos.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.increff.pos.api.ApiException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.increff.pos.util.IOUtil;

@Controller
public class SampleController {

    //Spring ignores . (dot) in the path. So we need fileName:.+
    //See https://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
    @RequestMapping(value = "/sample/{fileName:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws ApiException {
        // get your file as InputStream
        response.setContentType("text/csv");
        response.addHeader("Content-disposition:", "attachment; filename=" + fileName);
        String fileClasspath = "/com/increff/pos/" + fileName;
        InputStream is = SampleController.class.getResourceAsStream(fileClasspath);
        // copy it to response's OutputStream
        try {
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new ApiException("Unable to get sample file");
        } finally {
            IOUtil.closeQuietly(is);
        }

    }

}
