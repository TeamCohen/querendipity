package nies.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.io.FileFilter;

import nies.data.User;
import nies.metadata.NiesConfig;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class UploadedFileList extends NiesSupport implements UserAware, Preparable {
	private static final FileFilter DIRECTORIES_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}	
	};
	private User user;
	private String baseDir;
	private Map<String,List<File>> files = new TreeMap<String,List<File>>();
	
	public Map<String,List<File>> getFiles() { return files; }
	public User getUser() { return user; }
	
	public String execute() {
		if (this.hasActionErrors()) return ERROR;
		
		File saveDir = new File(baseDir,user.getUsername());
		if (!saveDir.exists()) return SUCCESS;
		
		addDirectory(saveDir);
		return SUCCESS;
	}
	
	public String all() {
		if (this.hasActionErrors()) return ERROR;
		File base = new File(baseDir);
		for (File f : base.listFiles(DIRECTORIES_FILTER)) { addDirectory(f); }
		return SUCCESS;
	}

	protected void addDirectory(File dir) {
		List<File> list = new ArrayList<File>();
		files.put(dir.getName(), list);

		for (File f : dir.listFiles()) { list.add(f); }
	}
	
	@Override
	public void setUser(User u, Interceptor i) {
		user = u;
	}

	@Override
	public void prepare() throws Exception {
		baseDir = NiesConfig.getProperty(UploadData.UPLOADS_DIR_PROP);
		if (baseDir == null) {
			this.addActionError("File uploading isn't set up properly. "
					+"The NIES properties file is missing the entry for "+UploadData.UPLOADS_DIR_PROP
					+" telling where uploaded files should be stored on the server.");
		}
	}
}
