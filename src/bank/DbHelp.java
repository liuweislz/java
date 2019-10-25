package bank;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//import oracle.sql.BLOB;

public class DbHelp {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
//	private static String driverName="oracle.jdbc.driver.OracleDriver";
//	private String url="jdbc:oracle:thin:@localhost:1521:orcl";
//	private String user="scott";
//	private String password="lw";
	//加载驱动
	static{
		try {
//			Class.forName(driverName);
			Class.forName(MyProperties.getInstance().getProperty("driverName"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	//获取数据库的连接
	public Connection getConn() throws SQLException{
//		conn=DriverManager.getConnection(url,user,password);
		conn=DriverManager.getConnection(MyProperties.getInstance().getProperty("url"),MyProperties.getInstance());
		return conn;
	}
	//关闭资源
	public void closeAll(Connection conn,PreparedStatement pstmt,ResultSet rs){
		
		if(null!=rs){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(null!=pstmt){
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(null!=conn){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	/**
	 *单记录查�? select * from table_name where id=? 
	 *传入的参�? 集合�? 集合的参数顺序必须和？顺序一�?
	 * @throws SQLException 
	 *
	 */
	public Map<String, Object> selectSingle(String sql,List<Object> params) throws Exception{
		Map<String, Object> map=null;
		
	
		try {
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsList(pstmt, params);
			//获取结果�?
			rs=pstmt.executeQuery();
			//根据结果集对象获取到�?有结果集中所有列�?
			List<String> columnNames=getAllColumnName(rs);
			if(rs.next()){
				map=new HashMap<String,Object>();
				String typeName=null;//值的类型
				Object obj=null;  //获取的�??
				//循环�?有的列名
				for(String name:columnNames){
					obj=rs.getObject(name);
					if(null!=obj){
						typeName=obj.getClass().getName();
					}else{
						continue;
					}
					if("oracle.sql.BLOB".equals(typeName)){
//						//对图片进行处�?
//						BLOB blob=(BLOB)obj;
//						InputStream in=blob.getBinaryStream();//将此 Blob实例指定�? BLOB值作为流 Blob �? 
//						byte []bt=new byte[(int)blob.length()];
//						in.read(bt);
//						map.put(name, bt);//将blob类型值以字节数组形式存储
						
					}else{
						map.put(name, obj);
					}
				}
				
				
			}
		}  finally{
			closeAll(conn, pstmt, rs);
		}
		return map;
	}
	
	
	/**
	 * 返回多条记录查询操作 select * from table_name
	 * @throws SQLException 
	 */
	public List<Map<String, Object>> selectMutil(String sql,List<Object> params) throws Exception{
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Map<String, Object> map=null;
		try{
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsList(pstmt, params);
			//获取结果�?
			rs=pstmt.executeQuery();
			//根据结果集对象获取到�?有集合中�?有列�?
			List<String> columnNames=getAllColumnName(rs);
			while(rs.next()){
				map=new HashMap<String,Object>();
				String typeName=null;//值的类型
				Object obj=null;  //获取的�??
				//循环�?有的列名
				for(String name:columnNames){
					obj=rs.getObject(name);
					if(null!=obj){
						typeName=obj.getClass().getName();	
					}else{
						continue;
					}
					if("oracle.sql.BLOB".equals(typeName)){
//						//对图片进行处�?
//						BLOB blob=(BLOB)obj;
//						InputStream in=blob.getBinaryStream();
//						byte []bt=new byte[(int)blob.length()];
//						in.read(bt);
//						map.put(name, bt);//将blob类型值以字节数组形式存储
						
					}else{
						map.put(name, obj);
					}
				}
				list.add(map);
			}
		}finally{
			closeAll(conn, pstmt, rs);
		}
		return list;
	}
	/**
	 * 获取查询后的字段�?
	 * @throws SQLException 
	 */
	public List<String> getAllColumnName(ResultSet rs) throws SQLException{
		List<String> list=new ArrayList<String>();
		//ResultSetMetaData:可用于获取有关ResultSet对象中列的类型和属�?�的信息的对�?
		ResultSetMetaData data=rs.getMetaData();
		int count=data.getColumnCount();
		for(int i=1;i<=count;i++){
			String str=data.getColumnName(i);//获取指定列的列名
			//添加列名到List集合�?
			list.add(str);
		}
		return list;
	}
 //将集合设置到预编译对象中
	public void setParamsList(PreparedStatement pstmt,List<Object> params) throws SQLException{
		if(null==params || params.size()<=0){
			return;
		}
		for(int i=0;i<params.size();i++){
			pstmt.setObject(i+1, params.get(i));
		}
	}
	 /**
	  * 批处理操�?  多个 insert update delect 同一事务
	  * @param sql  多条SQL语句
	  * @param params  多条sql语句的参�?  每条sql语句参数在小List集合中，多个再封装到大的List集合  �?�?对应
	  * @return
	  */
	public int update(List<String> sqls,List<List<Object>> params)throws SQLException{
		int result=0;
		try {
			conn=getConn();
			//设置事务手动提交
			conn.setAutoCommit(false);
			//循环sql语句
			if(null==sqls ||sqls.size()<=0){
			return result; 
			}
			for(int i=0;i<params.size();i++){
				//获取sql语句并创建预编译对象
				pstmt=conn.prepareStatement(sqls.get(i));
				//获取对应的sql语句参数集合
				List<Object> param=params.get(i);
				//设置参数
				setParamsList(pstmt, param);
				//执行更新
				result=pstmt.executeUpdate();
				if(result<=0){
					return result;
				}
			}
			//手动提交
			conn.commit();
		} catch (Exception e) {
			//设置回滚
			conn.rollback();
			result=0;
			e.printStackTrace();
		}finally{
			//还原事务的状�?
			conn.setAutoCommit(true);
			closeAll(conn, pstmt, rs);
		}
		
		return result;
	}
	
	/**
	 * @throws SQLException 
	 * 更新操作 增删�?
	 * 传入的参�?  不定长的对象数组 传入的参数顺序必须和？顺序一�?
	 *  
	 */
	
	public int update(String sql,Object...params) throws SQLException{
		int result=0;
		try {
			conn=getConn();//获取连接对象
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsObject(pstmt, params);
			//执行
			result=pstmt.executeUpdate();
		} finally{
			closeAll(conn, pstmt, null);
		}
		return result;
	}
	//不定长参�?   设置参数  传入的参数顺序必须和？顺序一�?
	public void setParamsObject(PreparedStatement pstme,Object...params) throws SQLException{
		if(null==params ||params.length<=0){
			return;
		}
		for(int i=0;i<params.length;i++){
			//setObject():使用给定对象设置指定参数的�??
		pstmt.setObject(i+1, params[i]); //将数组中的第i个元素�?�设置为第i+1个问�?
		}
	}
	/**
	 * 更新操作  传入为list集合而不是不定长参数
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int update(String sql,List<Object> params) throws SQLException{
		int result=0;
		try {
			conn=getConn();//获取连接对象
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsObject(pstmt, params);
			//执行
			result=pstmt.executeUpdate();
		} finally{
			closeAll(conn, pstmt, null);
		}
		return result;
	}
	//   设置参数  传入的参数顺序必须和？顺序一�?
	public void setParamsObject(PreparedStatement pstme,List<Object> params) throws SQLException{
		if(null==params ||params.size()<=0){
			return;
		}
		for(int i=0;i<params.size();i++){
			//setObject():使用给定对象设置指定参数的�??
		pstmt.setObject(i+1, params.get(i)); //将数组中的第i个元素�?�设置为第i+1个问�?
		}
	}
	
	
	
	/**
	 * 聚合函数操作
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public double getPloymer(String sql,List<Object> params)throws SQLException{
		double result=0;
		try {
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			setParamsList(pstmt, params);
			rs=pstmt.executeQuery();
			if(rs.next()){
				result=rs.getDouble(1);
			}
		} finally{
			closeAll(conn, pstmt, rs);
			
		};
		
		return result;
	}
 	/**
 	 * 返回一行记录查询
 	 * @param sql
 	 * @param params
 	 * @param cls
 	 * @return
 	 * @throws Exception
 	 */
	public <T> T findSingle (String sql,List<Object> params,Class<T> cls) throws Exception{
		T t=null;
		try {
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsList(pstmt, params);
			rs=pstmt.executeQuery();
			//通过反射获取实体类中的所有方法
			Method []methods=cls.getDeclaredMethods();
			//通过反射获取实体类的所有属性
			//Field [] fields=cls.getDeclaredFields();	
			List<String> columnNames=getAllColumnName(rs);
			Object obj=null;

			if(rs.next()){
				//通过反射创建对象
				t=cls.newInstance();//默认调用无参数构造函数    newInstance()弱类型,效率低,只能调用无参构造
				//循环列
				for(String name : columnNames){
					obj=rs.getObject(name);
					//循环方法  set+name   setUname
					for(Method m:methods){
						if(("set"+name).equalsIgnoreCase(m.getName())){
							//set方法的形参类型进行判断    set方法的形参的数据类型
							String typeName=m.getParameterTypes()[0].getName();//返回一个 类对象的数组， 类以声明顺序表示由该对象表示的可执行文件的形式参数类型。
							//System.out.println(m.getParameterTypes());
							//System.out.println(typeName);//java.lang.Integer  java.lang.String
							if("java.lang.Integer".equals(typeName)){
								 m.invoke(t, rs.getInt(name));//激活此方法  传入的参数必须和底层方法的数据类型一致	
							} else if("java.lang.Double".equals(typeName)){
								 m.invoke(t, rs.getDouble(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 }else if("java.lang.Float".equals(typeName)){
								 m.invoke(t, rs.getFloat(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 } else if("java.lang.Long".equals(typeName)){
								 m.invoke(t, rs.getLong(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 }else{
								 m.invoke(t, rs.getString(name));
							 }
						}
					}
				}
				
			}	
		}finally{
			closeAll(conn, pstmt, rs);
		}
		return t;
	}
	
	
	/**
 	 * 返回多行记录查询
 	 * @param sql
 	 * @param params
 	 * @param cls
 	 * @return
 	 * @throws Exception
 	 */
	public <T> List<T> findMutil (String sql,List<Object> params,Class<T> cls) throws Exception{
		List<T> list=new ArrayList<T>();
		T t=null;
		try {
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsList(pstmt, params);
			rs=pstmt.executeQuery();
			//通过反射获取实体类中的所有方法
			Method []methods=cls.getDeclaredMethods();
			//通过反射获取实体类的所有属性
			//Field [] fields=cls.getDeclaredFields();	
			List<String> columnNames=getAllColumnName(rs);
			Object obj=null;

			while(rs.next()){
				//通过反射创建对象
				t=cls.newInstance();//默认调用无参数构造函数
				//循环列
				for(String name : columnNames){
					obj=rs.getObject(name);
					//循环方法  set+name   setUname
					for(Method m:methods){
						if(("set"+name).equalsIgnoreCase(m.getName())){
							//set方法的形参类型进行判断    set方法的形参的数据类型
							String typeName=m.getParameterTypes()[0].getName();
							if("java.lang.Integer".equals(typeName)){
								 m.invoke(t, rs.getInt(name));//激活此方法  传入的参数必须和底层方法的数据类型一致	
							} else if("java.lang.Double".equals(typeName)){
								 m.invoke(t, rs.getDouble(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 }else if("java.lang.Float".equals(typeName)){
								 m.invoke(t, rs.getFloat(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 } else if("java.lang.Long".equals(typeName)){
								 m.invoke(t, rs.getLong(name));//激活此方法  传入的参数必须和底层方法的数据类型一致
							 }else{
								 m.invoke(t, rs.getString(name));
							 }
						}
					}
				}
				//将对象添加到list集合
				list.add(t);
			}	
		}finally{
			closeAll(conn, pstmt, rs);
		}
		return list;
	}
 
	}
