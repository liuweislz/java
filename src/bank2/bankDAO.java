package bank2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class bankDAO {

	DbHelp db=new DbHelp();
	public int update(Integer accountid,float balance) throws SQLException{
		String sql="update account set balance=balance+? where accountid=?";
		return db.update(sql,balance,accountid);
	}
	
	public int add(Integer accountid,String password) throws SQLException{
		String sql="insert into account values(?,?,0)";
		return db.update(sql,accountid,password);
	} 
	public int transfer(Integer accountid1,Integer accountid2,float balance) throws SQLException{
		List <String> sqls=null;
		List<List<Object>> params=null;
		String sql1="update account set balance=balance-? where accountid=?";
		String sql2="update account set balance=balance+? where accountid=?";
		List<Object> param01=new ArrayList<Object>();
		param01.add(balance);
		param01.add(accountid1);
		sqls=new ArrayList<>();
		sqls.add(sql1);
		params=new ArrayList<List<Object>>();
		params.add(param01);
		
		List<Object> param02=new ArrayList<Object>();
		param02.add(balance);
		param02.add(accountid2);
		sqls.add(sql2);
		params.add(param02);
	
		return db.update(sqls, params);
		
	}
}
