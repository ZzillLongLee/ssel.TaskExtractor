package kr.ac.jbnu.ssel.util;

import java.io.File;
import java.io.IOException;

import kr.ac.jbnu.ssel.Configuration.Configuration;

public class generateJarTest {

	public static void main(String[] args) {
		generateJarTest gjt = new generateJarTest();
		gjt.generateJarFiles(Configuration.clonedProjectsPath);
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
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
