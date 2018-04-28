package kr.ac.jbnu.ssel.ReleaseVersionExtractor.releaseExtract;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;

public interface ReleaseUnitsExtractor {
	
	public HashMap<String, List<Task>> releaseUnitsExtract(String url) throws IOException;
	
	public void getCommitProejctsAsJar(HashMap<String, List<Task>> branchTaskHashMap) throws IOException, GitAPIException;

	public List<String> getAllChangedClassList(HashMap<String, List<Task>> releaseUnits);
	
}
