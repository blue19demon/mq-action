package com.test.api;

import java.util.List;

public class User{
	private String name;
	private Integer age;
	private List<Pac> packages;

	public static class Pac{
		private String tag;
		private List<Integer> content;
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public List<Integer> getContent() {
			return content;
		}
		public void setContent(List<Integer> content) {
			this.content = content;
		}
		public Pac(String tag, List<Integer> content) {
			super();
			this.tag = tag;
			this.content = content;
		}
		public Pac() {
			super();
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<Pac> getPackages() {
		return packages;
	}

	public void setPackages(List<Pac> packages) {
		this.packages = packages;
	}

	public User(String name, Integer age, List<Pac> packages) {
		super();
		this.name = name;
		this.age = age;
		this.packages = packages;
	}

	public User() {
		super();
	}
	
}

