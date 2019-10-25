package bank2;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class regiser extends Composite {
	private Text text_cno;
	private Text text_pwd;
	private Talker talker;
	private Label label;
	private Socket socket;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public regiser(Composite parent, int style) {
		super(parent, style);
		
		Label label_cno = new Label(this, SWT.NONE);
		label_cno.setBounds(158, 83, 141, 20);
		label_cno.setText("请输入想注册的卡号：");
		
		Label label_pwd = new Label(this, SWT.NONE);
		label_pwd.setBounds(209, 151, 90, 20);
		label_pwd.setText("请输入密码：");
		
		Button btn_register = new Button(this, SWT.NONE);
		btn_register.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
				String cardno=text_cno.getText().trim();
				String pwd=text_pwd.getText().trim();
				if(cardno!=null && pwd!=null){
					String msg=talker.sendDate(cardno, pwd);
					label.setText("提示"+msg);
					text_cno.setText("");
					text_pwd.setText("");
				}	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_register.setBounds(286, 247, 98, 30);
		btn_register.setText("注册");
		
		text_cno = new Text(this, SWT.BORDER);
		text_cno.setBounds(315, 80, 126, 26);
		
		text_pwd = new Text(this, SWT.BORDER);
		text_pwd.setBounds(315, 148, 126, 26);
		
		label = new Label(this, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label.setBounds(136, 194, 460, 20);
		
	}
	

}
