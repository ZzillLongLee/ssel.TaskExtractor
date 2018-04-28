package kr.ac.jbnu.ssel.ReleaseVersionExtractor;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.releaseExtract.GitReleaseExtractor;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.releaseExtract.ReleaseUnitsExtractor;


public class ReleaseVersionExtractorTest {

	public static void main(String[] args) throws IOException, GitAPIException {
		ReleaseUnitsExtractor releaseExtractor = new GitReleaseExtractor();
		HashMap<String, List<Task>> releaseUnits = releaseExtractor.releaseUnitsExtract(Configuration.GIT_URL);
		releaseExtractor.getCommitProejctsAsJar(releaseUnits);
		List<String> aa = releaseExtractor.getAllChangedClassList(releaseUnits);
		System.out.println("done!");
	}
}
