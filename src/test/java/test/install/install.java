package test.install;

import javax.swing.JDialog;

import org.testng.annotations.Test;

import tools.commonTools.installWindow;

public class install{

	@Test
	public void installNewPrj(){

		try{
			installWindow dialog = new installWindow();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setModal(true);
			dialog.setVisible(true);
		} catch(Exception e){
			e.printStackTrace();
		}

	}
}
