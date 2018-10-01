package cn.seclib.vo;

import java.util.List;

public class FirstButton {

	private String name;
	
	private List<SubButton> sub_button;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SubButton> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<SubButton> sub_button) {
		this.sub_button = sub_button;
	}
	
}
