package com.xx.service.impl;

import com.xx.dao.BookDao;
import com.xx.service.BookService;
import org.xia.springframwork.ioc.annotation.XiaAutowired;
import org.xia.springframwork.ioc.annotation.XiaService;

/**
 * Created by sgl on 18/8/8.
 */
@XiaService
public class BookServiceImpl implements BookService{
    @XiaAutowired
    private BookDao bookDao;

    @Override
    public void add(){
        bookDao.add();
        System.out.println("xinzeng ....");
    }
}
