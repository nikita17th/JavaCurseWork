package com.example.demo;


import java.io.IOException;

import static services.Port.Port.work;


public class ThirdService {

    public static void main(String[] args) {
        try {
            work();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
