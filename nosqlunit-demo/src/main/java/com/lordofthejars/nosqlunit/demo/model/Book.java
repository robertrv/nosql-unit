package com.lordofthejars.nosqlunit.demo.model;


import lombok.Value;

@Value
public class Book {

	private String title;
	
	private int numberOfPages;

    private Long id;
}
