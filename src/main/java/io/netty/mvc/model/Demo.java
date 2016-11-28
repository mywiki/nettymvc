package io.netty.mvc.model;

public class Demo {
    private Integer demoId;

    private String demoStr;

    public Demo() {
		super();
	}

	public Demo(Integer demoId, String demoStr) {
		super();
		this.demoId = demoId;
		this.demoStr = demoStr;
	}

	public Integer getDemoId() {
        return demoId;
    }

    public void setDemoId(Integer demoId) {
        this.demoId = demoId;
    }

    public String getDemoStr() {
        return demoStr;
    }

    public void setDemoStr(String demoStr) {
        this.demoStr = demoStr == null ? null : demoStr.trim();
    }
}