package kr.ac.jbnu.ssel.CallHierarchy;

import java.util.List;

import kr.ac.jbnu.ssel.CallHierarchy.Tree.MethodNode;

public class CallHirarchyExtractorTest {
	public static void main(String[] args) {
		CallHirarchyExtractor che = new CallHirarchyExtractor();
		List<MethodNode> callGraph = che.generateCallGraph("D:\\test\\Task_V1.jar");
		System.out.println("please check the structure of Call Graph");
	}
}
