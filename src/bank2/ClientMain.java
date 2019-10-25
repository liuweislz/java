package bank2;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ClientMain {
	private Label lblip;
	private Socket socket;
	protected Shell shell;
	private  Talker talker;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ClientMain window = new ClientMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(772, 431);
		shell.setText("SWT Application");
		
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DiscData.stackLayout.topControl=DiscData.regiser;
				shell.layout();
			}
		});
		menuItem.setText("银行开户");
		
		MenuItem menuItem_1 = new MenuItem(menu, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DiscData.stackLayout.topControl=DiscData.diposit;
				shell.layout();
			}
		});
		menuItem_1.setText("存款");
		
		MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DiscData.stackLayout.topControl=DiscData.withdraw;
				shell.layout();
			}
		});
		menuItem_2.setText("取款");
		
		MenuItem menuItem_3 = new MenuItem(menu, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DiscData.stackLayout.topControl=DiscData.transfer;
				shell.layout();
			}
		});
		menuItem_3.setText("转账");
		
		MenuItem menuItem_4 = new MenuItem(menu, SWT.NONE);
		menuItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		menuItem_4.setText("退出系统");
		
		Composite composite = new Composite(shell, SWT.NONE);
		DiscData.regiser=new regiser(shell, SWT.NONE);
		DiscData.diposit=new diposit(shell, SWT.NONE);
		DiscData.withdraw=new withdraw(shell, SWT.NONE);
		DiscData.transfer=new transfer(shell, SWT.NONE);
		shell.setLayout(DiscData.stackLayout);
		DiscData.stackLayout.topControl=composite;
		
		Label label = new Label(composite, SWT.NONE);
		label.setBounds(97, 92, 76, 20);
		label.setText("欢迎");
		
	    lblip = new Label(composite, SWT.NONE);
		lblip.setBounds(525, 10, 219, 30);
		lblip.setText("银行服务器IP:");

		begin();
	}
	
	public void begin(){
		new Thread(){
			public void run(){
			try{
				try {
					//连接服务器
				    socket=new Socket("127.0.0.1", 8888);
				} catch (IOException e) {
					e.printStackTrace();
				}
				shell.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageBox msg=new MessageBox(shell);
						msg.setMessage("提示");
						msg.setText("你已进入银行卡操作界面");
						msg.open();	
					}
				});
				talker=new Talker(socket, new MsgListener() {

				@Override
					public void onConnect(InetAddress addr) {
						//连接成功，显示服务器IP
						shell.getDisplay().asyncExec(new Runnable() {
							
							@Override
							public void run() {
								lblip.setText("银行服务器IP:"+addr);
							}
						});
					}
				});		
			}catch(Exception e){
				throw new RuntimeException(e);
			}		
			}	
		}.start();
	}
}
