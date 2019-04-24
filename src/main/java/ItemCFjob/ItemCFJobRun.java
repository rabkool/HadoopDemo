package ItemCFjob;

import ItemCFstep1.ItemCFMR1;
import ItemCFstep2.ItemCFMR2;
import ItemCFstep3.ItemCFMR3;
import ItemCFstep4.ItemCFMR4;
import ItemCFstep5.ItemCFMR5;

public class ItemCFJobRun {

	public static void main(String[] args) {
		int status1 = -1;
		int status2 = -1;
		int status3 = -1;
		int status4 = -1;
		int status5 = -1;

		status1 = new ItemCFMR1().run();
		if (status1 == 1) {
			System.out.println("1运行成功");
			status2 = new ItemCFMR2().run();
		} else {
			System.out.println("1运行失败");
		}

		if (status2 == 1) {
			System.out.println("2运行成功");
			status3 = new ItemCFMR3().run();
		} else {
			System.out.println("2运行失败");
		}
		if (status3 == 1) {
			System.out.println("3运行成功");
			status4 = new ItemCFMR4().run();
		} else {
			System.out.println("3运行失败");
		}
		if (status4 == 1) {
			System.out.println("4运行成功");
			status5 = new ItemCFMR5().run();
		} else {
			System.out.println("4运行失败");
		}
		if (status5 == 1) {
			System.out.println("5运行成功");

		} else {
			System.out.println("5运行失败");
		}
	}

}
