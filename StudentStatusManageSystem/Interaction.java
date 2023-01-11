import jdbcStudy2.jdbcUtls;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Interaction {
    static Connection connection = null;
    static PreparedStatement pstatement = null;
    static ResultSet resultSet = null;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("------------------------------学生学籍管理系统-----------------------------");
        boolean valid = true;
        while (valid) {
            System.out.println("------------------------------------------------------------------------");
            System.out.println("请输入你要进行的操作(序号)：1.添加学生信息 2.删除学生信息 3.更改学生信息 4.查询学生信息");
            int num = sc.nextInt();
            switch (num) {
                case 1:
                    try {
                        add();
                    } catch (SQLException e) {
                        System.out.println("添加操作有误");
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        delete();
                    } catch (SQLException e) {
                        System.out.println("删除操作有误");
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        update();
                    } catch (SQLException e) {
                        System.out.println("更新操作有误");
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        select();
                    } catch (SQLException e) {
                        System.out.println("查询操作有误");
                        e.printStackTrace();
                    }
                    break;
            }
            System.out.println("是否退出(序号)：1.是 2.否");
            if(sc.nextInt()==1) valid = false;
        }
        System.out.println("---------------------------已退出学生学籍管理系统-----------------------------");
    }
    public static void add() throws SQLException {
        try {
            //用户交互操作
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入学生姓名：");
            String sname = sc.next();
            System.out.println("请输入学生性别：");
            String sgender = sc.next();
            System.out.println("请输入学生生日：");
            String sbirthday = sc.next();
            java.sql.Date sdate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(sbirthday).getTime());
            System.out.println("请输入学生专业:");
            String major = sc.next();
            connection = DatabaseConnection.getConnection();
            //添加学生信息进入学生表
            String sql1 = "insert into Student (name,gender,birthday) values (?,?,?);";
            pstatement = connection.prepareStatement(sql1);
            pstatement.setString(1,sname);
            pstatement.setString(2,sgender);
            pstatement.setDate(3,sdate);
            int i = pstatement.executeUpdate();
            //添加课程信息进入课程表
            String sql6 = "select classID from Class where major = ?";
            pstatement = connection.prepareStatement(sql6);
            pstatement.setString(1,major);
            resultSet = pstatement.executeQuery();
            int j = 1;
            if(!resultSet.next()) {
                String sql2 = "insert into Class (major) values (?);";
                pstatement = connection.prepareStatement(sql2);
                pstatement.setString(1,major);
                j = pstatement.executeUpdate();
            }
            //获取学生ID
            String sql3 = "select studentID from Student where name = ? and birthday = ?";
            pstatement = connection.prepareStatement(sql3);
            pstatement.setString(1,sname);
            pstatement.setDate(2, sdate);
            resultSet = pstatement.executeQuery();
            int sID = 0;
            if(resultSet.next()) {
                sID = resultSet.getInt("studentID");
            }
            //获取课程ID
            String sql4 = "select classID from Class where major = ?";
            pstatement = connection.prepareStatement(sql4);
            pstatement.setString(1,major);
            resultSet = pstatement.executeQuery();
            int cID = 0;
            if(resultSet.next()) {
                cID = resultSet.getInt("classID");
            }
            //添加学生和课程进入关联表
            String sql5 = "insert into StudentToClass (studentID,classID) values (?,?);";
            pstatement = connection.prepareStatement(sql5);
            pstatement.setInt(1,sID);
            pstatement.setInt(2,cID);
            int k = pstatement.executeUpdate();
            if(i>0&&j>0&&k>0) {
                System.out.println("更新成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("日期格式错误");
            e.printStackTrace();
        } finally {
            DatabaseConnection.releaseSource(connection,pstatement,resultSet);
        }
    }
    public static void delete() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入要删除的学生ID");
            int sID = sc.nextInt();
            connection = DatabaseConnection.getConnection();
            String sql0 = "select name from Student where studentID = ?";
            pstatement = connection.prepareStatement(sql0);
            pstatement.setInt(1,sID);
            resultSet = pstatement.executeQuery();
            int j = 1;
            if(!resultSet.next()) {
                System.out.println("没有此ID学生");
            }else {
                String sql1 = "delete from Student where studentID = ?";
                pstatement = connection.prepareStatement(sql1);
                pstatement.setInt(1,sID);
                int i = pstatement.executeUpdate();
                String sql2 = "delete from StudentToClass where studentID = ?";
                pstatement = connection.prepareStatement(sql2);
                pstatement.setInt(1,sID);
                j = pstatement.executeUpdate();
                if(i>0&&j>0) {
                    System.out.println("删除成功！");
                }
                String sql3 = "alter table Student auto_increment = ?";
                pstatement = connection.prepareStatement(sql3);
                pstatement.setInt(1,sID);
                pstatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DatabaseConnection.releaseSource(connection,pstatement,resultSet);
        }
    }
    public static void update() throws SQLException {
        try {
            connection = DatabaseConnection.getConnection();
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入修改学生学号：" );
            int sID = sc.nextInt();
            System.out.println("请输入更改后的学生姓名：");
            String sname = sc.next();
            System.out.println("请输入更改后的学生性别：");
            String sgender = sc.next();
            System.out.println("请输入更改后的学生生日：");
            String sbirthday = sc.next();
            java.sql.Date sdate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(sbirthday).getTime());
            System.out.println("请输入学生更改后的学生专业:");
            String major = sc.next();
            //修改学生表的学生信息
            String sql1 = "update Student set name = ? , gender = ? , birthday = ? where studentID = ?;";
            pstatement = connection.prepareStatement(sql1);
            pstatement.setString(1,sname);
            pstatement.setString(2,sgender);
            pstatement.setDate(3,sdate);
            pstatement.setInt(4,sID);
            int i = pstatement.executeUpdate();
            //修改课程表的信息
            String sql2 = "select classID from Class where major = ?";
            pstatement = connection.prepareStatement(sql2);
            pstatement.setString(1,major);
            resultSet = pstatement.executeQuery();
            int j = 1;
            if(!resultSet.next()) {
                String sql3 = "insert into Class (major) values (?);";
                pstatement = connection.prepareStatement(sql3);
                pstatement.setString(1,major);
                j = pstatement.executeUpdate();
            }
            //获取课程ID
            String sql4 = "select classID from Class where major = ?";
            pstatement = connection.prepareStatement(sql4);
            pstatement.setString(1,major);
            resultSet = pstatement.executeQuery();
            int cID = 0;
            if(resultSet.next()) {
                cID = resultSet.getInt("classID");
            }
            //删除关联表原学生和课程
            String sql5 = "delete from StudentToClass where studentID = ?";
            pstatement = connection.prepareStatement(sql5);
            pstatement.setInt(1,sID);
            pstatement.executeUpdate();
            //修改关联表的学生和课程
            String sql6 = "insert into StudentToClass (studentID,classID) values (?,?);";
            pstatement = connection.prepareStatement(sql6);
            pstatement.setInt(1,sID);
            pstatement.setInt(2,cID);
            int k = pstatement.executeUpdate();
            if(i>0&&j>0&&k>0) {
                System.out.println("更新成功");
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }finally {
            DatabaseConnection.releaseSource(connection,pstatement,resultSet);
        }
    }
    public static void select() throws SQLException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入查询学生的ID：");
            int sid = sc.nextInt();
            connection = DatabaseConnection.getConnection();
            String sql1 = "select * from Student where studentID = ?";
            pstatement = connection.prepareStatement(sql1);
            pstatement.setInt(1,sid);
            resultSet = pstatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("学生ID："+resultSet.getInt("studentID"));
                System.out.println("学生姓名："+resultSet.getString("name"));
                System.out.println("学生性别："+resultSet.getString("gender"));
                System.out.println("学生生日："+resultSet.getString("birthday"));
                String sql2 = "select * from StudentToClass where studentID = ?";
                pstatement = connection.prepareStatement(sql2);
                pstatement.setInt(1,sid);
                resultSet = pstatement.executeQuery();
                int cid = 0;
                if(resultSet.next()) {
                    cid = resultSet.getInt("classID");
                    System.out.println("学生班级："+cid);
                }
                String sql3 = "select * from Class where classID = ?";
                pstatement = connection.prepareStatement(sql3);
                pstatement.setInt(1,cid);
                resultSet = pstatement.executeQuery();
                if(resultSet.next()) {
                    System.out.println("学生专业："+resultSet.getString("major"));
                    System.out.println("学生登记时间："+resultSet.getDate("entryTime"));
                }
            }else {
                System.out.println("查询失败，没有此ID的学生");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DatabaseConnection.releaseSource(connection,pstatement,resultSet);
        }
    }
}
