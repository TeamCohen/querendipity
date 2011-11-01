package nies.actions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.interceptor.Interceptor;

import nies.data.User;
import nies.metadata.NiesConfig;

public class UploadData extends NiesSupport implements UserAware {
	private static final Logger logger = Logger.getLogger(UploadData.class);
	public static final String UPLOADS_DIR_PROP="nies.uploads";
	private File file;
	private String fileContentType;
	private String fileFileName;
	private User user;
	
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	
	public String upload() {
		if (this.file == null) return INPUT;
		String dir = NiesConfig.getProperty(UPLOADS_DIR_PROP);
		if (dir == null) {
			this.addActionError("File uploading isn't set up properly. "
					+"The NIES properties file is missing the entry for "+UPLOADS_DIR_PROP
					+" telling where uploaded files should be stored on the server.");
			return ERROR;
		}
		File saveDir = new File(dir,user.getUsername());
		if (!saveDir.exists()) saveDir.mkdir();
		File saveFile = new File(saveDir,this.fileFileName);
		try {
			FileWriter writer = new FileWriter(saveFile);
			FileReader reader = new FileReader(file);
			int c;
			logger.info("Copying file...");
			while ((c = reader.read()) != -1) writer.write(c);
			reader.close();
			writer.close();
		} catch (IOException e) {
			this.addActionError("Something went wrong while moving the uploaded file to its permanent location.");
			logger.error(e);
			return ERROR;
		}

		logger.info("Success");
		this.addActionMessage("Uploaded "+saveFile.getName());
		return SUCCESS;
	}
	@Override
	public void setUser(User u, Interceptor i) {
		this.user = u;
	}
}
