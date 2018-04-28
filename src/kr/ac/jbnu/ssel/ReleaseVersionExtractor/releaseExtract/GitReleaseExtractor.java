package kr.ac.jbnu.ssel.ReleaseVersionExtractor.releaseExtract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import kr.ac.jbnu.ssel.Configuration.Configuration;
import kr.ac.jbnu.ssel.ReleaseVersionExtractor.ReleaseUnit.Task;
import kr.ac.jbnu.ssel.util.JarFileExportor;

public class GitReleaseExtractor implements ReleaseUnitsExtractor {

	private FileRepository repo;
	private Git git;
	private RevWalk walk;
	private boolean isMasterBranch;
	private Ref branchOfCommit = null;

	@Override
	public HashMap<String, List<Task>> releaseUnitsExtract(String url) throws IOException {
		// TODO Auto-generated method stub
		List<Ref> branches;
		initGitRepo(url);
		HashMap<String, List<Task>> branchTaskHashMap = null;
		try
		{
			branches = git.branchList().call();
			// get all commit history according to the each branch
			branchTaskHashMap = getAllCommitHistory(branches);
			// This method get all Changed FileList from each commit and then get changed
			// code in a file from each commit
			getAllChangedFilesFromCommitLIst(branchTaskHashMap);
			// get all code from changed file in each commit
			getChangedCodes(branchTaskHashMap);
		} catch (GitAPIException e)
		{
			e.printStackTrace();
		}
		return branchTaskHashMap;

	}

