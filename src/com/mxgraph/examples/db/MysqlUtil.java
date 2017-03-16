/*
 * 链接mysql的工具类
 */
package com.mxgraph.examples.db;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;


public class MysqlUtil {
	//这些都没用，其实可以把这些写在外部配置文件里面这块统一读取
	private final String DBDRIVER = "com.mysql.jdbc.Driver";
    private final String DBURL = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=uft8";
    private final String DBUSER = "root";
    private final String DBPASSWORD = "root";
    private Connection conn = null;
    
    public MysqlUtil(){}
    /**
     * 得到数据库的链接
     *
     * @return  数据库连接
     * 
     */
    public Connection getConn() {
        try {
           // Class.forName(DBDRIVER).newInstance();
        	Class.forName("com.mysql.jdbc.Driver");    //载入mysql的driver
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registCenter","root","root");  //链接数据库
            
        } catch (Exception e) {
        	//System.out.println();
        	e.printStackTrace();
            System.out.println("数据库连接失败");
        }
        return conn;
    }
    
    /**
     * 关闭数据库链接
     * 
     * 最好每次用完都关闭，不然连接池会被占用完
     * 
     */
    public void close() {
        try {
            this.conn.close();  //关闭数据库，最好每次用完都关闭，不然连接池会被占用完
        } catch (Exception e) {
            System.out.println("数据库关闭失败！");
        }
    }
    
    /**
     * 执行sql语句
     *
     * @param sql
     *    要执行的sql语句
     * @return  执行结果
     * 
     */
    public ResultSet executeQuery(String sql) {  //执行sql指令
        ResultSet rs = null;
        try {
            rs = null;
            Connection conn = this.getConn();   //得到数据库链接
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);   //执行sql语句
        } catch (SQLException ex) {
            System.out.println("ִ执行错误");
            ex.printStackTrace();
        }
        return rs;
    }
    
    /**
     * 执行sql语句
     *
     * @param sql
     *    要执行的sql语句
     * 
     */
    public boolean execute(String sql) {  //执行sql语句，不返回结果只返回有没有执行成功
        boolean rs = false;
        try {
            rs = false;
            Connection conn = this.getConn();
            Statement stmt = conn.createStatement();
            rs = stmt.execute(sql);
        } catch (SQLException ex) {
            System.out.println("ִ执行错误");
            ex.printStackTrace();
        }
        return rs;
    }

    /**
     * 跟新数据库数据
     *
     * @param strSql
     *    要执行的sql语句
     * @return  执行结果
     * 
     */
    public boolean executeUpdate(String strSQL) {
        try {
            Connection conn = this.getConn();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(strSQL);
        
        } catch (SQLException ex) {
            System.err.println("连接数据库失败了，异常为" + ex.getMessage());
        }
        return true;
    }
    
    /**
     * 得到所有数据源
     *
     * @return  数据源；
     * 格式为1.数据源1-2.数据源2-....
     * 
     */
    public String getAllDataSource(){      //得到数据源
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataSource order by id");
    	try {
			while (rs.next()) {
				dataSources += rs.getInt("id") + "."+rs.getString("name");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    /**
     * 得到所选数据源的所有字段
     *
     *@param  source所选数据源的id
     * @return  数据源的字段；
     * 格式为1.字段1-2.字段2-....
     * 
     */
    public String getFields(int source){    
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataField where source = "+source+" order by value");
    	try {
			while (rs.next()) {
				dataSources += rs.getInt("value") + "."+rs.getString("tips");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    /**
     * 得到所有数据源的一条数据作为数据样例
     *
     * @return  数据源；
     * 格式为：字段1-字段2-....
     * 
     */
    public String getExamples(int source){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from dataField where source = "+source+" order by value");
    	try {
			while (rs.next()) {
				dataSources +=rs.getString("example");
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    /**
     * 得到所有数据源对应的数据汇总视图
     *
     * @return  数据源；
     * 格式为：视图1-视图2....
     * 
     */
    public String getModels(int source){
    	String dataSources = "";
    	ResultSet rs = executeQuery("select * from views where source = "+source+" order by id");
    	try {
			while (rs.next()) {
				dataSources += rs.getString("name")+"("+rs.getString("src")+")";
				if(!rs.isLast()){
					dataSources += "-";
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSources;
    }
    
    /**
     * 获得webservice列表
     *
     * @return  webservice列表
     * 
     */
    public Vector<WebServiceDbModel> getAllType(){    //获得webservice列表
    	Vector<WebServiceDbModel> allType = new Vector<WebServiceDbModel>();
		ResultSet rs = executeQuery("select * from services");
		try {
			while (rs.next()) {
				WebServiceDbModel model = new WebServiceDbModel();
				model.setId(rs.getInt("id"));
				model.setParentid(rs.getInt("father"));
				model.setName(rs.getString("name"));
				model.setType(rs.getString("type"));
				model.setURL(rs.getString("URL"));
				allType.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allType;
    }
    
    /**
    * 获得所选的webservice的方法列表
    *
    * @param serviceId
    *    所选web service的ID
    * @return  所选的webservice的方法列表
    * 
    */
    public Vector<MethodModel> getMethods(int  serviceId){   //获得所选的webservice的方法列表
    	Vector<MethodModel> methods = new Vector<MethodModel>();
    	ResultSet rs = executeQuery("select * from methods where serviceId="+serviceId);
    	try {
			while (rs.next()) {
				MethodModel model = new MethodModel();
				model.setId(rs.getInt("id"));
				model.setURL(rs.getString("URL"));
				model.setName(rs.getString("name"));
				model.setMethod(rs.getString("method"));
				methods.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return methods;
    }
    
    public static void main(String[] args){    //测试代码
    	MysqlUtil ct = new MysqlUtil();
    	System.out.println(ct.getModels(1));
    	Vector<MethodModel> res = ct.getMethods(1);
    	for(MethodModel model:res){
    		System.out.println("id:"+model.getId()+" URL:"+model.getURL()+" name:"+model.getName()+" method:"+model.getMethod());
    	}
    	Vector<WebServiceDbModel>json = new Vector<WebServiceDbModel>();
		ResultSet rs = ct
				.executeQuery("select * from services");
		try {
			while (rs.next()) {
				WebServiceDbModel model = new WebServiceDbModel();
				model.setId(rs.getInt("id"));
				model.setParentid(rs.getInt("father"));
				model.setName(rs.getString("name"));
				model.setType(rs.getString("type"));
				json.add(model);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ct.close();
		}
		for(WebServiceDbModel model:json){
			System.out.println("id:"+model.getId()+" father:"+model.getParentid()+" name:"+model.getName()+" type:"+model.getType());
		}
    }
}
