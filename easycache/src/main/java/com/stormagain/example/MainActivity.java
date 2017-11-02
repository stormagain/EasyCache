package com.stormagain.example;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.stormagain.easycache.EasyCacheManager;
import com.stormagain.easycache.R;

import java.util.HashSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Example
        Student student = new Student();
        student.name = "zhangsan";
        student.age = 18;

        Student student1 = new Student();
        student1.name = "lisi";
        student1.age = 20;

        ExampleProxy exampleProxy = EasyCacheManager.getInstance().create(ExampleProxy.class);
        //cache
        exampleProxy.cacheStudent(student);
        //loadCache
        exampleProxy.loadStudent().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new Consumer<Student>() {
                    @Override
                    public void accept(Student cachedStudent) throws Exception {
                        Log.d("Student", "student:" + cachedStudent.name + " " + cachedStudent.age);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }
        );


        HashSet<Student> students = new HashSet<>();
        students.add(student);
        students.add(student1);

        exampleProxy.cacheStudents(students);
        HashSet<Student> set = exampleProxy.loadStudents();
        for (Student s : set) {
            Log.d("Student", "student:" + s.name + " " + s.age);
        }

        //批量删除多个key
//        exampleProxy.removeStudent();
//        exampleProxy.loadStudents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
