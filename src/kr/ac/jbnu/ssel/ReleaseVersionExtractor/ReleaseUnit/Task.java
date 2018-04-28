package kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.revwalk.RevCommit;

public class Task {

	private String commitName;
	private String authorName;
	private Date date;
	private String commitMsg;
	private List<DiffEntry> diffFileList;
	private HashMap<String, String> diffFileChangedMap;
	private RevCommit commit;
	private HashMap<String, String> changedCodesMap;

	public Task(RevCommit commit) {
		this.commitName = commit.getName();
		this.authorName = commit.getAuthorIdent().getName();
		this.date = new Date(commit.getCommitTime() * 1000L);
		this.commitMsg = commit.getFullMessage();
		this.commit = commit;
	}

	public void setCommitName(String commitName) {
		this.commitName = commitName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCommitMsg(String commitMsg) {
		this.commitMsg = commitMsg;
	}

	public void setDiffFileList(List<DiffEntry> diffs) {
		this.diffFileList = diffs;
	}

	public void setCommit(RevCommit commit) {
		this.commit = commit;
	}

	public String getCommitName() {
		return commitName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public Date getDate() {
		return date;
	}

	public String getCommitMsg() {
		return commitMsg;
	}

	public List<DiffEntry> getDiffFileList() {
		return diffFileList;
	}

	public RevCommit getCommit() {
		return commit;
	}

	public void setDiffFileChangedMap(HashMap<String, String> diffFileChangedMap) {
		this.diffFileChangedMap = diffFileChangedMap;
	}

	public HashMap<String, String> getDiffFileChangedMap() {
		return diffFileChangedMap;
	}

	public HashMap<String, String> getChangedCodesMap() {
		return changedCodesMap;
	}

	public void setChangedCodesMap(HashMap<String, String> changedCodesMap) {
		this.changedCodesMap = changedCodesMap;
	}
}
