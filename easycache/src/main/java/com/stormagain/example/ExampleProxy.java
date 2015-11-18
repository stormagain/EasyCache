package com.stormagain.example;

import com.stormagain.easycache.Cache;
import com.stormagain.easycache.EasySpCache;
import com.stormagain.easycache.Key;
import com.stormagain.easycache.LoadCache;

/**
 * Created by 37X21=777 on 15/11/18.
 */

@EasySpCache(name = "example_sp")
public interface ExampleProxy {

    @Cache
    void cacheStudent(@Key(value = "student") Student student);

    @LoadCache(key = "student",getClassType = Student.class)
    Student loadStudent();

}
