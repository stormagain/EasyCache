package com.stormagain.example;

import com.stormagain.easycache.Cache;
import com.stormagain.easycache.Clear;
import com.stormagain.easycache.EasyCache;
import com.stormagain.easycache.Key;
import com.stormagain.easycache.LoadCache;
import com.stormagain.easycache.RemoveKey;
import com.stormagain.easycache.Type;

import java.util.HashSet;

/**
 * Created by 37X21=777 on 15/11/18.
 */
@EasyCache(name = "example", type = Type.SHARED_PREFERENCE)
public interface ExampleProxy {

    @Cache
    void cacheStudent(@Key(value = "student") Student student);

    @LoadCache(key = "student", classType = Student.class)
    Student loadStudent();

    @RemoveKey(key = "student")
    void removeStudent();

    @Clear
    void clearExample();

    @Cache
    void cacheStudents(@Key(value = "students") HashSet<Student> students);

    @LoadCache(key = "students", classType = Student.class, collectionType = HashSet.class)
    HashSet<Student> loadStudents();

}
