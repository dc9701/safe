package tools.commonTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class unzipFile{

	/**
	 * extract zip file to specified folder.
	 * 
	 * @param zipPath
	 * @param descDir
	 * @author isea533
	 */
	public static void unZipFiles(String zipPath, String descDir) throws IOException{

		unZipFiles(new File(zipPath), descDir);
	}

	/**
	 * extract zip file to specified folder.
	 * 
	 * @param zipFile
	 * @param descDir
	 * @author isea533
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String descDir) throws IOException{

		File pathFile = new File(descDir);
		if( ! pathFile.exists()){
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile);
		for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
			ZipEntry entry = (ZipEntry)entries.nextElement();
			String zipEntryName = entry.getName();
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");;
			// check if folder exists, or create the folder.
			File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
			if( ! file.exists()){
				file.mkdirs();
			}
			// check if the path is directory.
			if(new File(outPath).isDirectory()){
				continue;
			}
			// log output files.
			// System.out.println(outPath);

			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while((len = in.read(buf1)) > 0){
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
		zip.close();
		// System.out.println("******************Extract Completed********************");
	}

}
