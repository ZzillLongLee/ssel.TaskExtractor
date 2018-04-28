package kr.ac.jbnu.ssel.CallHierarchy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IPath;

import gr.gousiosg.javacg.InvokedMethod.InvokedMethod;
import gr.gousiosg.javacg.stat.JCallGraph;
import kr.ac.jbnu.ssel.CallHierarchy.Tree.InvokedMethodNode;
import kr.ac.jbnu.ssel.CallHierarchy.Tree.MethodNode;
import kr.ac.jbnu.ssel.Configuration.Configuration;

public class CallHirarchyExtractor {

	private HashMap<String, List<InvokedMethod>> callListMap;

	public List<MethodNode> generateCallGraph(String jarFilePath) {
		JCallGraph jCallGraph = new JCallGraph();
		callListMap = jCallGraph.getCallList(jarFilePath);
		// removing invoked method call from others(library, jre, ...etc.)
		List<String> classList = jCallGraph.getClassList();
		filteringClass(classList);
		// building call hierarchy
		List<MethodNode> methodCallHierarchyList = buildCallHierarchy();
		return methodCallHierarchyList;
	}

	private List<MethodNode> buildCallHierarchy() {
		List<MethodNode> methodNodes = new ArrayList<>();
		List<MethodNode> methodNodesToCompare = new ArrayList<>();
		// building topNode for call graph
		for (Entry<String, List<InvokedMethod>> callList : callListMap.entrySet())
		{
			MethodNode methodNode = new MethodNode(callList.getKey(), callList.getValue());
			methodNodes.add(methodNode);
			methodNodesToCompare.add(methodNode);
		}
		for (int i = 0; i < methodNodes.size(); i++)
		{
			MethodNode methodNode = methodNodes.get(i);
			List<InvokedMethodNode> invokedMethodList = methodNode.getChildren();
			if (methodNode.hasChildren() == true)
			{
				for (InvokedMethodNode invokedMethodNode : invokedMethodList)
				{
					for (MethodNode methodNodeToCompare : methodNodesToCompare)
					{
						if (invokedMethodNode.getName().equals(methodNodeToCompare.getName()))
						{
							invokedMethodNode.setMethodNode(methodNodeToCompare);
							break;
						}
					}
					buildChildNode(methodNodesToCompare, invokedMethodNode);
				}
			}
		}
		return methodNodes;
	}

	private void buildChildNode(List<MethodNode> methodNodesToCompare, InvokedMethodNode invokedMethodNode) {
		if (invokedMethodNode.hasChildren() == true)
		{
			List<InvokedMethodNode> lowLevelInvokedMethodList = invokedMethodNode.getChildren();
			for (InvokedMethodNode lowLevelInvokedMethodNode : lowLevelInvokedMethodList)
			{
				for (MethodNode methodNodeToCompare : methodNodesToCompare)
				{
					if (lowLevelInvokedMethodNode.getName().equals(methodNodeToCompare.getName()))
					{
						lowLevelInvokedMethodNode.setMethodNode(methodNodeToCompare);
						break;
					}
				}
				buildChildNode(methodNodesToCompare, lowLevelInvokedMethodNode);
			}
		}
	}

	private void filteringClass(List<String> projectClassList) {
		ArrayList<List<InvokedMethod>> hashValuesToRemove = new ArrayList<List<InvokedMethod>>();
		for (Entry<String, List<InvokedMethod>> method : callListMap.entrySet())
		{
			List<InvokedMethod> invokedList = method.getValue();
			List<InvokedMethod> valuesToRemove = new ArrayList<InvokedMethod>();
			for (InvokedMethod invokedMethod : invokedList)
			{
				boolean isIncludedwithinProjectClass = isIncludedwithInProjectClass(projectClassList, invokedMethod);
				if (isIncludedwithinProjectClass == false)
					valuesToRemove.add(invokedMethod);
			}
			invokedList.removeAll(valuesToRemove);
		}
	}

	private boolean isIncludedwithInProjectClass(List<String> projectClassList, InvokedMethod invokedMethod) {
		String invokedClass = invokedMethod.getReferenceType();
		for (String projectClass : projectClassList)
		{
			if (invokedClass.equals(projectClass))
				return true;
		}
		return false;
	}
}
