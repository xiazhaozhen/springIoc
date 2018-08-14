package com.xx.controller;

import com.xx.service.BookService;
import org.xia.springframwork.ioc.annotation.XiaAutowired;
import org.xia.springframwork.ioc.annotation.XiaController;

/**
 * Created by sgl on 18/8/8.
 */
@XiaController
public class BookController {
    @XiaAutowired
    private BookService bookService;
}
