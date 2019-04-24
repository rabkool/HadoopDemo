package UserCFjob;

import UserCFstep1.UserCFMR1;
import UserCFstep2.UserCFMR2;
import UserCFstep3.UserCFMR3;
import UserCFstep4.UserCFMR4;
import UserCFstep5.UserCFMR5;
/**
 * 基于用户过滤算法
 * @author 90595
 *
 */
public class UserCFJobRun {

	public static void main(String[] args) {
		int status1 = -1;
		int status2 = -1;
		int status3 = -1;
		int status4 = -1;
		int status5 = -1;
		
		 status1 = new UserCFMR1().run();
		 if(status1 == 1) {
			 System.out.println("1运行成功");
			 status2 = new UserCFMR2().run();
		 }else {
			 System.out.println("1运行失败");
		 }
		 
		 if(status2 == 1) {
			 System.out.println("2运行成功");
			 status3 = new UserCFMR3().run();
		 }else {
			 System.out.println("2运行失败");
		 }
		 if(status3 == 1) {
			 System.out.println("3运行成功");
			 status4 = new UserCFMR4().run();
		 }else {
			 System.out.println("3运行失败");
		 }
		 if(status4 == 1) {
			 System.out.println("4运行成功");
			 status5 = new UserCFMR5().run();
		 }else {
			 System.out.println("4运行失败");
		 }
		 if(status5 == 1) {
			 System.out.println("5运行成功");
			
		 }else {
			 System.out.println("5运行失败");
		 }
	}

}