	private void initGitRepo(String URL) {
		try
		{
			repo = new FileRepository(URL);
			git = new Git(repo);
			walk = new RevWalk(repo);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private HashMap<String, List<Task>> getAllCommitHistory(List<Ref> branches) throws GitAPIException, NoHeadException,
			IOException, MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException {
		HashMap<String, List<Task>> branchTaskHashMap = new HashMap<>();
		List<Task> taskList = new ArrayList<>();
		for (Ref branch : branches)
		{
			String branchName = branch.getName();

			Iterable<RevCommit> commits = git.log().all().call();
			for (RevCommit commit : commits)
			{
				boolean foundInThisBranch = false;

				RevCommit targetCommit = walk.parseCommit(repo.resolve(commit.getName()));
				for (Entry<String, Ref> e : repo.getAllRefs().entrySet())
				{
					if (e.getKey().startsWith(Constants.R_HEADS))
					{
						if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId())))
						{
							String foundInBranch = e.getValue().getName();
							if (branchName.equals(foundInBranch))
							{
								foundInThisBranch = true;
								break;
							}
						}
					}
				}

				if (foundInThisBranch)
				{
					Task task = new Task(commit);
					taskList.add(task);
				}
			}
			branchTaskHashMap.put(branchName, taskList);
		}
		return branchTaskHashMap;
	}

	private void getAllChangedFilesFromCommitLIst(HashMap<String, List<Task>> branchTaskHashMap) {
		for (Entry<String, List<Task>> branch : branchTaskHashMap.entrySet())
		{
			List<Task> branchTaskList = branch.getValue();
			for (int i = 0; i < branchTaskList.size(); i++)
			{
				try
				{
					Task task = branchTaskList.get(i);
					RevCommit lastestCommit = task.getCommit();
					AbstractTreeIterator lastestTreeIterator = getCanonicalTreeParser(lastestCommit);

					RevCommit previousCommit = null;
					AbstractTreeIterator oldTreeIterator = null;
					// get prviousCommit by the before last index
					if (i != branchTaskList.size() - 1)
					{
						previousCommit = branchTaskList.get(i + 1).getCommit();
						oldTreeIterator = getCanonicalTreeParser(previousCommit);
					}

					List<DiffEntry> difFileLists = null;
					// get all changed File List
					if (previousCommit != null && oldTreeIterator != null)
					{
						difFileLists = git.diff().setNewTree(lastestTreeIterator).setOldTree(oldTreeIterator).call();
						task.setDiffFileList(difFileLists);
						// get Changed context from changed file list in each commit
						HashMap<String, String> diffFileChangedMap = new HashMap<>();
						for (DiffEntry difFile : difFileLists)
						{

							// if (difFile.getNewPath().contains(Configuration.Constants.JAVA_EXTENTION))
							// {
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							DiffFormatter formatter = new DiffFormatter(stream);
							formatter.setRepository(repo);
							formatter.format(difFile);
							diffFileChangedMap.put(difFile.getNewPath(), stream.toString());
							// }
						}
						task.setDiffFileChangedMap(diffFileChangedMap);
					}
				} catch (IOException | GitAPIException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	private void getChangedCodes(HashMap<String, List<Task>> branchTaskHashMap) {
		HashMap<String, String> changedCodesMap = null;
		for (Entry<String, List<Task>> branch : branchTaskHashMap.entrySet())
		{
			List<Task> branchTaskList = branch.getValue();
			for (int i = 0; i < branchTaskList.size(); i++)
			{
				Task task = branchTaskList.get(i);
				List<DiffEntry> diffFileList = task.getDiffFileList();
				changedCodesMap = new HashMap<>();
				if (i != branchTaskList.size() - 1)
				{
					for (DiffEntry diffEntry : diffFileList)
					{
						String filePath = diffEntry.getNewPath();
						if (filePath.endsWith(kr.ac.jbnu.ssel.Configuration.Constants.JAVA_EXTENTION))
						{
							RevTree tree = task.getCommit().getTree();
							try
							{
								TreeWalk treeWalk = new TreeWalk(repo);
								treeWalk.addTree(tree);
								treeWalk.setRecursive(true);
								treeWalk.setFilter(PathFilter.create(filePath));
								if (!treeWalk.next())
								{
									throw new IllegalStateException("Did not find expected file: " + filePath);
								}
								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								ObjectId objectId = treeWalk.getObjectId(0);
								ObjectLoader loader = repo.open(objectId);
								loader.copyTo(stream);
								changedCodesMap.put(filePath, stream.toString());
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				task.setChangedCodesMap(changedCodesMap);
			}
		}
	}

	private AbstractTreeIterator getCanonicalTreeParser(ObjectId commitId) throws IOException {
		RevCommit commit = walk.parseCommit(commitId);
		ObjectId treeId = commit.getTree().getId();
		ObjectReader reader = git.getRepository().newObjectReader();
		CanonicalTreeParser treeParser = new CanonicalTreeParser();
		treeParser.reset(reader, treeId);
		return treeParser;
	}

	@Override
	public void getCommitProejctsAsJar(HashMap<String, List<Task>> branchTaskHashMap)
			throws IOException, GitAPIException {
		for (Entry<String, List<Task>> branch : branchTaskHashMap.entrySet())
		{
			if (branch.getKey().endsWith(kr.ac.jbnu.ssel.Configuration.Constants.BRANCH_MASTER))
			{
				List<Task> branchTaskList = branch.getValue();
				Collections.reverse(branchTaskList);
				int i = 0;
				for (Task task : branchTaskList)
				{
					String remoteBranchName = "Task_V" + i;
					RevCommit commit = task.getCommit();
					// build remote branch for each commit
					Ref remotedBranch = BuildRemoteBranch(commit, remoteBranchName);
					// clone branch project and then convert as jarFile.
					ClonedBranch(remotedBranch, remoteBranchName);
					i++;
				}
				break;
			}
		}
		generateJarFiles(Configuration.clonedProjectsPath);
	}

	private Ref BuildRemoteBranch(RevCommit commit, String remoteBranchName) {
		Ref branch = null;
		try
		{
			if (checkedExistsBranch(commit) == true)
			{
				if (isMasterBranch == true)
				{
					branch = branchOfCommit;
				} else
				{
					git.branchDelete().setBranchNames(branchOfCommit.getName()).setForce(true).call();
					branch = git.branchCreate().setStartPoint(commit).setName(remoteBranchName).call();
				}
			} else
			{
				branch = git.branchCreate().setStartPoint(commit.getName()).setName(remoteBranchName).call();
			}

			if (!branch.getName().endsWith(kr.ac.jbnu.ssel.Configuration.Constants.BRANCH_MASTER))
			{
				// push as remote branch
				PushCommand pushCommand = git.push();
				pushCommand.setRemote(kr.ac.jbnu.ssel.Configuration.Constants.ORIGIN_GIT);
				pushCommand.setRefSpecs(new RefSpec(branch.getName()));
				pushCommand.setCredentialsProvider(
						new UsernamePasswordCredentialsProvider(kr.ac.jbnu.ssel.Configuration.Constants.GIT_ID,
								kr.ac.jbnu.ssel.Configuration.Constants.GIT_PSW));
				pushCommand.call();
				System.out.println("created remote branch: " + branch.getName() + " in git");
			}
		} catch (GitAPIException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return branch;
	}

	private void ClonedBranch(Ref remotedBranch, String remoteBranchName)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		String remoteUrl = repo.getConfig().getString("remote", kr.ac.jbnu.ssel.Configuration.Constants.ORIGIN_GIT,
				"url");
		// prepare a new folder for the cloned repository
		File localPath = new File(Configuration.clonedProjectsPath + remoteBranchName);
		if (!localPath.delete())
		{
			FileUtils.deleteDirectory(localPath);
		}

		// then clone
		System.out.println("Cloning from " + remoteUrl + " to " + localPath);
		try (Git result = Git.cloneRepository().setURI(remoteUrl).setDirectory(localPath)
				.setCredentialsProvider(
						new UsernamePasswordCredentialsProvider(kr.ac.jbnu.ssel.Configuration.Constants.GIT_ID,
								kr.ac.jbnu.ssel.Configuration.Constants.GIT_PSW))
				.setBranch(remotedBranch.getName()).call())
		{
			// Note: the call() returns an opened repository already which needs to be
			// closed to avoid file handle leaks!
			git.branchDelete().setBranchNames(remoteBranchName).setForce(true).call();
			System.out.println("Having repository: " + result.getRepository().getDirectory());
		}
	}

	private void generateJarFiles(String clonedprojectspath) {
		try
		{
			File file = new File(clonedprojectspath);
			File[] clonedFileList = file.listFiles();
			for (File clonedFile : clonedFileList)
			{
				JarFileExportor jarFileExportor = new JarFileExportor();
				jarFileExportor.run(clonedFile.getPath());
				System.out.println(clonedFile.getName() + "is generated");
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkedExistsBranch(RevCommit commit) {
		try
		{
			List<Ref> branchList = git.branchList().call();
			for (Ref branch : branchList)
			{
				if (branch.getObjectId().getName().equals(commit.name()))
				{
					branchOfCommit = branch;
					if (branch.getName().endsWith(kr.ac.jbnu.ssel.Configuration.Constants.BRANCH_MASTER))
						isMasterBranch = true;
					return true;
				}
			}
		} catch (GitAPIException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public List<String> getAllChangedClassList(HashMap<String, List<Task>> branchTaskHashMap) {
		List<String> allClassList = new ArrayList<>();
		for (Entry<String, List<Task>> branch : branchTaskHashMap.entrySet())
		{
			if (branch.getKey().endsWith(kr.ac.jbnu.ssel.Configuration.Constants.BRANCH_MASTER))
			{
				List<Task> branchTaskList = branch.getValue();
				Collections.reverse(branchTaskList);
				for (Task task : branchTaskList)
				{
					List<DiffEntry> taskChangedFileList = task.getDiffFileList();
					if (taskChangedFileList != null)
					{
						for (DiffEntry taskChangedFile : taskChangedFileList)
						{
							if (taskChangedFile.getNewPath().endsWith(Configuration.JAVA_FILE_EXTEND))
							{
								boolean isFileInList = checkIsFileInList(allClassList, taskChangedFile);
								if (isFileInList == false)
									allClassList.add(taskChangedFile.getNewPath());
							}
						}
					}
				}
			}
		}
		return allClassList;

	}

	private boolean checkIsFileInList(List<String> allClassList, DiffEntry taskChangedFile) {
		boolean isFileInList = false;
		// TODO Auto-generated method stub
		String changedFilePath = taskChangedFile.getNewPath();
		for (String classList : allClassList)
		{
			if (changedFilePath.equals(classList))
			{
				return isFileInList = true;
			}
		}
		return isFileInList;
	}

}
