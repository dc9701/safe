package tools.vpm;

import tools.commonTools.CommonTools;

//import hpc.framework.helper.LogUtils;

public class StackInfo {

	public enum Stack {
	    DEV2("dev2"),
	    PIE1("pie1"),
	    PRODUCTION("production"),
	    REF2("ref2"),
	    STAGE1("stage1"),
	    TEST1("test1");
	    private final String value;
	    private Stack(String value) {
	      this.value = value;
	    }
	    public boolean equalsStack(String stack) {
	      return (null==stack) ? false : stack.trim().equalsIgnoreCase(value);
	    }
	    public String toString() {
	      return value;
	    }
	  }
	
	public static Stack getStack(String stack) {
	    for( Stack s : Stack.values() ) {
	      if( s.equalsStack(stack.trim()) ) {
	        return s;
	      }
	    }
	    System.out.println(CommonTools.getCurrentTime() + " (" + Thread.currentThread().getId() + ") INFO - " + "getStack \"" + stack + "\" Stack does not exist.");
	    return null; // will never get here.
	  }
	
}

