package tools.commonTools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class installWindow extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6969102672875294672L;
	private final JPanel contentPanel = new JPanel();
	private JTextField prjName;
	private JTextField prjRoot;
	private JComboBox<String> prjType;
	private JTextField artifactID;
	private JTextField groupID;
	private JTextField version;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){

		try{
			installWindow dialog = new installWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public installWindow(){

		setAlwaysOnTop(true);
		setTitle("Initiate Your Project");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblProjectName = new JLabel("Project Name:");
			lblProjectName.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblProjectName.setBounds(10, 91, 91, 14);
			contentPanel.add(lblProjectName);
		}

		prjName = new JTextField();
		prjName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		prjName.setText("New Project");
		prjName.setBounds(111, 88, 292, 20);
		contentPanel.add(prjName);
		prjName.setColumns(10);
		{
			JLabel lblProjectType = new JLabel("Project Type:");
			lblProjectType.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblProjectType.setBounds(10, 113, 91, 14);
			contentPanel.add(lblProjectType);
		}

		prjType = new JComboBox<String>();
		prjType.setFont(new Font("Tahoma", Font.PLAIN, 11));
		prjType.setBounds(111, 110, 168, 20);
		prjType.addItem("Web");
		prjType.addItem("Android");
		prjType.addItem("iOS");
		prjType.addItem("FrontPanel");
		contentPanel.add(prjType);
		{
			JLabel lblPojectRoot = new JLabel("Poject Root:");
			lblPojectRoot.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblPojectRoot.setBounds(10, 135, 91, 14);
			contentPanel.add(lblPojectRoot);
		}
		{
			prjRoot = new JTextField();
			prjRoot.setFont(new Font("Tahoma", Font.PLAIN, 11));
			prjRoot.setEditable(false);
			prjRoot.setBounds(111, 132, 190, 20);
			contentPanel.add(prjRoot);
			prjRoot.setColumns(10);
			prjRoot.setText(new File(javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory() + "/"
					+ prjName.getText().replace(" ", "_")).getAbsolutePath());
		}

		JButton btnBowser = new JButton("Bowser...");
		btnBowser.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnBowser.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				selectFile();
			}
		});
		btnBowser.setBounds(311, 129, 89, 23);
		contentPanel.add(btnBowser);
		{
			JLabel lblArtifactId = new JLabel("Artifact ID:");
			lblArtifactId.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblArtifactId.setBounds(10, 176, 91, 20);
			contentPanel.add(lblArtifactId);
		}
		{
			artifactID = new JTextField();
			artifactID.setFont(new Font("Tahoma", Font.PLAIN, 11));
			artifactID.setText("NewProject");
			artifactID.setBounds(111, 176, 190, 20);
			contentPanel.add(artifactID);
			artifactID.setColumns(10);
		}
		{
			groupID = new JTextField();
			groupID.setFont(new Font("Tahoma", Font.PLAIN, 11));
			groupID.setText("TEST");
			groupID.setBounds(111, 154, 190, 20);
			contentPanel.add(groupID);
			groupID.setColumns(10);
		}
		{
			version = new JTextField();
			version.setFont(new Font("Tahoma", Font.PLAIN, 11));
			version.setText("0.0.1 - SNAPSHOT");
			version.setBounds(111, 198, 190, 20);
			contentPanel.add(version);
			version.setColumns(10);
		}
		{
			JLabel lblGroupId = new JLabel("Group ID:");
			lblGroupId.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblGroupId.setBounds(10, 157, 91, 14);
			contentPanel.add(lblGroupId);
		}
		{
			JLabel lblVersion = new JLabel("Version:");
			lblVersion.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblVersion.setBounds(10, 201, 91, 14);
			contentPanel.add(lblVersion);
		}

		JTextPane txtpnThanksForChoosing = new JTextPane();
		txtpnThanksForChoosing.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnThanksForChoosing.setBackground(SystemColor.control);
		txtpnThanksForChoosing.setEditable(false);
		txtpnThanksForChoosing
				.setText("Thanks for choosing SAFE!\r\n\r\nBy seeing this dialog, we are pleased to tell you your SAFE is well configured and will be installed on your system soon.\r\nNow we are about to help you create your first initial project.");
		txtpnThanksForChoosing.setBounds(10, 5, 414, 80);
		contentPanel.add(txtpnThanksForChoosing);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
				okButton.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e){

						clickedOK();

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
				cancelButton.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e){

						close();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void clickedOK(){

		int prjT = prjType.getSelectedIndex();
		String prjN = prjName.getText().replace(" ", "_");
		String prjR = prjRoot.getText();
		String gID = groupID.getText();
		String aID = artifactID.getText();
		String ver = version.getText();
		XmlFormatter xf = new XmlFormatter();
		String sourceFolder = "C:\\sample\\";

		File root = new File(prjR);
		if( ! root.exists()){
			if(JOptionPane.showConfirmDialog(this, "Folder Does Not Exist, Do You Want to Create?", "Folder Not Found", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				if( ! root.mkdirs()){
					JOptionPane.showMessageDialog(this, "Cannot Create Root Folder!", "Folder Creation Error", JOptionPane.ERROR_MESSAGE);
				}
			} else{
				return;
			}
		} else{
			if(JOptionPane.showConfirmDialog(this, "Folder Already Exist, Do You Want to Override it? All content in this folder will be earsed.",
					"Folder Conflict", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				delAllFile(prjR);
			} else{
				return;
			}
		}
		try{
			// Unzip sample files
			String sampleFile = CommonTools.getTestRoot().replaceAll("target/[\\s\\S-]{0,10}classes/", "src/test/resources/data/sample.zip");
			sourceFolder = CommonTools.getTestRoot().replaceAll("target/[\\s\\S－]{0,10}classes/", "target/zip/");
			unzipFile.unZipFiles(sampleFile, sourceFolder);
			sourceFolder += "sample/";

			// Create POM.xml
			String temp = readFile(sourceFolder + "pom.xml");
			String xmlPath = CommonTools.getTestRoot().replaceAll("target/[\\s\\S－]{0,10}classes/", "pom.xml");
			String SAFEGID = getValueFromXml(xmlPath, "groupId");
			String SAFEAID = getValueFromXml(xmlPath, "artifactId");
			String SAFEVER = getValueFromXml(xmlPath, "version");

			temp = temp.replace("__groupID__", gID);
			temp = temp.replace("__artifactID__", aID);
			temp = temp.replace("__version__", ver);
			temp = temp.replace("__prjName__", prjN);

			temp = temp.replace("__SAFEGID__", SAFEGID);
			temp = temp.replace("__SAFEAID__", SAFEAID);
			temp = temp.replace("__SAFEVER__", SAFEVER);

			writeFile((prjR + "/pom.xml"), xf.format(temp));

			// Determine the app type.

			String appType = "";
			switch(prjT){
			case 0:
				appType = "WebApp";
				break;

			case 1:
				appType = "AndroidApp";
				break;

			case 2:
				appType = "IOSApp";
				break;

			case 3:
				appType = "FrontPanel";
			}

			// Create Folder Structure
			new File(prjR + "/src/main/java").mkdirs();
			new File(prjR + "/src/test/java/fwk").mkdirs();
			new File(prjR + "/src/test/java/test/cases/" + prjN).mkdirs();
			new File(prjR + "/src/test/resources/conf/" + prjN + "/sut").mkdirs();
			new File(prjR + "/src/test/resources/conf/" + prjN + "/ui/" + appType + "/content").mkdirs();
			new File(prjR + "/src/test/resources/conf/" + prjN + "/ui/" + appType + "/message").mkdirs();
			new File(prjR + "/src/test/resources/data/" + prjN).mkdirs();
			new File(prjR + "/src/test/resources/data/common/browserProfiles/drivers").mkdirs();
			new File(prjR + "/src/test/resources/data/common/browserProfiles/seleniumOnChrome/default").mkdirs();
			new File(prjR + "/src/test/resources/data/common/browserProfiles/seleniumOnFirefox/default").mkdirs();
			new File(prjR + "/testngXML").mkdirs();

			// Create inherited framework class based on user's selection.
			temp = readFile(sourceFolder + "defaultFwkClass.java");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__prjType__", appType);
			writeFile(prjR + "/src/test/java/fwk/" + prjN + appType + ".java", temp);

			// Create Configuration Files
			// AutotestConfig.properties
			temp = readFile(sourceFolder + "AutotestConfig.properties");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__prjType__", appType);
			writeFile(prjR + "/src/test/resources/conf/AutotestConfig.properties", temp);

			// Default SUT file
			temp = readFile(sourceFolder + "defaultSUT.properties");
			writeFile(prjR + "/src/test/resources/conf/" + prjN + "/sut/Sut" + appType + "OnTest.properties", temp);

			// Default UI config
			temp = readFile(sourceFolder + "defaultUI.properties");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__prjType__", appType);
			writeFile(prjR + "/src/test/resources/conf/" + prjN + "/ui/default.properties", temp);

			// Default UIMAP
			temp = readFile(sourceFolder + "defaultUIMAP.json");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__version__", ver);
			writeFile(prjR + "/src/test/resources/conf/" + prjN + "/ui/" + appType + "/" + prjN + ".json", temp);

			// Default content/messages
			temp = readFile(sourceFolder + "defaultContent");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__prjType__", appType);
			writeFile(prjR + "/src/test/resources/conf/" + prjN + "/ui/" + appType + "/content/" + prjN + "Content", temp);
			writeFile(prjR + "/src/test/resources/conf/" + prjN + "/ui/" + appType + "/message/" + prjN + "Messages", temp);

			// Create a first test for new users:
			temp = readFile(sourceFolder + "sampleTest.java");
			temp = temp.replace("__prjName__", prjN);
			temp = temp.replace("__prjType__", appType);
			writeFile(prjR + "/src/test/java/test/cases/" + prjN + "/MyFirstTest.java", temp);

		}

		catch(IOException e){

			JOptionPane.showMessageDialog(this,
					"Error occured when creating new project, please contact us with this message and the folder structure already created!", "Failed",
					JOptionPane.ERROR_MESSAGE);
			close();
			return;
		}

		JOptionPane.showMessageDialog(this, "Project Created Successfully! Please Import It As Existing Maven Project to Eclipse.", "Success",
				JOptionPane.PLAIN_MESSAGE);
		close();
	}

	protected boolean writeFile(String fileName, String content) throws IOException{

		File target = new File(fileName);
		if( ! target.exists())
			target.createNewFile();

		int length = 0;
		char[] buffer = new char[2048];
		String temp = "";
		FileOutputStream fos = new FileOutputStream(target);
		BufferedReader br = new BufferedReader(new StringReader(content));
		while((length = br.read(buffer)) != - 1){
			temp = new String(buffer, 0, length);
			byte bytes[] = temp.getBytes();
			fos.write(bytes, 0, length);
		}
		fos.close();
		return true;
	}

	protected String readFile(String fileName) throws IOException{

		File source = new File(fileName);
		if( ! source.exists()){
			JOptionPane.showMessageDialog(this, "Source File " + source.getName() + " Does Not Exist!", "File Not Found!", JOptionPane.ERROR_MESSAGE);
			return "";
		}
		FileInputStream fis = new FileInputStream(source);
		InputStreamReader inR = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(inR);
		String returnValue = "";
		String temp;
		while((temp = br.readLine()) != null){
			returnValue += temp + "\r\n";
		}
		fis.close();
		return returnValue;
	}

	protected void selectFile(){

		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		jfc.setSelectedFile(new File(prjRoot.getText()));
		// jfc.setFileFilter(filter);
		if(jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			prjRoot.setText(new File(jfc.getSelectedFile().getAbsolutePath() + "/" + prjName.getText().replace(" ", "_")).getAbsolutePath());

	}

	/**
	 * Delete folder
	 * 
	 * @param filePathAndName
	 *            String Folder Path e.g.: c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public void delFolder(String folderPath){

		try{
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete();

		} catch(Exception e){
			System.out.println("Error deleting folder.");
			e.printStackTrace();

		}

	}

	/**
	 * Remove all the files in folder.
	 * 
	 * @param path
	 *            String Folder path e.g.: c:/fqf
	 */
	public void delAllFile(String path){

		File file = new File(path);
		if( ! file.exists()){
			return;
		}
		if( ! file.isDirectory()){
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for(int i = 0; i < tempList.length; i ++ ){
			if(path.endsWith(File.separator)){
				temp = new File(path + tempList[i]);
			} else{
				temp = new File(path + File.separator + tempList[i]);
			}
			if(temp.isFile()){
				temp.delete();
			}
			if(temp.isDirectory()){
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected String getValueFromXml(String xmlPath, String attribute){

		String value = null;
		Element root;
		SAXReader saxReader = new SAXReader();
		try{
			InputSource src = new InputSource(new StringReader(readFile(xmlPath)));
			Document document = saxReader.read(src);
			// final Document document = (Document)DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
			root = document.getRootElement();
			List<Element> param = root.elements();
			for(Element element:param){
				if(element.getName().equals(attribute)){
					value = element.getText();
					break;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("xml is not exist or attribute is not exist!");
		}

		return value;

	}

	protected void close(){

		this.setVisible(false);
		this.dispose();

		// System.exit(0);
	}
}
