package executaPostgreSQL;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Executor extends JFrame {
	 JTextArea _resultArea = new JTextArea(6, 20);

	 Connection con = null;
     Statement st = null;
     ResultSet rs = null;

 
     String url = "jdbc:postgresql://localhost/guara_finances_dev";
     String user = "postgres";
     String password = "postgres";
	 
	 
	public Executor() {
        //... Set textarea's initial text, scrolling, and border.
        _resultArea.setText("Executando em");
        JScrollPane scrollingArea = new JScrollPane(_resultArea);
        
        //... Get the content pane, set layout, add to center
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollingArea, BorderLayout.CENTER);
        
        //... Set window characteristics.
        this.setContentPane(content);
        this.setTitle("TextAreaDemo B");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }
	
	public static void listf(String directoryName, String ext, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && file.getName().endsWith(ext)) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	            listf(file.getAbsolutePath(), ext, files);
	        }
	    }
	}
	
	public static void main(String[] args) {
		Executor e = new Executor();
		
		e.setVisible(true);
		 String filePath = new File(".").getPath();
	     e._resultArea.append(filePath+"\n");
		
		ArrayList<File> files = new ArrayList<>();
		
		listf(filePath, ".txt", files);
		
		
		try {
			e.readFiles(files);
		} catch (SQLException e1) {
			e._resultArea.append("Error: "+e1.getMessage()+"\n");
		}
		
		
	}

	private void readFiles(ArrayList<File> files) throws SQLException {
		
		this.createConnection();
		
		try {
			for (File f : files) {
				readLines(f);
			}
		} finally {
			this.con.close();
		}
	}

	private void readLines(File f) throws SQLException {
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
		  String sql = "";
		  while (( sql = reader.readLine()) != null ) {
		    //separate all csv fields into string array
		    this.executeSql(sql);
		  }
		} catch (IOException e) {
		    System.err.println(e);
		}
	}

	private void executeSql(String sql) throws SQLException {
		this._resultArea.append(sql+"\n");
		st.executeUpdate(sql);
	}

	private void createConnection() throws SQLException {
		con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT VERSION()");

        if (rs.next()) {
            System.out.println(rs.getString(1));
        }
	}
	
	
}
