package nies.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nies.data.User;
import nies.metadata.NiesConfig;

import com.csvreader.CsvReader;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class DisplayCsvData extends NiesSupport implements Preparable {
	private static final Logger logger = Logger.getLogger(DisplayCsvData.class);
	private String basedirname;
	private String filename;
	private List<String[]> data = new ArrayList<String[]>();
	private String[] headers = null;
	private int maxcols=0;
	private int[] selected_cols = null;
	
	public String[] getHeaders() { return headers; }
	public void setCols(String selection) {
		if (selection.length() == 0) return;
		String[] sels = selection.split(",");
		selected_cols = new int[sels.length];
		int i=0;
		for (String s : sels) {
			selected_cols[i++] = Integer.parseInt(s);
		}
	}
	public int[] getCols() { return selected_cols; }
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<String[]> getData() {
		return data;
	}
	public int getMaxcols() { return maxcols; }
	
	public String execute() {
		File file = new File(basedirname,filename);
		if(!file.exists()) {
			this.addActionError("File "+file.getAbsolutePath()+" doesn't exist!");
			return ERROR;
		}
		
		CsvReader reader;
		try {
			reader = new CsvReader(new FileReader(file));
			reader.readHeaders();
			if (reader.getHeader(0).startsWith("#")) this.headers = reader.getHeaders();
			else data.add(reader.getHeaders());
			
			for(; reader.readRecord();) {
				String[] row =reader.getValues(); 
				data.add(row);
				if (row.length > maxcols) maxcols = row.length;
			}
			
			if (selected_cols == null) {
				selected_cols = new int[maxcols];
				for (int i=0; i<maxcols; i++) {
					selected_cols[i] = i+1;
				}
			}
			
			if (headers == null) {
				headers = new String[maxcols];
				for (int i=0; i<maxcols; i++) {
					headers[i] = String.valueOf(i);
				}
			} else {
				for (int i=0; i<headers.length; i++) {
					if (headers[i] == "") headers[i] = String.valueOf(i);
				}
			}
			return SUCCESS;
		} catch (FileNotFoundException e) { this.addActionError(e.getMessage()); logger.error(e);
		} catch (IOException e) { this.addActionError(e.getMessage()); logger.error(e);
		}
		return ERROR;
	}
	@Override
	public void prepare() throws Exception {
		basedirname = NiesConfig.getProperty(UploadData.UPLOADS_DIR_PROP);
		if (basedirname == null) {
			this.addActionError("File uploading isn't set up properly. "
					+"The NIES properties file is missing the entry for "+UploadData.UPLOADS_DIR_PROP
					+" telling where uploaded files should be stored on the server.");
		}
	}
	
}
