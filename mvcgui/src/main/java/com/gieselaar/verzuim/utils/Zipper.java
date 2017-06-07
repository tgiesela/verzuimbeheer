package com.gieselaar.verzuim.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {
	List<String> fileList;
	private String sourcefolder;
    private boolean verbose = false;
    private final static Logger logger = Logger.getLogger("Zipper");
	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public Zipper() {
		fileList = new ArrayList<String>();
	}

	/**
	 * Zip it. All files added by addPath will be inserted into the zip file
	 * 
	 * @param zipFile
	 *            output ZIP file location
	 */
	public void zipIt(String zipFile) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			if (verbose)
				logger.info("Output to Zip : " + zipFile);

			for (String file : this.fileList) {

				if (verbose)
					logger.info("File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(sourcefolder
						+ File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			zos.close();

			if (verbose)
				logger.info("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void unZipIt(String zipFile, String targetfolder) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory if it does not exist
			File folder = new File(targetfolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(
					new FileInputStream(zipFile));

			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(targetfolder + File.separator
						+ fileName);

				if (verbose)
					logger.info("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			if (verbose)
				logger.info("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addPath(File node) {
		if (node.isFile()) {
			sourcefolder = node.getParent();
		}
		else
			if (node.isDirectory()) {
				sourcefolder = node.getAbsolutePath();
			}
			else
				throw new RuntimeException("Invalid file type");
		generateFileList(node);
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * This method can be called multiple times to build the filelist.
	 * 
	 * @param node
	 *            file or directory
	 */
	private void generateFileList(File node) {

		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for zip, this is the path relative to the
	 * SOURCE_FOLDER
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file) {
		return file.substring(sourcefolder.length() + 1, file.length());
	}

}
