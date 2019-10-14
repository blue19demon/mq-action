package com.slack.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic implements CommunicationWay{
	private String name;

	@Override
	public String name() {
		return name;
	}
}
